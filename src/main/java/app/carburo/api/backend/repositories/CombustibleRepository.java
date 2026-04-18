package app.carburo.api.backend.repositories;


import app.carburo.api.backend.entities.Combustible;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CombustibleRepository extends CrudRepository<Combustible, Short> {

	List<Combustible> findAll();
}
