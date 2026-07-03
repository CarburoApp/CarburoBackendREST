package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.UsuarioDto;
import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.Provincia;
import app.carburo.api.backend.entities.Usuario;
import app.carburo.api.backend.exceptions.InvalidUsuarioDataException;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.exceptions.UsuarioAlreadyExistsException;
import app.carburo.api.backend.repositories.CombustibleRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import app.carburo.api.backend.repositories.UsuarioRepository;
import app.carburo.api.backend.services.EstacionDeServicioService;
import app.carburo.api.backend.services.UsuarioService;
import app.carburo.api.backend.services.queryServices.CombustibleQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - UsuarioService (Comprehensive Suite)")
class UsuarioServiceTest {

	@Mock
	private EstacionDeServicioService estacionDeServicioService;
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private ProvinciaRepository provinciaRepository;
	@Mock
	private CombustibleRepository combustibleRepository;
	@Mock
	private CombustibleQueryService combustibleQueryService;

	private UsuarioService service;
	private final UUID userId = UUID.randomUUID();

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		service = new UsuarioService(
				usuarioRepository,
				estacionDeServicioService,
				provinciaRepository,
				combustibleRepository,
				combustibleQueryService
		);
	}

	// =========================================================================
	// LECTURAS Y COMPROBACIONES BÁSICAS
	// =========================================================================
	@Nested
	@DisplayName("Consultas y Existencia")
	class QueryAndExistenceTests {

		@Test
		@DisplayName("getUsuario - Debe mapear correctamente a UsuarioDto si existe")
		void shouldReturnUsuarioDtoWhenUserExists() {
			Usuario usuarioMock = mock(Usuario.class);
			Provincia provinciaMock = mock(Provincia.class);

			when(usuarioMock.getUuid()).thenReturn(userId);
			when(usuarioMock.getProvinciaFavorita()).thenReturn(provinciaMock);
			when(provinciaMock.getId()).thenReturn((short) 33);

			// SOLUCIÓN NPE: Mockear las colecciones internas requeridas por UsuarioDto.from()
			when(usuarioMock.getCombustiblesFavoritos()).thenReturn(new HashSet<>());
			when(usuarioMock.getEessFavoritas()).thenReturn(new HashSet<>());

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));

			UsuarioDto result = service.getUsuario(userId);

			assertNotNull(result);
			assertEquals(userId, result.uuid());
			assertEquals((short) 33, result.id_provincia_favorita());
			assertNotNull(result.ids_combustibles_favoritos());
			assertNotNull(result.ids_estaciones_de_servicio_favoritas());
		}

		@Test
		@DisplayName("getUsuario - Debe lanzar ResourceNotFoundException si no existe")
		void shouldThrowNotFoundWhenUserDoesNotExist() {
			when(usuarioRepository.findById(userId)).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> service.getUsuario(userId));
		}

		@Test
		@DisplayName("existsUsuario - Debe retornar true si el repositorio confirma existencia")
		void shouldReturnTrueWhenUserExistsInDb() {
			when(usuarioRepository.existsById(userId)).thenReturn(true);
			assertTrue(service.existsUsuario(userId));
		}

		@Test
		@DisplayName("existsUsuario - Debe retornar false si el repositorio no lo encuentra")
		void shouldReturnFalseWhenUserDoesNotExistInDb() {
			when(usuarioRepository.existsById(userId)).thenReturn(false);
			assertFalse(service.existsUsuario(userId));
		}
	}

	// =========================================================================
	// FAVORITOS (PROVINCIAS / COMBUSTIBLES / ESTACIONES)
	// =========================================================================
	@Nested
	@DisplayName("Gestión de Favoritos (Lecturas)")
	class FavoritesQueriesTests {

		@Test
		@DisplayName("getProvinciaFavorita - Debe retornar el ID de la provincia favorita")
		void shouldReturnFavoriteProvinciaId() {
			Usuario usuarioMock = mock(Usuario.class);
			Provincia provinciaMock = mock(Provincia.class);

			when(provinciaMock.getId()).thenReturn((short) 12);
			when(usuarioMock.getProvinciaFavorita()).thenReturn(provinciaMock);
			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));

			assertEquals((short) 12, service.getProvinciaFavorita(userId));
		}

		@Test
		@DisplayName("getCombustiblesFavoritos - Debe retornar el Set de IDs mapeados")
		void shouldReturnSetOfFavoriteCombustibleIds() {
			Usuario usuarioMock = mock(Usuario.class);
			Combustible c1 = mock(Combustible.class);
			Combustible c2 = mock(Combustible.class);

			when(c1.getId()).thenReturn((short) 1);
			when(c2.getId()).thenReturn((short) 2);
			Set<Combustible> combustibles = new HashSet<>(Arrays.asList(c1, c2));

			when(usuarioMock.getCombustiblesFavoritos()).thenReturn(combustibles);
			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));

			Set<Short> result = service.getCombustiblesFavoritos(userId);

			assertEquals(2, result.size());
			assertTrue(result.contains((short) 1));
			assertTrue(result.contains((short) 2));
		}

		@Test
		@DisplayName("getEstacionesDeServicioFavoritasDto - Debe retornar los IDs planos devueltos por la Query nativa")
		void shouldReturnListOfStationIds() {
			Usuario usuarioMock = mock(Usuario.class);
			List<Integer> expectedIds = Arrays.asList(100, 200, 300);

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			when(usuarioRepository.findEstacionesFavoritasIdsByUuid(userId)).thenReturn(expectedIds);

			List<Integer> result = service.getEstacionesDeServicioFavoritasDto(userId);

			assertEquals(expectedIds, result);
		}
	}

	// =========================================================================
	// CREACIÓN DE USUARIO
	// =========================================================================
	@Nested
	@DisplayName("Creación de Usuarios")
	class CreateUsuarioTests {

		@Test
		@DisplayName("Debe lanzar InvalidUsuarioDataException si el DTO o el UUID son nulos")
		void shouldThrowInvalidDataWhenDtoOrUuidIsNull() {
			assertThrows(InvalidUsuarioDataException.class, () -> service.createUsuario(null));

			UsuarioDto nullUuidDto = new UsuarioDto(null, (short) 1, null, new HashSet<>());
			assertThrows(InvalidUsuarioDataException.class, () -> service.createUsuario(nullUuidDto));
		}

		@Test
		@DisplayName("Debe lanzar UsuarioAlreadyExistsException si el UUID ya está registrado")
		void shouldThrowAlreadyExistsWhenUuidInUse() {
			UsuarioDto dto = new UsuarioDto(userId, (short) 1, new HashSet<>(), new HashSet<>());
			when(usuarioRepository.existsById(userId)).thenReturn(true);

			assertThrows(UsuarioAlreadyExistsException.class, () -> service.createUsuario(dto));
		}

		@Test
		@DisplayName("Debe lanzar ResourceNotFoundException si la provincia no existe")
		void shouldThrowNotFoundWhenProvinciaDoesNotExist() {
			UsuarioDto dto = new UsuarioDto(userId, (short) 99, new HashSet<>(), new HashSet<>());
			when(usuarioRepository.existsById(userId)).thenReturn(false);
			when(provinciaRepository.findById((short) 99)).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> service.createUsuario(dto));
		}

		@Test
		@DisplayName("Debe crear usuario e inyectar combustibles por defecto si el set del DTO viene nulo")
		void shouldCreateUserWithDefaultCombustiblesWhenSetIsNull() {
			UsuarioDto dto = new UsuarioDto(userId, (short) 33, null, new HashSet<>());
			Provincia provinciaMock = mock(Provincia.class);
			List<Combustible> cachedCombustibles = Arrays.asList(mock(Combustible.class), mock(Combustible.class));

			when(usuarioRepository.existsById(userId)).thenReturn(false);
			when(provinciaRepository.findById((short) 33)).thenReturn(Optional.of(provinciaMock));
			when(combustibleQueryService.findAllCombustiblesCached()).thenReturn(cachedCombustibles);

			service.createUsuario(dto);

			ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
			verify(usuarioRepository).save(userCaptor.capture());

			Usuario savedUser = userCaptor.getValue();
			assertEquals(userId, savedUser.getUuid());
			assertEquals(provinciaMock, savedUser.getProvinciaFavorita());
			assertEquals(2, savedUser.getCombustiblesFavoritos().size());
		}

		@Test
		@DisplayName("Debe crear usuario con combustibles específicos si se pasan en el DTO")
		void shouldCreateUserWithSpecificCombustibles() {
			Set<Short> idsCombustibles = new HashSet<>(Arrays.asList((short) 1, (short) 2));
			UsuarioDto dto = new UsuarioDto(userId, (short) 33, idsCombustibles, new HashSet<>());
			Provincia provinciaMock = mock(Provincia.class);

			Combustible c1 = mock(Combustible.class);
			Combustible c2 = mock(Combustible.class);

			when(usuarioRepository.existsById(userId)).thenReturn(false);
			when(provinciaRepository.findById((short) 33)).thenReturn(Optional.of(provinciaMock));
			when(combustibleRepository.findById((short) 1)).thenReturn(Optional.of(c1));
			when(combustibleRepository.findById((short) 2)).thenReturn(Optional.of(c2));

			service.createUsuario(dto);

			ArgumentCaptor<Usuario> userCaptor = ArgumentCaptor.forClass(Usuario.class);
			verify(usuarioRepository).save(userCaptor.capture());

			Usuario savedUser = userCaptor.getValue();
			assertEquals(2, savedUser.getCombustiblesFavoritos().size());
		}

		@Test
		@DisplayName("Debe lanzar ResourceNotFoundException si uno de los combustibles pasados no existe")
		void shouldThrowNotFoundWhenSpecificCombustibleDoesNotExist() {
			Set<Short> idsCombustibles = new HashSet<>(Arrays.asList((short) 1, (short) 99));
			UsuarioDto dto = new UsuarioDto(userId, (short) 33, idsCombustibles, new HashSet<>());
			Provincia provinciaMock = mock(Provincia.class);

			when(usuarioRepository.existsById(userId)).thenReturn(false);
			when(provinciaRepository.findById((short) 33)).thenReturn(Optional.of(provinciaMock));
			when(combustibleRepository.findById((short) 1)).thenReturn(Optional.of(mock(Combustible.class)));
			when(combustibleRepository.findById((short) 99)).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> service.createUsuario(dto));
		}
	}

	// =========================================================================
	// ACTUALIZACIONES / MODIFICACIONES
	// =========================================================================
	@Nested
	@DisplayName("Actualización de Datos")
	class UpdateOperationsTests {

		@Test
		@DisplayName("updateProvincia - Debe actualizar la provincia del usuario correctamente")
		void shouldUpdateProvinciaSuccessfully() {
			Usuario usuarioMock = mock(Usuario.class);
			Provincia nuevaProvincia = mock(Provincia.class);

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			when(provinciaRepository.findById((short) 40)).thenReturn(Optional.of(nuevaProvincia));

			service.updateProvincia(userId, (short) 40);

			verify(usuarioMock).setProvinciaFavorita(nuevaProvincia);
			verify(usuarioRepository).save(usuarioMock);
		}

		@Test
		@DisplayName("updateProvincia - Debe lanzar ResourceNotFoundException si la provincia no existe")
		void shouldThrowWhenNewProvinciaNotFound() {
			Usuario usuarioMock = mock(Usuario.class);
			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			when(provinciaRepository.findById((short) 50)).thenReturn(Optional.empty());

			assertThrows(ResourceNotFoundException.class, () -> service.updateProvincia(userId, (short) 50));
		}

		@Test
		@DisplayName("updateCombustiblesFavoritos - Debe limpiar y cargar la cache total si el set recibido es null")
		void shouldReplaceWithCacheWhenSetIsNull() {
			Usuario usuarioMock = mock(Usuario.class);
			Set<Combustible> userCombustiblesSpy = spy(new HashSet<>());
			when(usuarioMock.getCombustiblesFavoritos()).thenReturn(userCombustiblesSpy);

			List<Combustible> allCached = Arrays.asList(mock(Combustible.class), mock(Combustible.class));

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			when(combustibleQueryService.findAllCombustiblesCached()).thenReturn(allCached);

			service.updateCombustiblesFavoritos(userId, null);

			verify(userCombustiblesSpy).clear();
			verify(userCombustiblesSpy).addAll(anyCollection());
			verify(usuarioRepository).save(usuarioMock);
		}

		@Test
		@DisplayName("updateCombustiblesFavoritos - Debe limpiar y setear elementos específicos si el conjunto es válido")
		void shouldReplaceWithSpecificSetOfCombustibles() {
			Usuario usuarioMock = mock(Usuario.class);
			Set<Combustible> userCombustiblesSpy = spy(new HashSet<>());
			when(usuarioMock.getCombustiblesFavoritos()).thenReturn(userCombustiblesSpy);

			Set<Short> inputs = new HashSet<>(Arrays.asList((short) 5));
			Combustible c5 = mock(Combustible.class);

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			when(combustibleRepository.findById((short) 5)).thenReturn(Optional.of(c5));

			service.updateCombustiblesFavoritos(userId, inputs);

			verify(userCombustiblesSpy).clear();
			assertTrue(userCombustiblesSpy.contains(c5));
			verify(usuarioRepository).save(usuarioMock);
		}
	}

	// =========================================================================
	// ESTACIONES FAVORITAS
	// =========================================================================
	@Nested
	@DisplayName("Gestión de Estaciones Favoritas")
	class EstacionesFavoritasMutationTests {

		@Test
		@DisplayName("addEstacionDeServicioFavorita - Debe añadir la estación y persistir el cambio si existe")
		void shouldAddStationToFavoritesSuccessfully() {
			Usuario usuarioMock = mock(Usuario.class);
			Set<EstacionDeServicio> eessSpy = spy(new HashSet<>());
			when(usuarioMock.getEessFavoritas()).thenReturn(eessSpy);

			EstacionDeServicio estacionMock = mock(EstacionDeServicio.class);

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			doNothing().when(estacionDeServicioService).existsOrThrow(777);
			when(estacionDeServicioService.getEstacionDeServicioById(777)).thenReturn(estacionMock);

			service.addEstacionDeServicioFavorita(userId, 777);

			verify(estacionDeServicioService).existsOrThrow(777);
			verify(eessSpy).add(estacionMock);
			verify(usuarioRepository).save(usuarioMock);
		}

		@Test
		@DisplayName("removeEstacionDeServicioFavorita - Debe remover la estación de la colección")
		void shouldRemoveStationFromFavoritesSuccessfully() {
			Usuario usuarioMock = mock(Usuario.class);
			EstacionDeServicio estacionMock = mock(EstacionDeServicio.class);

			Set<EstacionDeServicio> eessSpy = spy(new HashSet<>());
			eessSpy.add(estacionMock);
			when(usuarioMock.getEessFavoritas()).thenReturn(eessSpy);

			when(usuarioRepository.findById(userId)).thenReturn(Optional.of(usuarioMock));
			doNothing().when(estacionDeServicioService).existsOrThrow(777);
			when(estacionDeServicioService.getEstacionDeServicioById(777)).thenReturn(estacionMock);

			service.removeEstacionDeServicioFavorita(userId, 777);

			verify(estacionDeServicioService).existsOrThrow(777);
			verify(eessSpy).remove(estacionMock);
			verify(usuarioRepository).save(usuarioMock);
		}
	}
}