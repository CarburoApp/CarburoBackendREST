package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Municipio;
import app.carburo.api.backend.entities.Provincia;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repositorio para la entidad Municipio.
 * Permite operaciones CRUD básicas y consultas personalizadas.
 */
public interface MunicipioRepository extends CrudRepository<Municipio, Short> {

	@Query("SELECT DISTINCT m " +
			"FROM Municipio m " +
			"WHERE m.provincia.id = :provincia " +
			"ORDER BY m.denominacion ASC")
	List<Municipio> findMunicipioByProvincia(@Param("provincia") short idProvincia);

	@Query("SELECT DISTINCT m " +
			"FROM Municipio m " +
			"JOIN m.estacionesDeServicio e " +
			"WHERE m.provincia.id = :provincia " +
			"ORDER BY m.denominacion ASC")
	List<Municipio> findMunicipioByProvinciaConEESS(@Param("provincia") short provinciaId);

	@Query("SELECT DISTINCT m " +
			"FROM Municipio m " +
			"JOIN m.estacionesDeServicio e " +
			"WHERE m.provincia.comunidadAutonoma.id = :comunidadAutonoma " +
			"ORDER BY m.denominacion ASC")
	List<Municipio> findMunicipioByComunidadAutonomaConEESS(
			@Param("comunidadAutonoma") short comunidadAutonomaId);

	Provincia id(short id);
}
