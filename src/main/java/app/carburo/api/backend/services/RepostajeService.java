package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.RepostajeDto;
import app.carburo.api.backend.entities.*;
import app.carburo.api.backend.exceptions.InvalidRepostajeDataException;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static app.carburo.api.backend.entities.Repostaje.COSTE_UNITARIO_MAX_VALUE;
import static app.carburo.api.backend.entities.Repostaje.COSTE_UNITARIO_MIN_VALUE;
import static app.carburo.api.backend.entities.Vehiculo.*;

/**
 * Servicio encargado de la gestión de los repostajes de los vehículos.
 *
 * <p>Este servicio encapsula toda la lógica de negocio relacionada con la entidad {@link Repostaje},
 * incluyendo su creación, consulta y actualización de relaciones.</p>
 *
 * <p>Se utiliza como capa intermedia entre los controladores REST y los repositorios JPA,
 * garantizando separación de responsabilidades y centralización de reglas de negocio.</p>
 *
 */
@Service
public class RepostajeService {

    private final RepostajeRepository repostajeRepository;
    private final VehiculoRepository vehiculoRepository;
    private final VehiculoUsuarioRepository vehiculoUsuarioRepository;
    private final UsuarioRepository usuarioRepository;
    private final CombustibleRepository combustibleRepository;
    private final EstacionDeServicioRepository estacionDeServicioRepository;

    /**
     * Constructor con inyección de dependencias.
     */
    public RepostajeService(RepostajeRepository repostajeRepository,
                            VehiculoRepository vehiculoRepository,
                            VehiculoUsuarioRepository vehiculoUsuarioRepository,
                            UsuarioRepository usuarioRepository,
                            CombustibleRepository combustibleRepository,
                            EstacionDeServicioRepository estacionDeServicioRepository) {

        this.repostajeRepository          = repostajeRepository;
        this.vehiculoRepository           = vehiculoRepository;
        this.vehiculoUsuarioRepository    = vehiculoUsuarioRepository;
        this.usuarioRepository            = usuarioRepository;
        this.combustibleRepository        = combustibleRepository;
        this.estacionDeServicioRepository = estacionDeServicioRepository;
    }

    /**
     * Obtiene un repostaje concreto asociado a un vehículo.
     * <p>
     * Comprobaciones realizadas:
     * - El vehículo debe existir.
     * - El usuario debe estar vinculado al vehículo.
     * - El repostaje debe existir.
     * - El repostaje debe pertenecer al vehículo indicado.
     *
     * @param uuid        UUID del usuario solicitante
     * @param idVehiculo  identificador del vehículo
     * @param idRepostaje identificador del repostaje
     * @return {@link RepostajeDto}
     */
    public RepostajeDto getRepostaje(UUID uuid, int idVehiculo, int idRepostaje) {
        validateVehiculoLinkedToUser(uuid, idVehiculo);

        Repostaje repostaje = validateRepostajeOwnership(idVehiculo, idRepostaje);

        return RepostajeDto.from(repostaje);
    }

    /**
     * Obtiene todos los repostajes de un usuario.
     *
     * @param uuid UUID del usuario
     * @return listado de repostajes
     */
    public List<RepostajeDto> getRepostajesUsuario(UUID uuid) {

        findUsuarioOrThrow(uuid);

        return repostajeRepository.findAllByUsuarioUuidOrderByFechaRepostajeDesc(uuid)
                .stream().map(RepostajeDto::from)
                .toList();
    }

    /**
     * Obtiene el repostaje según su id.
     *
     * @param uuid UUID del usuario
     * @param id   Id del repostaje
     * @return listado de repostajes
     */
    public RepostajeDto getRepostajeById(UUID uuid, int id) {
        findUsuarioOrThrow(uuid);

        Repostaje repostaje = findRepostajeOrThrow(id);

        validateVehiculoLinkedAndOwnership(uuid, repostaje.getVehiculo().getId(), false);

        return RepostajeDto.from(repostaje);
    }

