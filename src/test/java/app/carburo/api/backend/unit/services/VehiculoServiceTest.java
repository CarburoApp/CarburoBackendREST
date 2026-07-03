package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.VehiculoDto;
import app.carburo.api.backend.entities.GrupoCombustible;
import app.carburo.api.backend.entities.Usuario;
import app.carburo.api.backend.entities.Vehiculo;
import app.carburo.api.backend.entities.VehiculoUsuario;
import app.carburo.api.backend.exceptions.InvalidVehiculoDataException;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.repositories.GrupoCombustibleRepository;
import app.carburo.api.backend.repositories.UsuarioRepository;
import app.carburo.api.backend.repositories.VehiculoRepository;
import app.carburo.api.backend.repositories.VehiculoUsuarioRepository;
import app.carburo.api.backend.services.VehiculoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - VehiculoService")
class VehiculoServiceTest {

	@Mock
	private VehiculoRepository vehiculoRepository;
	@Mock
	private VehiculoUsuarioRepository vehiculoUsuarioRepository;
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private GrupoCombustibleRepository grupoCombustibleRepository;

	@InjectMocks
	private VehiculoService service;

	private UUID usuarioUuid;
	private int vehiculoId;
	private Usuario usuarioMock;
	private GrupoCombustible grupoMock;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		usuarioUuid = UUID.randomUUID();
		vehiculoId = 1;

		usuarioMock = new Usuario();
		usuarioMock.setUuid(usuarioUuid);

