package app.carburo.api.backend.services;

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
import app.carburo.api.backend.services.queryServices.CombustibleQueryService;
import jakarta.transaction.Transactional;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
/**
 * Servicio encargado de la gestión de usuarios dentro del sistema.
 *
 * <p>Este servicio encapsula toda la lógica de negocio relacionada con la entidad {@link Usuario},
 * incluyendo su creación, consulta y actualización de relaciones como provincia favorita,
 * combustibles favoritos y estaciones de servicio favoritas.</p>
 *
 * <p>Se utiliza como capa intermedia entre los controladores REST y los repositorios JPA,
 * garantizando separación de responsabilidades y centralización de reglas de negocio.</p>
 *
 * <p>Las operaciones críticas lanzan excepciones de dominio como {@link ResourceNotFoundException},
 * {@link UsuarioAlreadyExistsException} o {@link InvalidUsuarioDataException}.</p>
 */
@Service
public class UsuarioService {

    private final EstacionDeServicioService estacionDeServicioService;
    private final UsuarioRepository usuarioRepository;
    private final ProvinciaRepository provinciaRepository;
    private final CombustibleRepository combustibleRepository;
    private final CombustibleQueryService combustibleQueryService;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     * @param estacionDeServicioService servicio de estaciones de servicio
     * @param provinciaRepository repositorio de provincias
     * @param combustibleQueryService repositorio de combustibles
     */
    public UsuarioService(UsuarioRepository usuarioRepository,
                          EstacionDeServicioService estacionDeServicioService,
                          ProvinciaRepository provinciaRepository,
                          CombustibleRepository combustibleRepository,
                          CombustibleQueryService combustibleQueryService) {
        this.estacionDeServicioService = estacionDeServicioService;
        this.usuarioRepository       = usuarioRepository;
        this.provinciaRepository     = provinciaRepository;
        this.combustibleRepository   = combustibleRepository;
        this.combustibleQueryService = combustibleQueryService;
    }

    /**
     * Obtiene un usuario completo y lo transforma a DTO.
     *
     * @param uuid identificador único del usuario
     * @return DTO del usuario
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public UsuarioDto getUsuario(UUID uuid) {
        Usuario usuario = findUsuarioOrThrow(uuid);
        return UsuarioDto.from(usuario);
    }

    /**
     * Establece si un usuario está ya registrado en la BD.
     *
     * @param uuid identificador único del usuario
     * @return Boolean indicando con true si el usuario existe, false en caso contrario
     */
    public boolean existsUsuario(UUID uuid) {
        return usuarioRepository.existsById(uuid);
    }

    /**
     * Obtiene la provincia favorita de un usuario.
     *
     * @param uuid identificador único del usuario
     * @return identificador de la provincia favorita
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public Short getProvinciaFavorita(UUID uuid) {
        Usuario usuario = findUsuarioOrThrow(uuid);
        return usuario.getProvinciaFavorita().getId();
    }

    /**
     * Obtiene los combustibles favoritos de un usuario.
     *
     * @param uuid identificador único del usuario
     * @return conjunto de identificadores de combustibles favoritos
     * @throws ResourceNotFoundException si el usuario no existe
     */
    public Set<Short> getCombustiblesFavoritos(UUID uuid) {
        Usuario usuario = findUsuarioOrThrow(uuid);

        return usuario.getCombustiblesFavoritos()
                .stream()
                .map(Combustible::getId)
                .collect(Collectors.toSet());
    }

    /**
     * Obtiene los ids de las estaciones de servicio favoritas del usuario..
     *
     * @param uuid identificador único del usuario
     * @return lista de ids de las estaciones de servicio favoritas
     * @throws ResourceNotFoundException si el usuario no existe
     */
    @SneakyThrows
    public List<Integer> getEstacionesDeServicioFavoritasDto(UUID uuid) {
        findUsuarioOrThrow(uuid);
        return usuarioRepository.findEstacionesFavoritasIdsByUuid(uuid);
    }