    /**
     * Obtiene todos los repostajes de un vehículo.
     * <p>
     * Comprobaciones realizadas:
     * - El vehículo debe existir.
     * - El usuario debe estar vinculado al vehículo.
     *
     * @param uuid UUID del usuario solicitante
     * @param idVehiculo identificador del vehículo
     * @return listado de repostajes
     */
    public List<RepostajeDto> getRepostajesVehiculo(UUID uuid, int idVehiculo) {

        validateVehiculoLinkedToUser(uuid, idVehiculo);

        return repostajeRepository.findAllByVehiculoIdOrderByFechaRepostajeDesc(
                idVehiculo).stream().map(RepostajeDto::from).toList();
    }

    /**
     * Crea un nuevo repostaje asociado a un vehículo.
     * <p>
     * Comprobaciones realizadas:
     * - El usuario debe existir.
     * - El vehículo debe existir.
     * - El usuario debe ser propietario del vehículo.
     * - El combustible debe existir.
     * - La estación de servicio debe existir.
     * <p>
     * Tras insertar correctamente el repostaje, el odómetro actual
     * del vehículo será actualizado automáticamente si el odómetro
     * final del repostaje es superior al actual.
     *
     * @param uuid UUID del usuario creador
     * @param idVehiculo identificador del vehículo
     * @param dto datos del repostaje
     * @return identificador del repostaje creado
     */
    @Transactional
    public int createRepostaje(UUID uuid, int idVehiculo, RepostajeDto dto) {
        Usuario usuario = findUsuarioOrThrow(uuid);
        Vehiculo vehiculo = validateVehiculoLinkedAndOwnership(uuid, idVehiculo, true);
        Combustible combustible = findCombustibleOrThrow(dto.id_combustible());
        EstacionDeServicio estacion = findEstacionOrThrow(dto.id_estacion_de_servicio());

        validateRepostajeDto(dto);

        if (combustible.getIdGrupoCombustible() != vehiculo.getGrupoCombustible().getId())
            throw new InvalidRepostajeDataException(
                    "El combustible indicado no está dentro del grupo de combustibles aceptados por el vehículo.");

        BigDecimal odoInicial = dto.odometro_inicial() != null ? BigDecimal.valueOf(dto.odometro_inicial()) : null;
        BigDecimal odoFinal = BigDecimal.valueOf(dto.odometro_final());

        if (repostajeRepository.existsSolapamientoOdomatros(idVehiculo, odoInicial, odoFinal, null))
            throw new InvalidRepostajeDataException("Conflicto de kilometraje: El odómetro final o la franja indicada entra en conflicto, se solapa o coincide con hitos de un repostaje existente.");

        Repostaje repostaje = new Repostaje(vehiculo, combustible, estacion, usuario,
                                            dto.cantidad(), dto.coste_unitario(),
                                            dto.odometro_inicial(), dto.odometro_final(),
                                            dto.fecha_repostaje(),
                                            dto.deposito_lleno(), dto.nota());

        repostaje = repostajeRepository.save(repostaje);

        updateVehiculoOdometerIfNecessary(vehiculo, repostaje.getOdometroFinal());

        return repostaje.getId();
    }

