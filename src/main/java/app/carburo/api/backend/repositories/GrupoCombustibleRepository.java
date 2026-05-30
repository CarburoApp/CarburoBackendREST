package app.carburo.api.backend.repositories;


import app.carburo.api.backend.entities.GrupoCombustible;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface GrupoCombustibleRepository extends CrudRepository<GrupoCombustible, Short> {

	List<GrupoCombustible> findAll();
}