    /**
     * Crea un nuevo usuario en el sistema.
     *
     * @param dto datos del usuario a crear
     * @throws InvalidUsuarioDataException si el DTO es inválido
     * @throws UsuarioAlreadyExistsException si el usuario ya existe
     * @throws ResourceNotFoundException si la provincia o combustibles no existen
     */
    @Transactional
    public void createUsuario(UsuarioDto dto) {

        if (dto == null || dto.uuid() == null) {
            throw new InvalidUsuarioDataException("El UUID es obligatorio");
        }

        if (usuarioRepository.existsById(dto.uuid())) {
            throw new UsuarioAlreadyExistsException(dto.uuid());
        }

        Provincia provincia = provinciaRepository.findById(dto.id_provincia_favorita())
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

        Usuario usuario = new Usuario(dto.uuid(), provincia);

        if (dto.ids_combustibles_favoritos() == null) {
            usuario.getCombustiblesFavoritos()
                    .addAll(combustibleQueryService.findAllCombustiblesCached());
        } else {
            usuario.getCombustiblesFavoritos()
                    .addAll(dto.ids_combustibles_favoritos()
                                    .stream()
                                    .map(this::getCombustibleOrThrow)
                                    .toList());
        }

        usuarioRepository.save(usuario);
    }

    /**
     * Actualiza la provincia favorita del usuario.
     *
     * @param uuid identificador del usuario
     * @param provinciaId identificador de la nueva provincia
     * @throws ResourceNotFoundException si el usuario o provincia no existen
     */
    @Transactional
    public void updateProvincia(UUID uuid, short provinciaId) {

        Usuario usuario = findUsuarioOrThrow(uuid);

        Provincia provincia = provinciaRepository.findById(provinciaId)
                .orElseThrow(() -> new ResourceNotFoundException("Provincia no encontrada"));

        usuario.setProvinciaFavorita(provincia);

        usuarioRepository.save(usuario);
    }

    /**
     * Reemplaza completamente los combustibles favoritos del usuario.
     *
     * @param uuid identificador del usuario
     * @param ids conjunto de identificadores de combustibles
     * @throws ResourceNotFoundException si algún combustible no existe
     */
    @Transactional
    public void updateCombustiblesFavoritos(UUID uuid, Set<Short> ids) {

        Usuario usuario = findUsuarioOrThrow(uuid);

        Set<Combustible> nuevos = (ids == null) ? new HashSet<>(
                combustibleQueryService.findAllCombustiblesCached())
                : ids.stream()
                .map(this::getCombustibleOrThrow)
                .collect(Collectors.toSet());

        usuario.getCombustiblesFavoritos().clear();
        usuario.getCombustiblesFavoritos().addAll(nuevos);

        usuarioRepository.save(usuario);
    }

    /**
     * Añade una estación de servicio a favoritos del usuario.
     *
     * @param uuid identificador del usuario
     * @param estacionId identificador de la estación
     * @throws ResourceNotFoundException si el usuario o estación no existen
     */
    @Transactional
    public void addEstacionDeServicioFavorita(UUID uuid, int estacionId) {
        Usuario usuario = findUsuarioOrThrow(uuid);

        estacionDeServicioService.existsOrThrow(estacionId);
        EstacionDeServicio estacion = estacionDeServicioService.getEstacionDeServicioById(
                estacionId);

        usuario.getEessFavoritas().add(estacion);

        usuarioRepository.save(usuario);
    }

    /**
     * Elimina una estación de servicio de favoritos del usuario.
     *
     * @param uuid identificador del usuario
     * @param estacionId identificador de la estación
     * @throws ResourceNotFoundException si el usuario o estación no existen
     */
    @Transactional
    public void removeEstacionDeServicioFavorita(UUID uuid, int estacionId) {
        Usuario usuario = findUsuarioOrThrow(uuid);

        estacionDeServicioService.existsOrThrow(estacionId);
        EstacionDeServicio estacion = estacionDeServicioService.getEstacionDeServicioById(
                estacionId);

        usuario.getEessFavoritas().remove(estacion);

        usuarioRepository.save(usuario);
    }

    /**
     * Busca un usuario por UUID o lanza excepción si no existe.
     *
     * @param uuid identificador del usuario
     * @return entidad Usuario
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
    private Combustible getCombustibleOrThrow(short id) {
        return combustibleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Combustible no encontrado: " + id));
    }
}