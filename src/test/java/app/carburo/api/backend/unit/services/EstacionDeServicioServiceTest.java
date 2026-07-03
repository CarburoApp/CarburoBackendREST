package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.entities.*;
import app.carburo.api.backend.entities.enums.Margen;
import app.carburo.api.backend.entities.enums.Remision;
import app.carburo.api.backend.entities.enums.Venta;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.*;
import app.carburo.api.backend.services.EstacionDeServicioService;
import app.carburo.api.backend.services.PrecioStateService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - EstacionDeServicioService")
class EstacionDeServicioServiceTest {

	@Mock
	private EstacionDeServicioRepository eessRepository;
	@Mock
	private PrecioCombustibleRepository precioRepository;
	@Mock
	private ComunidadAutonomaRepoository caRepository;
	@Mock
	private ProvinciaRepository provinciaRepository;
	@Mock
	private MunicipioRepository municipioRepository;
	@Mock
	private PrecioStateService precioStateService;

	private EstacionDeServicioService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new EstacionDeServicioService(eessRepository, precioRepository,
												caRepository, provinciaRepository,
												municipioRepository, precioStateService);
		// Configuración por defecto común para la fecha del estado del sistema
		when(precioStateService.getBestDate()).thenReturn(LocalDate.now());
	}

	// =========================================================================
	// ANALÍTICAS Y ESTADÍSTICAS
	// =========================================================================
	@Nested
	@DisplayName("Analíticas e Estadísticas Globales")
	class EstadisticasTests {

		@Test
		@DisplayName("Debe lanzar BadRequestException si la fecha solicitada es futura")
		void shouldThrowWhenFechaIsFuture() {
			LocalDate futura = LocalDate.now().plusDays(5);
			assertThrows(BadRequestException.class,
						 () -> service.getEstadisticasCombustibles(futura));
		}

		@Test
		@DisplayName(
				"Debe lanzar BadRequestException si la fecha es anterior a la FECHA_MINIMA"
		)
		void shouldThrowWhenFechaIsBeforeMinimum() {
			LocalDate antigua = PrecioCombustible.FECHA_MINIMA.minusDays(1);
			assertThrows(BadRequestException.class,
						 () -> service.getEstadisticasCombustibles(antigua));
		}
	}

	// =========================================================================
	// CONSULTAS POR ID Y ESCENARIOS GEOGRÁFICOS
	// =========================================================================
	@Nested
	@DisplayName("Búsqueda por IDs y Localización")
	class FindByIdAndCoordinatesTests {

		@Test
		@DisplayName("Debe lanzar ResourceNotFoundException si la EESS no existe")
		void shouldThrowWhenEessDoesNotExist() {
			when(eessRepository.existsById(999)).thenReturn(false);
			assertThrows(ResourceNotFoundException.class,
						 () -> service.getEstacionDeServicioDtoById(999));
		}

		@Test
		@DisplayName(
				"Debe calcular distancias e inyectar precios al pedir una estación por ID y Coordenadas"
		)
		void shouldReturnEessDtoWithDistanceAndPrecios() {
			int id = 1;
			double lat = 43.53;
			double lon = -5.66;
			EstacionDeServicio eess = mockEess(id, "REPSOL");

			when(eessRepository.existsById(id)).thenReturn(true);
			when(eessRepository.findEstacionDeServicioById(id)).thenReturn(eess);
			when(eessRepository.findDistanciaById(id, lat, lon)).thenReturn(450L);

			EstacionDeServicioDto result = service.getEstacionDeServicioDtoById(id, lat,
																				lon);

			assertNotNull(result);
			assertEquals("REPSOL", result.rotulo());
			assertEquals(450L, result.distancia_metros());
			verify(precioRepository, times(1)).findPreciosHoyByListadoIdEstaciones(
					anyList(), any());
		}

		@Test
		@DisplayName(
				"Debe lanzar BadRequestException si los IDs superan las 100 unidades en lote"
		)
		void shouldThrowWhenIdsListIsTooLarge() {
			List<Integer> masDeCienIds = Collections.nCopies(101, 1);
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoByIdsSinPrecios(
								 masDeCienIds));
		}

		@Test
		@DisplayName(
				"Debe lanzar BadRequestException si la lista de IDs viene vacía o nula"
		)
		void shouldThrowWhenIdsListIsEmpty() {
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoByIdsSinPrecios(null));
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoByIdsSinPrecios(
								 List.of()));
		}

		@Test
		@DisplayName(
				"Debe validar rangos de coordenadas matemáticamente de forma estricta"
		)
		void shouldValidateCoordinatesRanges() {
			// Latitud errónea (> 90 o < -90)
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoCercanas(95.0, 0.0, 5));
			// Longitud errónea (> 180 o < -180)
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoCercanas(40.0, -190.0,
																		  5));
			// Coordenadas NaN
			assertThrows(BadRequestException.class,
						 () -> service.getEstacionesDeServicioDtoCercanas(Double.NaN, 0.0,
																		  5));
		}

		@Test
		@DisplayName(
				"Debe aplicar topes correctos (Límit Máximo) al consultar estaciones cercanas"
		)
		void shouldCapCercanasLimit() throws BadRequestException {
			double lat = 40.0;
			double lon = -3.0;
			EstacionDeServicio eess = mockEess(1, "PLENOIL");

			// Si el cliente pide 50, se debe capar internamente a MAX_ESTACIONES_CERCANAS (10)
			when(eessRepository.findEstacionDeServicioMasCercana(lat, lon,
																 10)).thenReturn(
					List.of(eess));

			List<EstacionDeServicioDto> result = service.getEstacionesDeServicioDtoCercanas(
					lat, lon, 50);

			assertNotNull(result);
			verify(eessRepository).findEstacionDeServicioMasCercana(lat, lon, 10);
		}
	}

	// =========================================================================
	// FILTROS GEOGRÁFICOS (CCAA, PROVINCIA, MUNICIPIO)
	// =========================================================================
	@Nested
	@DisplayName("Filtros Políticos y Administrativos")
	class RegionFiltersTests {

		@Test
		@DisplayName(
				"Debe filtrar por Comunidad Autónoma e inyectar precios agrupados en lote"
		)
		void shouldFilterByComunidadAutonomaAndGroupPrecios() {
			short idCa = 1;
			EstacionDeServicio e1 = mockEess(10, "SHELL");
			EstacionDeServicio e2 = mockEess(20, "BP");

			when(caRepository.existsById(idCa)).thenReturn(true);
			when(eessRepository.findEstacionDeServicioByComunidadAutonoma(
					idCa)).thenReturn(List.of(e1, e2));

			List<EstacionDeServicioDto> result = service.getEstacionesDeServicioDtoByComunidadAutonoma(
					idCa);

			assertEquals(2, result.size());
			// Verifica que se optimizaron los accesos pidiendo todos los precios de una tacada con la lista de IDs [10, 20]
			verify(precioRepository, times(1)).findPreciosHoyByListadoIdEstaciones(
					List.of(10, 20), precioStateService.getBestDate());
		}

		@Test
		@DisplayName(
				"Debe lanzar ResourceNotFoundException si la provincia no existe al filtrar EESS"
		)
		void shouldThrowWhenProvinciaNotFound() {
			short idProvincia = 99;
			when(provinciaRepository.existsById(idProvincia)).thenReturn(false);
			assertThrows(ResourceNotFoundException.class,
						 () -> service.getEstacionesDeServicioDtoByProvincia(
								 idProvincia));
		}
	}

	// =========================================================================
	// GESTIÓN DE PRECIOS E HISTÓRICOS
	// =========================================================================
	@Nested
	@DisplayName("Históricos de Precios")
	class PreciosHistoricosTests {

		@Test
		@DisplayName("Debe acotar el número máximo de días a consultar del histórico")
		void shouldCapMaxHistoryDays() throws BadRequestException {
			int id = 5;
			when(eessRepository.existsById(id)).thenReturn(true);

			// Si piden 100 días, la regla de negocio debe rebajarlo al límite estipulado (MAX_DIAS = 30)
			service.getPreciosDeCombustiblesDtoByEstacionDeServicioId(id, 100);

			LocalDate hoy = LocalDate.now();
			LocalDate fechaInicioEsperada = hoy.minusDays(
					29); // 30 días incluyendo hoy -> (30 - 1)

			verify(precioRepository).findByEstacion_IdAndId_FechaBetween(id,
																		 fechaInicioEsperada,
																		 hoy);
		}

		@Test
		@DisplayName(
				"Debe lanzar BadRequestException si el parámetro 'dias' es menor o igual a cero"
		)
		void shouldThrowWhenDaysLessThanOne() {
			int id = 5;
			when(eessRepository.existsById(id)).thenReturn(true);
			assertThrows(BadRequestException.class,
						 () -> service.getPreciosDeCombustiblesDtoByEstacionDeServicioId(
								 id, 0));
		}
	}

	// =========================================================================
	// MOCK ENGINE UTILS (Aislamiento de Entidades Complejas)
	// =========================================================================
	private EstacionDeServicio mockEess(int id, String rotulo) {
		EstacionDeServicio eess = mock(EstacionDeServicio.class);
		Municipio m = mock(Municipio.class);
		Provincia p = mock(Provincia.class);
		Coordenada coord = new Coordenada(40.0, -3.0);

		when(eess.getId()).thenReturn(id);
		when(eess.getRotulo()).thenReturn(rotulo);
		when(eess.getMunicipio()).thenReturn(m);
		when(m.getId()).thenReturn((short) 1);
		when(eess.getProvincia()).thenReturn(p);
		when(p.getId()).thenReturn((short) 1);
		when(eess.getCoordenada()).thenReturn(coord);
		when(eess.getMargen()).thenReturn(Margen.DERECHO);
		when(eess.getRemision()).thenReturn(Remision.DM);
		when(eess.getVenta()).thenReturn(Venta.PUBLICA);
		when(eess.getCombustiblesDisponibles()).thenReturn(Set.of());

		return eess;
	}
}