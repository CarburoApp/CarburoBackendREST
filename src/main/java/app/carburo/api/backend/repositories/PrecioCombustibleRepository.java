package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.PrecioCombustible;
import app.carburo.api.backend.entities.PrecioCombustibleId;
import org.springframework.data.repository.CrudRepository;

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
}