    /**
     * Actualiza un repostaje existente.
     * Se permite actualizar únicamente la cantidad, coste unitario, odómetro inicial, odómetro final, si el depósito se llenó o no, y la nota.
     * <p>
     * Comprobaciones realizadas:
     * - El vehículo debe existir.
     * - El usuario debe ser propietario del vehículo.
     * - El repostaje debe existir.
     * - El repostaje debe pertenecer al vehículo.
     * - El combustible debe existir.
     * - La estación de servicio debe existir.
     * <p>
     * Tras actualizar correctamente el repostaje, el odómetro actual
     * del vehículo será actualizado automáticamente si el odómetro
     * final del repostaje es superior al actual.
     *
     * @param uuid UUID del usuario
     * @param idVehiculo identificador del vehículo
     * @param idRepostaje identificador del repostaje
     * @param dto datos actualizados
     */
    @Transactional
    public void updateRepostaje(UUID uuid, int idVehiculo, int idRepostaje,
                                RepostajeDto dto) {

        Vehiculo vehiculo = validateVehiculoLinkedAndOwnership(uuid, idVehiculo, true);
        Repostaje repostaje = validateRepostajeOwnership(idVehiculo, idRepostaje);

        validateRepostajeDto(dto);

        BigDecimal nuevoOdoInicial = dto.odometro_inicial() != null ? BigDecimal.valueOf(dto.odometro_inicial()) : null;
        BigDecimal nuevoOdoFinal = BigDecimal.valueOf(dto.odometro_final());

        // Comparamos de forma segura contemplando que cualquiera de los dos iniciales pueda ser null
        boolean haCambiadoInicial = (repostaje.getOdometroInicial() == null && nuevoOdoInicial != null) ||
                (repostaje.getOdometroInicial() != null && nuevoOdoInicial == null) ||
                (repostaje.getOdometroInicial() != null && nuevoOdoInicial != null && repostaje.getOdometroInicial().compareTo(nuevoOdoInicial) != 0);

        boolean haCambiadoFinal = repostaje.getOdometroFinal().compareTo(nuevoOdoFinal) != 0;

        // Solo vamos a la base de datos si el usuario editó las casillas de kilómetros
        if (haCambiadoInicial || haCambiadoFinal) {
            if (repostajeRepository.existsSolapamientoOdomatros(idVehiculo, nuevoOdoInicial, nuevoOdoFinal, idRepostaje))
                throw new InvalidRepostajeDataException("La actualización falla: Las nuevas lecturas de odómetro entran en conflicto o se solapan con registros históricos.");

        }

        repostaje.setCosteUnitario(dto.coste_unitario());
        repostaje.setCantidad(dto.cantidad());
        repostaje.setOdometroInicial(dto.odometro_inicial());
        repostaje.setOdometroFinal(dto.odometro_final());
        repostaje.setDepositoLleno(dto.deposito_lleno());
        repostaje.setNota(dto.nota());

        repostajeRepository.save(repostaje);

        updateVehiculoOdometerIfNecessary(vehiculo, repostaje.getOdometroFinal());
    }

    /**
     * Elimina un repostaje existente.
     * <p>
     * Comprobaciones realizadas:
     * - El vehículo debe existir.
     * - El usuario debe ser propietario del vehículo.
     * - El repostaje debe existir.
     * - El repostaje debe pertenecer al vehículo.
     *
     * @param uuid        UUID del usuario
     * @param idVehiculo  identificador del vehículo
     * @param idRepostaje identificador del repostaje
     */
    @Transactional
    public void deleteRepostaje(UUID uuid, int idVehiculo, int idRepostaje) {

        validateVehiculoLinkedAndOwnership(uuid, idVehiculo, true);
        Repostaje repostaje = validateRepostajeOwnership(idVehiculo, idRepostaje);
        repostajeRepository.delete(repostaje);
    }

    /**
     * Valida que un repostaje pertenece al vehículo indicado.
     *
     * @param idVehiculo  identificador del vehículo
     * @param idRepostaje identificador del repostaje
     * @return entidad repostaje
     */
    private Repostaje validateRepostajeOwnership(int idVehiculo, int idRepostaje) {

        Repostaje repostaje = findRepostajeOrThrow(idRepostaje);

        if (!repostaje.getVehiculo().getId().equals(idVehiculo)) {
            throw new UnauthorizedException(
                    "El repostaje no pertenece al vehículo indicado");
        }

        return repostaje;
    }

    /**
     * Valida que un usuario esté vinculado a un vehículo.
     *
     * @param uuid       UUID del usuario
     * @param idVehiculo identificador del vehículo
     */
    private void validateVehiculoLinkedToUser(UUID uuid, int idVehiculo) {

        findVehiculoOrThrow(idVehiculo);

        vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(uuid, idVehiculo)
                .orElseThrow(() -> new UnauthorizedException(
                        "El usuario no está vinculado al vehículo"));
    }