		grupoMock = new GrupoCombustible();
		grupoMock.setId((short) 1);
	}

	// ==========================================
	// TESTS: existsVehiculo
	// ==========================================

	@Test
	@DisplayName("existsVehiculo: Debe devolver true si el vehículo existe")
	void existsVehiculoShouldReturnTrueWhenExists() {
		when(vehiculoRepository.existsById(vehiculoId)).thenReturn(true);
		assertTrue(service.existsVehiculo(vehiculoId));
	}

	@Test
	@DisplayName("existsVehiculo: Debe devolver false si el vehículo no existe")
	void existsVehiculoShouldReturnFalseWhenNotExists() {
		when(vehiculoRepository.existsById(vehiculoId)).thenReturn(false);
		assertFalse(service.existsVehiculo(vehiculoId));
	}

	// ==========================================
	// TESTS: getVehiculo
	// ==========================================

	@Test
	@DisplayName("getVehiculo: Debe devolver el DTO si existe y está vinculado")
	void getVehiculoShouldReturnDtoWhenValid() {
		Vehiculo v = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		VehiculoUsuario vu = new VehiculoUsuario(v, usuarioMock, true);

		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(v));
		when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(usuarioUuid, vehiculoId))
				.thenReturn(Optional.of(vu));

		VehiculoDto result = service.getVehiculo(usuarioUuid, vehiculoId);

		assertNotNull(result);
		assertEquals(vehiculoId, result.id());
		assertEquals(usuarioUuid, result.uuid_usuario_solicitante());
		assertTrue(result.is_usuario_solicitante_propietario());
		assertEquals("1234BBB", result.matricula());
		assertEquals("Seat", result.marca());
	}

	@Test
	@DisplayName("getVehiculo: Debe lanzar ResourceNotFoundException si el vehículo no existe")
	void getVehiculoShouldThrowNotFoundWhenVehiculoDoesNotExist() {
		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.empty());

		assertThrows(ResourceNotFoundException.class, () -> service.getVehiculo(usuarioUuid, vehiculoId));
	}

	@Test
	@DisplayName("getVehiculo: Debe lanzar UnauthorizedException si no hay vinculación")
	void getVehiculoShouldThrowUnauthorizedWhenNotLinked() {
		Vehiculo v = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(v));
		when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(usuarioUuid, vehiculoId))
				.thenReturn(Optional.empty());

		assertThrows(UnauthorizedException.class, () -> service.getVehiculo(usuarioUuid, vehiculoId));
	}

	// ==========================================
	// TESTS: getVehiculoFromUsuario
	// ==========================================

	@Test
	@DisplayName("getVehiculoFromUsuario: Debe mapear la lista de vehículos del usuario")
	void getVehiculoFromUsuarioShouldReturnList() {
		Vehiculo v = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		VehiculoUsuario vu = new VehiculoUsuario(v, usuarioMock, true);

		when(usuarioRepository.findById(usuarioUuid)).thenReturn(Optional.of(usuarioMock));
		when(vehiculoUsuarioRepository.findAllByUsuarioUuid(usuarioUuid)).thenReturn(List.of(vu));

		List<VehiculoDto> result = service.getVehiculoFromUsuario(usuarioUuid);

		assertEquals(1, result.size());
		assertEquals("1234BBB", result.getFirst().matricula());
		assertTrue(result.getFirst().is_usuario_solicitante_propietario());
	}

	// ==========================================
	// TESTS: createVehiculo (Éxito y Validaciones)
	// ==========================================

	@Test
	@DisplayName("createVehiculo: Debe guardar el vehículo y la relación correctamente")
	void createVehiculoShouldSaveAndReturnId() {
		// Inicializado respetando el orden exacto de los campos de tu Record
		VehiculoDto dto = new VehiculoDto(0, usuarioUuid, true, "1234BBB", "Toyota", "Yaris", 1000.0, 42.0, "Híbrido", (short) 1);
		Vehiculo vSaved = buildVehiculo(7, "1234BBB", "Toyota", "Yaris", 1000.0, 42.0, "Híbrido");

		when(usuarioRepository.findById(usuarioUuid)).thenReturn(Optional.of(usuarioMock));
		when(grupoCombustibleRepository.findById((short) 1)).thenReturn(Optional.of(grupoMock));
		when(vehiculoRepository.save(any(Vehiculo.class))).thenReturn(vSaved);

		int newId = service.createVehiculo(usuarioUuid, dto);

		assertEquals(7, newId);
		verify(vehiculoRepository, times(1)).save(any(Vehiculo.class));
		verify(vehiculoUsuarioRepository, times(1)).save(any(VehiculoUsuario.class));
	}

	@Test
	@DisplayName("createVehiculo: Debe lanzar excepción si la marca está en blanco o excede 40 caracteres")
	void createVehiculoShouldValidateMarcaConstraints() {
		when(usuarioRepository.findById(usuarioUuid)).thenReturn(Optional.of(usuarioMock));

		VehiculoDto dtoVacio = new VehiculoDto(0, usuarioUuid, true, "1234BBB", "   ", "Yaris", 1000.0, 42.0, "N", (short) 1);
		VehiculoDto dtoLargo = new VehiculoDto(0, usuarioUuid, true, "1234BBB", "M".repeat(41), "Yaris", 1000.0, 42.0, "N", (short) 1);

		assertThrows(InvalidVehiculoDataException.class, () -> service.createVehiculo(usuarioUuid, dtoVacio));
		assertThrows(InvalidVehiculoDataException.class, () -> service.createVehiculo(usuarioUuid, dtoLargo));
	}

	@Test
	@DisplayName("createVehiculo: Debe lanzar excepción si el odómetro es negativo o supera el límite")
	void createVehiculoShouldValidateOdometroConstraints() {
		when(usuarioRepository.findById(usuarioUuid)).thenReturn(Optional.of(usuarioMock));

		VehiculoDto dtoNegativo = new VehiculoDto(0, usuarioUuid, true, "1234BBB", "Ford", "Focus", -5.0, 42.0, "N", (short) 1);
		VehiculoDto dtoExcedido = new VehiculoDto(0, usuarioUuid, true, "1234BBB", "Ford", "Focus", 10000000.0, 42.0, "N", (short) 1);

		assertThrows(InvalidVehiculoDataException.class, () -> service.createVehiculo(usuarioUuid, dtoNegativo));
		assertThrows(InvalidVehiculoDataException.class, () -> service.createVehiculo(usuarioUuid, dtoExcedido));
	}

	// ==========================================
	// TESTS: updateVehiculo
	// ==========================================

	@Test
	@DisplayName("updateVehiculo: Debe modificar los datos si el usuario es el propietario")
	void updateVehiculoShouldModifyWhenUserIsOwner() {
		Vehiculo vOriginal = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		VehiculoUsuario vu = new VehiculoUsuario(vOriginal, usuarioMock, true); // Propietario = true
		VehiculoDto dtoNew = new VehiculoDto(vehiculoId, usuarioUuid, true, "5555XYZ", "Seat", "Ibiza Cupra", 60000.0, 50.0, "Nueva Nota", (short) 1);

		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(vOriginal));
		when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(usuarioUuid, vehiculoId))
				.thenReturn(Optional.of(vu));

		service.updateVehiculo(usuarioUuid, vehiculoId, dtoNew);

		assertEquals("5555XYZ", vOriginal.getMatricula());
		assertEquals("Ibiza Cupra", vOriginal.getModelo());
		assertNotNull(vOriginal.getFechaModificacion());
		verify(vehiculoRepository, times(1)).save(vOriginal);
	}

	@Test
	@DisplayName("updateVehiculo: Debe lanzar UnauthorizedException si está vinculado pero no es propietario")
	void updateVehiculoShouldThrowUnauthorizedWhenNotOwner() {
		Vehiculo vOriginal = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		VehiculoUsuario vu = new VehiculoUsuario(vOriginal, usuarioMock, false); // Propietario = false
		VehiculoDto dtoNew = new VehiculoDto(vehiculoId, usuarioUuid, false, "5555XYZ", "Seat", "Ibiza", 50000.0, 45.0, "Nota", (short) 1);

		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(vOriginal));
		when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(usuarioUuid, vehiculoId))
				.thenReturn(Optional.of(vu));

		assertThrows(UnauthorizedException.class, () -> service.updateVehiculo(usuarioUuid, vehiculoId, dtoNew));
		verify(vehiculoRepository, never()).save(any());
	}

	// ==========================================
	// TESTS: deleteVehiculo
	// ==========================================

	@Test
	@DisplayName("deleteVehiculo: Debe borrar el vehículo si es propietario")
	void deleteVehiculoShouldDeleteWhenOwner() {
		Vehiculo v = buildVehiculo(vehiculoId, "1234BBB", "Seat", "Ibiza", 50000.0, 45.0, "Nota");
		VehiculoUsuario vu = new VehiculoUsuario(v, usuarioMock, true);

		when(vehiculoRepository.findById(vehiculoId)).thenReturn(Optional.of(v));
		when(vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(usuarioUuid, vehiculoId))
				.thenReturn(Optional.of(vu));

		service.deleteVehiculo(usuarioUuid, vehiculoId);

		verify(vehiculoRepository, times(1)).delete(v);
	}

	// ==========================================
	// MÉTODOS HELPER
	// ==========================================

	private Vehiculo buildVehiculo(int id, String mat, String marca, String mod, double odometro, double cap, String notas) {
		// Mapeamos los datos simulando que en la entidad real usas BigDecimal o similar (por el .doubleValue() del DTO)
		Vehiculo v = new Vehiculo(mat, marca, mod, odometro, cap, grupoMock, notas);
		v.setId(id);

		return v;
	}
}