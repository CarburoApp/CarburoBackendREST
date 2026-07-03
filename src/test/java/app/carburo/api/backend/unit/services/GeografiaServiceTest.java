package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.ComunidadAutonomaDto;
import app.carburo.api.backend.dto.MunicipioDto;
import app.carburo.api.backend.dto.ProvinciaDto;
import app.carburo.api.backend.entities.ComunidadAutonoma;
import app.carburo.api.backend.entities.Municipio;
import app.carburo.api.backend.entities.Provincia;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.ComunidadAutonomaRepoository;
import app.carburo.api.backend.repositories.MunicipioRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import app.carburo.api.backend.services.ComunidadAutonomaService;
import app.carburo.api.backend.services.MunicipioService;
import app.carburo.api.backend.services.ProvinciaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - Servicios Geográficos")
class GeografiaServiceTest {

	// Mocks de Repositorios
	@Mock
	private ComunidadAutonomaRepoository caRepository;
	@Mock
	private ProvinciaRepository provinciaRepository;
	@Mock
	private MunicipioRepository municipioRepository;

	// Servicios bajo prueba
	private ComunidadAutonomaService caService;
	private MunicipioService municipioService;
	private ProvinciaService provinciaService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		// Instanciamos los servicios inyectando de forma manual los mocks
		caService        = new ComunidadAutonomaService(caRepository);
		municipioService = new MunicipioService(municipioRepository, provinciaRepository);
		provinciaService = new ProvinciaService(provinciaRepository);
	}

	// ==========================================
	// TESTS PARA COMUNIDAD AUTONOMA SERVICE
	// ==========================================
	@Nested
	@DisplayName("ComunidadAutonomaService Tests")
	class ComunidadAutonomaServiceTests {

		@Test
		@DisplayName("Debe mapear comunidades a DTO plano sin relaciones")
		void shouldReturnComunidadesAutonomasDto() {
			ComunidadAutonoma ca = new ComunidadAutonoma((short) 1, "Galicia",
														 (short) 11);
			when(caRepository.findAll()).thenReturn(List.of(ca));

			List<ComunidadAutonomaDto> result = caService.getComunidadesAutonomasDto();

			assertEquals(1, result.size());
			assertEquals("Galicia", result.getFirst().denominacion());
			assertNull(result.getFirst().provincias());
		}

		@Test
		@DisplayName("Debe mapear comunidades incluyendo árbol completo de provincias")
		void shouldReturnComunidadesAutonomasDtoFullNested() {
			ComunidadAutonoma ca = new ComunidadAutonoma((short) 2, "Aragón", (short) 22);

			// Mockeamos la relación interna para evitar LazyInitializationException en el stream del DTO
			Provincia provMock = mock(Provincia.class);
			when(provMock.getId()).thenReturn((short) 50);
			when(provMock.getDenominacion()).thenReturn("Zaragoza");
			when(provMock.getComunidadAutonoma()).thenReturn(ca);
			when(provMock.getMunicipios()).thenReturn(
					Set.of()); // Lista de municipios vacía para el mapa

			ca.setProvincias(Set.of(provMock));
			when(caRepository.findAll()).thenReturn(List.of(ca));

			List<ComunidadAutonomaDto> result = caService.getComunidadesAutonomasDtoFullNested();

			assertEquals(1, result.size());
			assertNotNull(result.getFirst().provincias());
			assertEquals(1, result.getFirst().provincias().size());
			assertEquals("Zaragoza",
						 result.getFirst().provincias().getFirst().denominacion());
		}

		@Test
		@DisplayName("Debe buscar comunidad por ID y retornar Optional")
		void shouldFindComunidadById() {
			ComunidadAutonoma ca = new ComunidadAutonoma((short) 3, "Asturias",
														 (short) 33);
			when(caRepository.findComunidadAutonomaById((short) 3)).thenReturn(
					Optional.of(ca));

			Optional<ComunidadAutonoma> result = caService.getComunidadAutonomaById(
					(short) 3);

			assertTrue(result.isPresent());
			assertEquals("Asturias", result.get().getDenominacion());
		}
	}

	// ==========================================
	// TESTS PARA MUNICIPIO SERVICE
	// ==========================================
	@Nested
	@DisplayName("MunicipioService Tests")
	class MunicipioServiceTests {

		@Test
		@DisplayName("Debe mapear todos los municipios a DTO de forma plana")
		void shouldReturnAllMunicipiosDto() {
			Provincia provMock = mock(Provincia.class);
			when(provMock.getId()).thenReturn((short) 33);
			Municipio muni = new Municipio((short) 1, "Gijón", (short) 101, provMock);

			when(municipioRepository.findAll()).thenReturn(List.of(muni));

			List<MunicipioDto> result = municipioService.getMunicipiosDTO();

			assertEquals(1, result.size());
			assertEquals("Gijón", result.getFirst().denominacion());
			assertEquals(33, result.getFirst().id_provincia());
		}

		@Test
		@DisplayName("Debe retornar municipios filtrados por provincia si esta existe")
		void shouldGetMunicipiosByProvincia() {
			short idProvincia = 33;
			Provincia provMock = mock(Provincia.class);
			when(provMock.getId()).thenReturn(idProvincia);
			Municipio muni = new Municipio((short) 2, "Oviedo", (short) 102, provMock);

			when(provinciaRepository.existsById(idProvincia)).thenReturn(true);
			when(municipioRepository.findMunicipioByProvincia(idProvincia)).thenReturn(
					List.of(muni));

			List<MunicipioDto> result = municipioService.getMunicipiosDTOByProvincia(
					idProvincia);

			assertEquals(1, result.size());
			assertEquals("Oviedo", result.getFirst().denominacion());
		}

		@Test
		@DisplayName(
				"Debe lanzar ResourceNotFoundException si la provincia no existe al buscar municipios"
		)
		void shouldThrowWhenProvinciaNotFoundInMunicipios() {
			short idProvinciaInexistente = 99;
			when(provinciaRepository.existsById(idProvinciaInexistente)).thenReturn(
					false);

			assertThrows(ResourceNotFoundException.class,
						 () -> municipioService.getMunicipiosDTOByProvincia(
								 idProvinciaInexistente));

			verify(municipioRepository, never()).findMunicipioByProvincia(anyShort());
		}

		@Test
		@DisplayName("Debe retornar municipios con EESS de una provincia válida")
		void shouldGetMunicipiosByProvinciaConEess() {
			short idProvincia = 28; // Madrid
			Provincia provMock = mock(Provincia.class);
			when(provMock.getId()).thenReturn(idProvincia);
			Municipio muni = new Municipio((short) 10, "Alcalá de Henares", (short) 200,
										   provMock);

			when(provinciaRepository.existsById(idProvincia)).thenReturn(true);
			when(municipioRepository.findMunicipioByProvinciaConEESS(
					idProvincia)).thenReturn(List.of(muni));

			List<MunicipioDto> result = municipioService.getMunicipiosDTOByProvinciaConEESS(
					idProvincia);

			assertEquals(1, result.size());
			assertEquals("Alcalá de Henares", result.getFirst().denominacion());
		}
	}

	// ==========================================
	// TESTS PARA PROVINCIA SERVICE
	// ==========================================
	@Nested
	@DisplayName("ProvinciaService Tests")
	class ProvinciaServiceTests {

		@Test
		@DisplayName("Debe retornar todas las provincias mapeadas a DTO")
		void shouldGetProvinciasDto() {
			ComunidadAutonoma caMock = mock(ComunidadAutonoma.class);
			when(caMock.getId()).thenReturn((short) 1);

			Provincia prov = new Provincia();
			prov.setId((short) 15);
			prov.setDenominacion("A Coruña");
			prov.setComunidadAutonoma(caMock);

			when(provinciaRepository.findAll()).thenReturn(List.of(prov));

			List<ProvinciaDto> result = provinciaService.getProvinciasDTO();

			assertEquals(1, result.size());
			assertEquals("A Coruña", result.getFirst().denominacion());
			assertEquals(1, result.getFirst().id_comunidad_autonoma());
		}

		@Test
		@DisplayName(
				"Debe devolver las provincias ordenadas del repositorio de forma íntegra"
		)
		void shouldGetProvinciasOrderedByDenominacion() {
			Provincia p1 = mock(Provincia.class);
			Provincia p2 = mock(Provincia.class);
			when(provinciaRepository.findAllOrderByDenominacion()).thenReturn(
					List.of(p1, p2));

			List<Provincia> result = provinciaService.getProvinciasOrderByDenominacion();

			assertEquals(2, result.size());
			verify(provinciaRepository, times(1)).findAllOrderByDenominacion();
		}

		@Test
		@DisplayName("Debe retornar provincias filtradas por Comunidad Autónoma")
		void shouldGetProvinciasByComunidadAutonoma() {
			ComunidadAutonoma ca = new ComunidadAutonoma((short) 5, "Canarias",
														 (short) 55);
			Provincia p = mock(Provincia.class);

			when(provinciaRepository.findAllByComunidadAutonoma(ca)).thenReturn(
					List.of(p));

			List<Provincia> result = provinciaService.getProvinciasByComunidadAutonoma(
					ca);

			assertEquals(1, result.size());
			assertSame(p, result.getFirst());
		}

		@Test
		@DisplayName(
				"Debe lanzar IllegalArgumentException si la Comunidad Autónoma provista es nula"
		)
		void shouldThrowWhenComunidadAutonomaIsNull() {
			assertThrows(IllegalArgumentException.class,
						 () -> provinciaService.getProvinciasByComunidadAutonoma(null));

			verify(provinciaRepository, never()).findAllByComunidadAutonoma(any());
		}
	}
}