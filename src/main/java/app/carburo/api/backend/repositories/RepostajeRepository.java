package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Repostaje;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link Repostaje}
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface RepostajeRepository extends CrudRepository<Repostaje, Integer> {


	List<Repostaje> findAllByUsuarioUuidOrderByFechaRepostajeDesc(UUID uuid);

	List<Repostaje> findAllByVehiculoIdOrderByFechaRepostajeDesc(int idVehiculo);
}
