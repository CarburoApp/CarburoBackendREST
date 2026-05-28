package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.VehiculoDto;
import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.Usuario;
import app.carburo.api.backend.entities.Vehiculo;
import app.carburo.api.backend.entities.VehiculoUsuario;
import app.carburo.api.backend.exceptions.*;
import app.carburo.api.backend.repositories.CombustibleRepository;
import app.carburo.api.backend.repositories.UsuarioRepository;
import app.carburo.api.backend.repositories.VehiculoRepository;
import app.carburo.api.backend.repositories.VehiculoUsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Servicio encargado de la gestión de vehículos dentro del sistema.
 *
 * <p>
 * Encapsula la lógica de negocio relacionada con la entidad {@link Vehiculo},
 * incluyendo operaciones de creación, consulta, edición y eliminación.
 * </p>
 *
 * <p>
 * También realiza validaciones de vinculación y propiedad entre usuarios
 * y vehículos para operaciones protegidas.
 * </p>
 */
@Service
public class VehiculoService {

    private final VehiculoRepository vehiculoRepository;
    private final VehiculoUsuarioRepository vehiculoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CombustibleRepository combustibleRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param vehiculoRepository repositorio de vehiculos
     */
    public VehiculoService(VehiculoRepository vehiculoRepository,
                           VehiculoUsuarioRepository vehiculoUsuarioRepository,
                           UsuarioRepository usuarioRepository,
                           CombustibleRepository combustibleRepository) {
        this.vehiculoRepository = vehiculoRepository;
        this.usuarioRepository = usuarioRepository;
        this.vehiculoUsuarioRepository = vehiculoUsuarioRepository;
        this.combustibleRepository     = combustibleRepository;
    }

    /**
     * Comprueba si un vehículo existe en el sistema.
     *
     * @param id identificador del vehículo
     * @return true si existe, false en caso contrario
     */
    public boolean existsVehiculo(int id) {
        return vehiculoRepository.existsById(id);
    }

    /**
     * Obtiene un vehículo concreto vinculado a un usuario y lo devuelve en formato DTO..
     * Comprobaciones realizadas:
     * - El usuario debe existir.
     * - El vehículo debe existir.
     * - El vehículo debe estar vinculado al usuario.
     *
     * @param uuid {@link UUID} identificador único del usuario
     * @param id   identificador único del vehículo
     * @return {@link VehiculoDto} del vehículo solicitado
     * @throws ResourceNotFoundException si el vehículo o usuario no existe
     */
    public VehiculoDto getVehiculo(UUID uuid, int id) {
        Vehiculo vehiculo = validateVehiculoExistsAndOwnership(uuid, id, false);
        boolean propietario = isVehiculoOwner(uuid, id);

        return VehiculoDto.from(vehiculo, uuid, propietario);
    }

    /**
     * Obtiene todos los vehículos del usuario identificado por su id, y lo devuelve en formato DTO.
     * Se debe de verificar que el usuario existe y que cada vehículo esté vinculado al usuario.
     *
     * @param uuid {@link UUID} identificador único del usuario
     * @return Listado de {@link VehiculoDto} del los vehículos vinculados al usuario
     * @throws ResourceNotFoundException si el vehículo o usuario no existe
     */
    public List<VehiculoDto> getVehiculoFromUsuario(UUID uuid) {
        findUsuarioOrThrow(uuid);

        return vehiculoUsuarioRepository.findAllByUsuarioUuid(uuid)
                .stream()
                .map(vu -> VehiculoDto.from(vu.getVehiculo(), uuid, vu.isPropietario()))
                .toList();
    }


    /**
     *  Crea un nuevo vehículo y lo vincula al usuario como propietario.
     *
     * @param uuid UUID del usuario propietario
     * @param dto datos del vehículo
     * @return ID del vehículo creado
     * @throws InvalidUsuarioDataException si el DTO es inválido
     * @throws UsuarioAlreadyExistsException si el usuario ya existe
     * @throws ResourceNotFoundException si la provincia o combustibles no existen
     */
    @Transactional
    public int createVehiculo(UUID uuid, VehiculoDto dto) {

        Usuario usuario = findUsuarioOrThrow(uuid);

        validateVehiculoDto(dto);

        Set<Combustible> combustibles = dto.ids_combustibles_utilizados().stream()
                .map(this::getCombustiblesOrThrow).collect(Collectors.toSet());

        Vehiculo vehiculo = new Vehiculo(dto.matricula(), dto.marca(), dto.modelo(),
                                         dto.odometro_actual(), dto.capacidad_deposito(),
                                         combustibles, dto.notas());

        vehiculo = vehiculoRepository.save(vehiculo);

        VehiculoUsuario relacion = new VehiculoUsuario(vehiculo, usuario, true);

        vehiculoUsuarioRepository.save(relacion);

        return vehiculo.getId();
    }

