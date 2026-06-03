package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Usuario;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad Usuario.
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface UsuarioRepository extends CrudRepository<Usuario, UUID> {

	@Query(value = "SELECT id_eess FROM eess_favoritas WHERE uuid_usuario = :uuid", nativeQuery = true)
	List<Integer> findEstacionesFavoritasIdsByUuid(@Param("uuid") UUID uuid);
}
