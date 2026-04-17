package app.carburo.api.backend.repositories;


import app.carburo.api.backend.entities.Combustible;
import org.springframework.data.repository.CrudRepository;

public interface CombustibleRepository extends CrudRepository<Combustible, Short> {}