    /**
     * Valida que un usuario esté vinculado a un vehículo y,
     * opcionalmente, que además sea propietario.
     *
     * @param uuid        UUID del usuario
     * @param idVehiculo  identificador del vehículo
     * @param mustBeOwner true si se requiere propiedad
     * @return entidad vehículo
     */
    private Vehiculo validateVehiculoLinkedAndOwnership(UUID uuid, int idVehiculo,
                                                        boolean mustBeOwner) {

        Vehiculo vehiculo = findVehiculoOrThrow(idVehiculo);

        VehiculoUsuario relacion = vehiculoUsuarioRepository.findByUsuarioUuidAndVehiculoId(
                uuid, idVehiculo).orElseThrow(() -> new UnauthorizedException(
                "El usuario no está vinculado al vehículo"));

        if (mustBeOwner && !relacion.isPropietario()) {
            throw new UnauthorizedException("El usuario no es propietario del vehículo");
        }

        return vehiculo;
    }

    /**
     * Actualiza el odómetro actual del vehículo únicamente
     * si el nuevo valor es superior al actual.
     *
     * @param vehiculo      vehículo a actualizar
     * @param nuevoOdometro nuevo odómetro
     */
    private void updateVehiculoOdometerIfNecessary(Vehiculo vehiculo,
                                                   BigDecimal nuevoOdometro) {
        if (nuevoOdometro.compareTo(vehiculo.getOdometroActual()) <= 0) return;
        vehiculo.setOdometroActual(nuevoOdometro.doubleValue());
        vehiculoRepository.save(vehiculo);
    }

    /**
     * Busca un repostaje por ID o lanza excepción.
     *
     * @param id identificador del repostaje
     * @return entidad repostaje
     */
    private Repostaje findRepostajeOrThrow(int id) {
        return repostajeRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Repostaje no encontrado"));
    }

    /**
     * Busca un vehículo por ID o lanza excepción.
     *
     * @param id identificador del vehículo
     * @return entidad vehículo
     */
    private Vehiculo findVehiculoOrThrow(int id) {
        return vehiculoRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Vehículo no encontrado"));
    }

    /**
     * Busca un usuario por UUID o lanza excepción.
     *
     * @param uuid UUID del usuario
     * @return entidad usuario
     */
    private Usuario findUsuarioOrThrow(UUID uuid) {
        return usuarioRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
    }

    /**
     * Busca un combustible por ID o lanza excepción.
     *
     * @param id identificador del combustible
     * @return entidad combustible
     */
    private Combustible findCombustibleOrThrow(short id) {
        return combustibleRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Combustible no encontrado: " + id));
    }

    /**
     * Busca una estación de servicio por ID o lanza excepción.
     *
     * @param id identificador de la estación
     * @return entidad estación
     */
    private EstacionDeServicio findEstacionOrThrow(int id) {
        return estacionDeServicioRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException(
                        "Estación de servicio no encontrada"));
    }

    /**
     * Valida los datos de un DTO de repostaje.
     *
     * @param dto DTO a validar
     */
    private void validateRepostajeDto(RepostajeDto dto) {
        if (dto == null)
            throw new InvalidRepostajeDataException("El repostaje no puede ser nulo");

        if (dto.cantidad() <= CAPACIDAD_DEPOSITO_MIN_VALUE ||
                dto.cantidad() > CAPACIDAD_DEPOSITO_MAX_VALUE)
            throw new InvalidRepostajeDataException(
                    "La cantidad repostada debe ser mayor que 0 y menor que 99.999,99");

        if (dto.coste_unitario() <= COSTE_UNITARIO_MIN_VALUE || dto.coste_unitario() > COSTE_UNITARIO_MAX_VALUE) throw new InvalidRepostajeDataException(
                "El coste unitario debe ser mayor que 0 y menor que 999,999");

        if (dto.odometro_final() < ODOMETRO_MIN_VALUE || dto.coste_unitario() > ODOMETRO_MAX_VALUE) throw new InvalidRepostajeDataException(
                "El odómetro final no puede ser negativo ni mayor a 9.999.999,99");

        if (dto.odometro_inicial() != null && dto.odometro_inicial() < 0)
            throw new InvalidRepostajeDataException(
                "El odómetro inicial no puede ser negativo");

        if (dto.odometro_inicial() != null &&
                dto.odometro_final() <= dto.odometro_inicial())
            throw new InvalidRepostajeDataException(
                    "El odómetro final no puede ser menor al inicial");

        if (dto.nota() != null && dto.nota().length() > 100)
            throw new InvalidRepostajeDataException(
                    "La nota no puede superar los 100 caracteres");
    }
}