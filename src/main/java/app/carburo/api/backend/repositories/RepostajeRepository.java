package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.Repostaje;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link Repostaje}
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface RepostajeRepository extends CrudRepository<Repostaje, Integer> {


	List<Repostaje> findAllByUsuarioUuidOrderByFechaRepostajeDesc(UUID uuid);

	List<Repostaje> findAllByVehiculoIdOrderByFechaRepostajeDesc(int idVehiculo);

	/**
	 * Comprueba si existe algún repostaje para un vehículo cuyo rango de odómetros se solape
	 * con el rango proporcionado, excluyendo opcionalmente un ID de repostaje (para actualizaciones).
	 */
	@Query("""
        SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END 
        FROM Repostaje r 
        WHERE r.vehiculo.id = :idVehiculo 
          AND (:idRepostajeExcluir IS NULL OR r.id <> :idRepostajeExcluir)
          AND (
               (r.odometroInicial IS NOT NULL AND :odometroInicial IS NOT NULL 
                AND r.odometroInicial < :odometroFinal AND r.odometroFinal > :odometroInicial)
               OR
               (:odometroInicial IS NULL AND r.odometroFinal > COALESCE(r.odometroInicial, 0) 
                AND :odometroFinal > COALESCE(r.odometroInicial, 0))
               OR
               (r.odometroInicial IS NULL AND :odometroInicial IS NOT NULL 
                AND r.odometroFinal > :odometroInicial)
               OR
               (:odometroFinal = r.odometroFinal)
               OR
               (:odometroFinal >= COALESCE(r.odometroInicial, 0) AND :odometroFinal <= r.odometroFinal)
          )
    """)
	boolean existsSolapamientoOdomatros(
			@Param("idVehiculo") int idVehiculo,
			@Param("odometroInicial") BigDecimal odometroInicial, // Puede recibir null
			@Param("odometroFinal") BigDecimal odometroFinal,
			@Param("idRepostajeExcluir") Integer idRepostajeExcluir
									   );
}