    /**
     * Actualiza los datos de un vehículo existente.
     *
     * @param uuid UUID del usuario
     * @param id   identificador del vehículo
     * @param dto  nuevos datos
     */
    @Transactional
    public void updateVehiculo(UUID uuid, int id, VehiculoDto dto) {

        Vehiculo vehiculo = validateVehiculoExistsAndOwnership(uuid, id, true);

        validateVehiculoDto(dto);

        vehiculo.setMarca(dto.marca());
        vehiculo.setModelo(dto.modelo());
        vehiculo.setMatricula(dto.matricula());

        vehiculo.setOdometroActual(dto.odometro_actual());
        vehiculo.setCapacidadDeposito(dto.capacidad_deposito());

        Set<Combustible> combustibles = combustibleRepository.findAllByIdIn(
                dto.ids_combustibles_utilizados());

        vehiculo.setCombustibles(combustibles);

        vehiculo.setNotas(dto.notas());

        vehiculo.setFechaModificacion(OffsetDateTime.now());

        vehiculoRepository.save(vehiculo);
    }


    /**
     * Elimina un vehículo. Se realiza comprobación de que el usuario es propietario del
     * vehículo antes de eliminarlo.
     *
     * @param id identificador del vehículo
     * @param uuid identificador del usuario
     * @throws ResourceNotFoundException si el usuario o el vehiculo no existen.
     */
    @Transactional
    public void deleteVehiculo(UUID uuid, int id) {
        Vehiculo vehiculo = validateVehiculoExistsAndOwnership(uuid, id, true);
        vehiculoRepository.delete(vehiculo);
    }

    /**
     * Busca un vehiculo por su id o lanza excepción si no existe.
     *
     * @param id identificador del vehículo
     * @return entidad {@link Vehiculo}
     * @throws ResourceNotFoundException si el vehiculo no existe
     */
    private Vehiculo findVehiculoOrThrow(int id) {
        return vehiculoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Vehiculo no encontrado"));
    }


    /**
     * Valida que un usuario es propietario de un vehículo. Lanza excepción si el usuario
     * o el vehículo no existen o en caso de que el usuario no sea propietario.
     * En caso de que todo se cumpla, devuelve la entidad del vehículo para su uso posterior.
     *
     * @param uuid        UUID del usuario
     * @param id          identificador del vehículo
     * @param mustBeOwner true si debe exigirse propiedad
     * @return entidad {@link Vehiculo}
     */
    private Vehiculo validateVehiculoExistsAndOwnership(UUID uuid, int id,
                                                        boolean mustBeOwner) {
        Vehiculo vehiculo = findVehiculoOrThrow(id);

        VehiculoUsuario relacion = vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(
                uuid, id).orElseThrow(() -> new UnauthorizedException(
                "El usuario no está vinculado al vehículo"));

        if (mustBeOwner && !relacion.isPropietario()) {
            throw new UnauthorizedException("El usuario no es propietario del vehículo");
        }
        return vehiculo;
    }

    /**
     * Comprueba si un usuario es propietario de un vehículo.
     *
     * @param uuid UUID del usuario
     * @param id   identificador del vehículo
     * @return true si es propietario
     */
    public boolean isVehiculoOwner(UUID uuid, int id) {

        return vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(uuid, id)
                .map(VehiculoUsuario::isPropietario).orElse(false);
    }

    /**
     * Busca un usuario por UUID o lanza excepción si no existe.
     *
     * @param uuid identificador del usuario
     * @return entidad {@link Usuario}
     * @throws ResourceNotFoundException si el usuario no existe
     */
    private Usuario findUsuarioOrThrow(UUID uuid) {
        return usuarioRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }


    /**
     * Obtiene un combustible por ID o lanza excepción si no existe.
     *
     * @param id identificador del combustible
     * @return entidad Combustible
     * @throws ResourceNotFoundException si el combustible no existe
     */
    private Combustible getCombustiblesOrThrow(short id) {
        return combustibleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combustible no encontrado: " + id));
    }

    /**
     * Valida los datos de un DTO de vehículo.
     *
     * @param dto DTO a validar
     */
    private void validateVehiculoDto(VehiculoDto dto) {
        if (dto == null)
            throw new InvalidVehiculoDataException("El vehículo no puede ser nulo");

        if (dto.marca() == null || dto.marca().isBlank() || dto.marca().length() > 40)
            throw new InvalidVehiculoDataException(
                    "La marca es obligatorio y con longitud <= 40.");

        if (dto.modelo() == null || dto.modelo().isBlank() || dto.modelo().length() > 40)
            throw new InvalidVehiculoDataException(
                    "El modelo es obligatorio y con longitud <= 40.");

        if (dto.matricula() == null || dto.matricula().isBlank() ||
                dto.matricula().length() > 20) throw new InvalidVehiculoDataException(
                "La matricula es obligatoria y con longitud <= 20.");

        if (dto.odometro_actual() < 0) {
            throw new InvalidVehiculoDataException(
                    "El odómetro actual no puede ser menor al inicial");
        }

        if (dto.capacidad_deposito() < 0) {
            throw new InvalidVehiculoDataException(
                    "La capacidad del depósito debe ser mayor que 0");
        }

        if (dto.ids_combustibles_utilizados() == null ||
                dto.ids_combustibles_utilizados().isEmpty()) {
            throw new InvalidVehiculoDataException(
                    "Los combustibles utilizados no pueden ser nulo o vacío.");
        }

        if (!combustibleRepository.existsAllByIdIn(dto.ids_combustibles_utilizados())) {
            throw new InvalidVehiculoDataException(
                    "Algunos combustibles indicados como utilizados no son válidos: " +
                            dto.ids_combustibles_utilizados());
        }
    }
}