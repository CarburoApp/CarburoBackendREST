package app.carburo.api.backend.services;

import app.carburo.api.backend.repositories.PrecioCombustibleRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Servicio encargado de determinar qué fecha de precios de combustible
 * es la válida en cada momento.
 *
 * <p>
 * Se utiliza para resolver el problema de transición diaria (medianoche),
 * donde los precios del día actual pueden no estar todavía cargados.
 * </p>
 */
@Service
public class PrecioStateService {

	private final PrecioCombustibleRepository precioCombustibleRepository;

	/**
	 * Fecha de precios considerada válida actualmente.
	 * Se mantiene en memoria para evitar consultas repetitivas a BD.
	 */
	private volatile LocalDate cachedValidDate = null;

	/**
	 * Marca temporal del último cálculo de la fecha válida.
	 * Se usa junto con CACHE_TTL_MS para evitar recalcular constantemente.
	 */
	private volatile long lastCheck = 0;

	/**
	 * Tiempo de vida del cache en milisegundos.
	 * Evita consultas innecesarias a base de datos en intervalos cortos.
	 */
	private static final long CACHE_TTL_MS = 60_000; // 1 minuto

	public PrecioStateService(PrecioCombustibleRepository precioCombustibleRepository) {
		this.precioCombustibleRepository = precioCombustibleRepository;
	}

	/**
	 * Determina la mejor fecha disponible para los precios.
	 *
	 * <p>
	 * Lógica:
	 * 1. Si existe cache válido y no ha expirado → se reutiliza.
	 * 2. Si hay precios para hoy → se usa hoy.
	 * 3. Si no hay precios hoy → se usa ayer como fallback.
	 * 4. Si no hay datos en ninguno → se devuelve hoy por defecto.
	 * </p>
	 *
	 * @return fecha más adecuada para consultar precios de combustible
	 */
	public LocalDate getBestDate() {

		LocalDate today = LocalDate.now();
		long now = System.currentTimeMillis();

		// Reutiliza cache si sigue dentro del tiempo de vida
		if (cachedValidDate != null && (now - lastCheck) < CACHE_TTL_MS) {
			return cachedValidDate;
		}

		// Actualiza timestamp de última comprobación
		lastCheck = now;

		// 1. Intento con precios del día actual
		if (precioCombustibleRepository.existsById_Fecha(today)) {
			cachedValidDate = today;
			return today;
		}

		// 2. Fallback a día anterior (caso típico de retraso de carga nocturna)
		LocalDate yesterday = today.minusDays(1);

		if (precioCombustibleRepository.existsById_Fecha(yesterday)) {
			cachedValidDate = yesterday;
			return yesterday;
		}

		// 3. Fallback extremo (evita nulls en sistema)
		cachedValidDate = today;
		return today;
	}
}