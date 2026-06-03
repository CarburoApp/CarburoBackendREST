package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.PrecioCombustible;
import app.carburo.api.backend.entities.PrecioCombustibleId;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface PrecioCombustibleRepository
		extends CrudRepository<PrecioCombustible, PrecioCombustibleId> {

	/**
	 * Precios de una estación entre fechas (histórico)
	 */
	List<PrecioCombustible> findByEstacion_IdAndId_FechaBetween(int id,
																LocalDate fechaInicio,
																LocalDate fechaFin);

	/**
	 * Precios de una estación en una fecha concreta
	 */
	List<PrecioCombustible> findByEstacion_IdAndId_Fecha(int id, LocalDate fecha);

	@Query(
			value = "SELECT * FROM preciocombustible WHERE fecha = :fecha AND id_eess IN (:ids)",
			nativeQuery = true
	)
	List<PrecioCombustible> findPreciosHoyByListadoIdEstaciones(
			@Param("ids") List<Integer> ids, @Param("fecha") LocalDate fecha);


	boolean existsById_Fecha(LocalDate today);

	@Query("SELECT " +
			"c.id, c.denominacion, c.codigo, c.idGrupoCombustible, " +
			"ROUND(AVG(p.precio), 3), MAX(p.precio), MIN(p.precio), COUNT(p.estacion.id) " +
			"FROM PrecioCombustible p " +
			"JOIN p.combustible c " +
			"WHERE p.id.fecha = :fecha " +
			"GROUP BY c.id, c.denominacion, c.codigo, c.idGrupoCombustible")
	List<Object[]> findRawEstadisticasGlobalesPorFecha(@Param("fecha") LocalDate fecha);
}
