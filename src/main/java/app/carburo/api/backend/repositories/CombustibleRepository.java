package app.carburo.api.backend.repositories;


import app.carburo.api.backend.entities.Combustible;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface CombustibleRepository extends CrudRepository<Combustible, Short> {

	List<Combustible> findAll();

	Set<Combustible> findAllByIdIn(Set<Short> shorts);

	@Query("SELECT c.id FROM Combustible c")
	Set<Short> findAllIds();

	boolean existsAllByIdIn(Collection<Short> ids);
}
