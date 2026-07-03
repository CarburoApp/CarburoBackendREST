package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.RepostajeDto;
import app.carburo.api.backend.entities.*;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.repositories.*;
import app.carburo.api.backend.services.RepostajeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - RepostajeService (Strict DTO & Immutable ID)")
class RepostajeServiceTest {

	@Mock
	private RepostajeRepository repostajeRepository;
	@Mock
	private VehiculoRepository vehiculoRepository;
	@Mock
	private VehiculoUsuarioRepository vehiculoUsuarioRepository;
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private CombustibleRepository combustibleRepository;
	@Mock
	private EstacionDeServicioRepository estacionDeServicioRepository;

	private RepostajeService service;
	private final UUID userId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new RepostajeService(repostajeRepository, vehiculoRepository,
									   vehiculoUsuarioRepository, usuarioRepository,
									   combustibleRepository,
									   estacionDeServicioRepository);
	}

	// =========================================================================
	// CONSULTAS / LECTURA
	// =========================================================================
	@Nested
	@DisplayName("Métodos de Consulta")
	class GetRepostajesTests {

		@Test
		@DisplayName(
				"Debe retornar un RepostajeDto válido respetando firmas de OffsetDateTime"
		)
		void shouldReturnRepostajeDto() {
			int idVehiculo = 1;
			int idRepostaje = 10;
			Vehiculo vehiculoMock = mock(Vehiculo.class);
			when(vehiculoMock.getId()).thenReturn(idVehiculo);

			Repostaje repostaje = mockRepostajeEntidadCompleta(idRepostaje, vehiculoMock);

			// Mock de relación básico
			VehiculoUsuario vuMock = mock(VehiculoUsuario.class);
			when(vuMock.isPropietario()).thenReturn(true);

			when(vehiculoRepository.findById(idVehiculo)).thenReturn(
					Optional.of(vehiculoMock));
			when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(userId,
																		  idVehiculo)).thenReturn(
					Optional.of(vuMock));
			when(repostajeRepository.findById(idRepostaje)).thenReturn(
					Optional.of(repostaje));

			RepostajeDto result = service.getRepostaje(userId, idVehiculo, idRepostaje);

			assertNotNull(result);
			assertEquals(idRepostaje, result.id());
			assertEquals(idVehiculo, result.id_vehiculo());
			assertInstanceOf(OffsetDateTime.class, result.fecha_repostaje());
		}

		@Test
		@DisplayName(
				"Debe lanzar UnauthorizedException si el repostaje pertenece a otro vehículo"
		)
		void shouldThrowWhenRepostajeDoesNotBelongToVehiculo() {
			int idVehiculoReal = 1;
			int idVehiculoFalso = 2;
			int idRepostaje = 10;

			Vehiculo vReal = mock(Vehiculo.class);
			when(vReal.getId()).thenReturn(idVehiculoReal);
			Vehiculo vFalso = mock(Vehiculo.class);
			when(vFalso.getId()).thenReturn(idVehiculoFalso);

			Repostaje repostaje = mockRepostajeEntidadCompleta(idRepostaje, vReal);

			VehiculoUsuario vuMock = mock(VehiculoUsuario.class);
			when(vuMock.isPropietario()).thenReturn(true);

			when(vehiculoRepository.findById(idVehiculoFalso)).thenReturn(
					Optional.of(vFalso));
			when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(userId,
																		  idVehiculoFalso)).thenReturn(
					Optional.of(vuMock));
			when(repostajeRepository.findById(idRepostaje)).thenReturn(
					Optional.of(repostaje));

			assertThrows(UnauthorizedException.class,
						 () -> service.getRepostaje(userId, idVehiculoFalso,
													idRepostaje));
		}
	}

	// =========================================================================
	// CREACIÓN DE REPOSTAJES
	// =========================================================================
	@Nested
	@DisplayName("Creación de Repostajes")
	class CreateRepostajeTests {

		private RepostajeDto validDto;
		private Vehiculo vehiculoMock;

		@BeforeEach
		void setUp() {
			validDto     = new RepostajeDto(0,                           // id
											5,                           // id_vehiculo
											(short) 1,                   // id_combustible
											100,// id_estacion_de_servicio
											userId,// uuid_usuario_creador
											OffsetDateTime.now(),
											// fecha_repostaje
											OffsetDateTime.now(),        // fecha_registro
											45.5,                        // cantidad
											1.559,                       // coste_unitario
											1000.0,
											// odometro_inicial
											1050.0,                      // odometro_final
											true,                        // deposito_lleno
											"Nota válida"                // nota
			);
			vehiculoMock = mock(Vehiculo.class);
			GrupoCombustible grupoMock = mock(GrupoCombustible.class);
			when(vehiculoMock.getId()).thenReturn(5);
			when(vehiculoMock.getGrupoCombustible()).thenReturn(grupoMock);
			when(vehiculoMock.getOdometroActual()).thenReturn(BigDecimal.valueOf(900.0));
			when(grupoMock.getId()).thenReturn((short) 1);
		}

		@Test
		@DisplayName("Debe crear repostaje con éxito e inyectar el ID simulando la DB")
		void shouldCreateRepostajeSuccessfully() {
			Usuario usuario = mock(Usuario.class);
			when(usuario.getUuid()).thenReturn(userId);
			Combustible combustible = mock(Combustible.class);
			EstacionDeServicio estacion = mock(EstacionDeServicio.class);

			// SOLUCIÓN: Forzar a que el mock de la relación devuelva true en isPropietario()
			VehiculoUsuario relacionPropietarioMock = mock(VehiculoUsuario.class);
			when(relacionPropietarioMock.isPropietario()).thenReturn(true);

			when(combustible.getIdGrupoCombustible()).thenReturn((short) 1);
			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuario));
			when(vehiculoRepository.findById(5)).thenReturn(Optional.of(vehiculoMock));

			// Entregamos el mock configurado como propietario
			when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(userId,
																		  5)).thenReturn(
					Optional.of(relacionPropietarioMock));

			when(combustibleRepository.findById((short) 1)).thenReturn(
					Optional.of(combustible));
			when(estacionDeServicioRepository.findById(100)).thenReturn(
					Optional.of(estacion));

			when(repostajeRepository.existsSolapamientoOdomatros(eq(5), any(), any(),
																 any())).thenReturn(
					false);

			when(repostajeRepository.save(any(Repostaje.class))).thenAnswer(
					invocation -> {
						Repostaje entityToSave = invocation.getArgument(0);
						ReflectionTestUtils.setField(entityToSave, "id", 77);
						return entityToSave;
					});

			int resultId = service.createRepostaje(userId, 5, validDto);

			assertEquals(77, resultId);
			verify(vehiculoMock).setOdometroActual(1050.0);
			verify(vehiculoRepository).save(vehiculoMock);
		}
	}

	// =========================================================================
	// EDICIÓN / ACTUALIZACIÓN
	// =========================================================================
	@Nested
	@DisplayName("Actualización de Datos e Inmutabilidad")
	class UpdateRepostajeTests {

		@Test
		@DisplayName(
				"Debe evitar el chequeo de solapamiento si los kms coinciden con el histórico"
		)
		void shouldNotCheckOverlapWhenOdometerMatches() {
			Vehiculo v = mock(Vehiculo.class);
			when(v.getId()).thenReturn(1);
			when(v.getOdometroActual()).thenReturn(BigDecimal.valueOf(1500.0));
			GrupoCombustible g = mock(GrupoCombustible.class);
			when(g.getId()).thenReturn((short) 1);
			when(v.getGrupoCombustible()).thenReturn(g);

			VehiculoUsuario vu = mock(VehiculoUsuario.class);
			when(vu.isPropietario()).thenReturn(true);
			when(vehiculoRepository.findById(1)).thenReturn(Optional.of(v));
			when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(userId,
																		  1)).thenReturn(
					Optional.of(vu));

			Repostaje oldRepostaje = mockRepostajeEntidadCompleta(10, v);
			when(oldRepostaje.getOdometroInicial()).thenReturn(BigDecimal.valueOf(500.0));
			when(oldRepostaje.getOdometroFinal()).thenReturn(BigDecimal.valueOf(600.0));

			when(repostajeRepository.findById(10)).thenReturn(Optional.of(oldRepostaje));

			RepostajeDto updateDto = new RepostajeDto(10, 1, (short) 1, 100, userId,
													  OffsetDateTime.now(),
													  OffsetDateTime.now(), 40.0, 1.45,
													  500.0, 600.0, true, "Modificado");

			service.updateRepostaje(userId, 1, 10, updateDto);

			verify(repostajeRepository, never()).existsSolapamientoOdomatros(anyInt(),
																			 any(), any(),
																			 any());
			verify(repostajeRepository, times(1)).save(oldRepostaje);
		}
	}

	// =========================================================================
	// MOCK UTILS ENGINE
	// =========================================================================
	private Repostaje mockRepostajeEntidadCompleta(int id, Vehiculo vehiculo) {
		Repostaje r = mock(Repostaje.class);

		when(r.getId()).thenReturn(id);
		when(r.getVehiculo()).thenReturn(vehiculo);
		when(r.getCantidad()).thenReturn(BigDecimal.valueOf(40.0));
		when(r.getCosteUnitario()).thenReturn(BigDecimal.valueOf(1.40));
		when(r.getOdometroInicial()).thenReturn(BigDecimal.valueOf(0.0));
		when(r.getOdometroFinal()).thenReturn(BigDecimal.valueOf(500.0));
		when(r.getFechaRepostaje()).thenReturn(OffsetDateTime.now());
		when(r.getFechaRegistro()).thenReturn(OffsetDateTime.now());
		when(r.getDepositoLleno()).thenReturn(true);
		when(r.getNota()).thenReturn("Comentario mock");

		Combustible c = mock(Combustible.class);
		when(c.getId()).thenReturn((short) 1);
		EstacionDeServicio es = mock(EstacionDeServicio.class);
		when(es.getId()).thenReturn(1);
		Usuario u = mock(Usuario.class);
		when(u.getUuid()).thenReturn(userId);

		when(r.getCombustible()).thenReturn(c);
		when(r.getEstacionDeServicio()).thenReturn(es);
		when(r.getUsuario()).thenReturn(u);

		return r;
	}
}