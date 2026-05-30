package app.carburo.api.backend.services.queryServices;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.GrupoCombustible;
import app.carburo.api.backend.repositories.CombustibleRepository;
import app.carburo.api.backend.repositories.GrupoCombustibleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de consulta de combustibles y grupos de combustibles con caché en memoria.
 *
 * <p>
 * Centraliza el acceso a datos de combustibles y evita accesos directos al repositorio
 * desde otros servicios, permitiendo aplicar caching de forma consistente.
 * </p>
 *
 * <p>
 * Los combustibles son datos de referencia que no cambian, por lo que se cachean
 * para reducir carga en base de datos en endpoints de alta frecuencia.
 * </p>
 */
@Service
public class CombustibleQueryService {

	private final CombustibleRepository combustibleRepository;
	private final GrupoCombustibleRepository grupoCombustibleRepository;

	public CombustibleQueryService(CombustibleRepository combustibleRepository,
								   GrupoCombustibleRepository grupoCombustibleRepository) {
		this.grupoCombustibleRepository = grupoCombustibleRepository;
		this.combustibleRepository = combustibleRepository;
	}

	/**
	 * Devuelve todos los combustibles.
	 *
	 * <p>
	 * Primera llamada: consulta a BD.
	 * Siguientes llamadas: respuesta desde caché "entities_combustibles".
	 * </p>
	 */
	@Cacheable(value = "entities_combustibles")
	public List<Combustible> findAllCombustiblesCached() {
		return combustibleRepository.findAll();
	}

	/**
	 * Devuelve todos los grupos de combustibles.
	 *
	 * <p>
	 * Primera llamada: consulta a BD.
	 * Siguientes llamadas: respuesta desde caché "entities_grupo_combustibles".
	 * </p>
	 */
	@Cacheable(value = "entities_grupo_combustibles")
	public List<GrupoCombustible> findAllGruposDeCombustiblesCached() {
		return grupoCombustibleRepository.findAll();
	}
}