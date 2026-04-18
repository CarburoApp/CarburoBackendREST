package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.UsuarioDto;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.Provincia;
import app.carburo.api.backend.entities.Usuario;
import app.carburo.api.backend.exceptions.InvalidUsuarioDataException;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.exceptions.UsuarioAlreadyExistsException;
import app.carburo.api.backend.repositories.CombustibleRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import app.carburo.api.backend.repositories.UsuarioRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

/**
 * Servicio encargado de la gestión de usuarios del sistema.
 * <p>
 * Proporciona operaciones relacionadas con la persistencia y actualización
 * de la entidad {@link Usuario}, desacoplando la lógica de negocio del acceso
 * directo al repositorio.
 * </p>
 *
 * <p>
 * Este servicio se utiliza principalmente como apoyo al sistema de autenticación
 * (Supabase + Spring Security).:
 * </p>
 */
@Service
public class UsuarioService {

    /**
     * Repositorio de acceso a datos de usuarios.
     */
    private final UsuarioRepository usuarioRepository;
    private final ProvinciaRepository provinciaRepository;
    private final CombustibleRepository combustibleRepository;

    /**
     * Constructor con inyección de dependencias.
     *
     * @param usuarioRepository repositorio de usuarios
     */
    public UsuarioService(UsuarioRepository usuarioRepository,
                          ProvinciaRepository provinciaRepository,
                          CombustibleRepository combustibleRepository) {
        this.usuarioRepository     = usuarioRepository;
        this.provinciaRepository   = provinciaRepository;
        this.combustibleRepository = combustibleRepository;
    }

    public UsuarioDto getUsuario(UUID uuid) {
        Usuario usuario = usuarioRepository.findById(uuid).orElseThrow(
                () -> new ResourceNotFoundException("Usuario no encontrado"));

        return UsuarioDto.from(usuario);
    }


    @Transactional
    public void createUsuario(UsuarioDto dto) {
        if (dto == null || dto.uuid() == null) {
            throw new InvalidUsuarioDataException("El UUID es obligatorio");
        }

        if (usuarioRepository.existsById(dto.uuid())) {
            throw new UsuarioAlreadyExistsException(dto.uuid());
        }

        // Provincia
        Provincia provincia = provinciaRepository.findById(dto.id_provincia_favorita())
                .orElseThrow(
                        () -> new ResourceNotFoundException("Provincia no encontrada"));

        Usuario usuario = new Usuario(dto.uuid(), provincia);

        // Combustibles
        if (dto.ids_combustibles_favoritos() == null) {
            usuario.getCombustiblesFavoritos().addAll(combustibleRepository.findAll());

        } else {
            usuario.getCombustiblesFavoritos()
                    .addAll(dto.ids_combustibles_favoritos().stream()
                                    .map(id -> combustibleRepository.findById(id)
                                            .orElseThrow(
                                                    () -> new ResourceNotFoundException(
                                                            "Combustible no encontrado con id: " +
                                                                    id))).toList());
        }

        usuarioRepository.save(usuario);
    }


    /**
     * Actualiza la provincia favorita de un usuario.
     *
     * @param id        identificador UUID del usuario
     * @param provincia nueva provincia favorita
     * @throws IllegalArgumentException si alguno de los parámetros es {@code null}
     */
    public void updateProvinciaFavoritaUsuario(UUID id, Provincia provincia) {
        if (id == null || provincia == null) {
            throw new IllegalArgumentException("Parámetros inválidos para la actualización de provincia.");
        }

        Optional<Usuario> usuarioOpt = usuarioRepository.findById(id);
        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            usuario.setProvinciaFavorita(provincia);
            usuarioRepository.save(usuario);
        }
    }

    /**
     * Busca un usuario por su identificador UUID.
     *
     * @param uuid identificador del usuario
     * @return {@link Optional} con el usuario si existe, o {@link Optional#empty()} en caso contrario
     * @throws IllegalArgumentException si el UUID es {@code null}
     */
    public Optional<Usuario> findByUUID(UUID uuid) {
        if (uuid == null) {
            throw new IllegalArgumentException("El UUID no puede ser nulo.");
        }
        return usuarioRepository.findById(uuid);
    }

    /**
     * Comprueba si una estación de servicio está marcada como favorita por un usuario.
     *
     * @param usuario            Usuario a comprobar
     * @param estacionDeServicio Estación de servicio
     * @return true si la estación es favorita del usuario, false en caso contrario
     * @throws IllegalArgumentException si alguno de los parámetros es null
     */
    public boolean isEstacionDeServicioFavorita(Usuario usuario,
                                                EstacionDeServicio estacionDeServicio) {
        if (usuario == null || estacionDeServicio == null)
            throw new IllegalArgumentException(
                    "Parámetros inválidos para la comprobación de estación de servicio favorita para el usuario.");
        return usuario.getEessFavoritas().contains(estacionDeServicio);
    }

    /**
     * Añade una estación de servicio a la lista de favoritas del usuario.
     *
     * @param usuario            Usuario propietario de las favoritas
     * @param estacionDeServicio Estación a añadir
     * @throws IllegalArgumentException si alguno de los parámetros es null
     */
    @Transactional
    public void addEstacionDeServicioFavorita(Usuario usuario,
                                              EstacionDeServicio estacionDeServicio) {
        if (isEstacionDeServicioFavorita(usuario, estacionDeServicio)) return;
        if (usuario == null || estacionDeServicio == null)
            throw new IllegalArgumentException(
                    "Parámetros inválidos para la comprobación de estación de servicio favorita para el usuario.");
        usuario.getEessFavoritas().add(estacionDeServicio);
        usuarioRepository.save(usuario);
    }

    /**
     * Elimina una estación de servicio de la lista de favoritas del usuario.
     *
     * @param usuario            Usuario propietario de las favoritas
     * @param estacionDeServicio Estación a eliminar
     * @throws IllegalArgumentException si alguno de los parámetros es null
     */
    @Transactional
    public void removeEstacionDeServicioFavorita(Usuario usuario,
                                                 EstacionDeServicio estacionDeServicio) {
        if (!isEstacionDeServicioFavorita(usuario, estacionDeServicio)) return;
        if (usuario == null || estacionDeServicio == null)
            throw new IllegalArgumentException(
                    "Parámetros inválidos para la comprobación de estación de servicio favorita para el usuario.");
        usuario.getEessFavoritas().remove(estacionDeServicio);
        usuarioRepository.save(usuario);
    }
}