package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Usuario;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface UsuarioRepository extends CrudRepository<Usuario, UUID> {

}
