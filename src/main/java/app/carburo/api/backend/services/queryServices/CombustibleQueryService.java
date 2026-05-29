package app.carburo.api.backend.services.queryServices;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.repositories.CombustibleRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Servicio de consulta de combustibles con caché en memoria.
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

	public CombustibleQueryService(CombustibleRepository combustibleRepository) {
		this.combustibleRepository = combustibleRepository;
	}

	/**
	 * Devuelve todos los combustibles.
	 *
	 * <p>
	 * Primera llamada: consulta a BD.
	 * Siguientes llamadas: respuesta desde caché "combustibles".
	 * </p>
	 */
	@Cacheable(value = "combustibles")
	public List<Combustible> findAllCached() {
		return combustibleRepository.findAll();
	}
}