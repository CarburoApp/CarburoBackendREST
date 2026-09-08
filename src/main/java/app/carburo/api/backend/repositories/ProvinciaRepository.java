package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.ComunidadAutonoma;
import app.carburo.api.backend.entities.Provincia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Provincia.
 * Contiene métodos de acceso y consulta de provincias,
 * incluyendo filtrado por comunidad autónoma, ordenación ...
 */
public interface ProvinciaRepository extends CrudRepository<Provincia, Short> {

	/**
	 * Devuelve todas las provincias pertenecientes a una comunidad autónoma.
	 *
	 * @param comunidadAutonoma comunidad autónoma asociada
	 */
	Iterable<Provincia> findAllByComunidadAutonoma(ComunidadAutonoma comunidadAutonoma);

	@Query("""
       SELECT p
       FROM Provincia p
       WHERE p.comunidadAutonoma.id = :comunidadAutonoma
       ORDER BY p.denominacion ASC
       """)
	List<Provincia> findProvinciaByComunidadAutonoma(@Param("comunidadAutonoma") short idComunidadAutonoma);
	/**
	 * Devuelve una provincia según su identificador.
	 *
	 * @param provinciaFavorita ID de la provincia
	 */
	Optional<Provincia> findProvinciaById(short provinciaFavorita);

	/** Devuelve todas las provincias */
	Iterable<Provincia> findAll();

	/**
	 * Devuelve todas las provincias ordenadas alfabéticamente por denominación.
	 */
	@Query("SELECT p FROM Provincia p ORDER BY p.denominacion ASC")
	Iterable<Provincia> findAllOrderByDenominacion();
}
