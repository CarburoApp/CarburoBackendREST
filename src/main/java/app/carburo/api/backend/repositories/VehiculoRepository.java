package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Vehiculo;
import org.springframework.data.repository.CrudRepository;

/**
 * Repositorio para la entidad {@link Vehiculo}
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface VehiculoRepository extends CrudRepository<Vehiculo, Integer> {

}
