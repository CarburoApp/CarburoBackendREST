package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.dto.EstadisticaCombustibleDto;
import app.carburo.api.backend.dto.PrecioCombustibleDto;
import app.carburo.api.backend.services.EstacionDeServicioService;
import org.apache.coyote.BadRequestException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Controlador REST público de EstacionesDeServicio.
 * <p>
 * Expone endpoints de lectura de EstacionesDeServicio dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/estaciones-de-servicio
 */
@RestController
@RequestMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO)
public class EstacionDeServicioRestController {

	private final EstacionDeServicioService estacionDeServicioService;

	/**
	 * Inyección de dependencias de los servicios.
	 */
	public EstacionDeServicioRestController(
			EstacionDeServicioService estacionDeServicioService){
		this.estacionDeServicioService = estacionDeServicioService;
	}

	// DATOS GENÉRICOS -------------------------------------------------------------------

	/**
	 * Obtiene el número total de EstacionesDeServicio disponibles a nivel nacional.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/count
	 * </p>
	 *
	 * @return {@link ResponseEntity} con el conteo total de Estaciones de Servicio
	 * y código HTTP 200 OK
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_TOTALES)
	public ResponseEntity<ApiResponse<Long>> doGetTotalEstacionesDeServicio() {
		return ResponseEntity.ok(ApiResponse.success(
				estacionDeServicioService.getTotalEstacionesDeServicio()));
	}

	/**
	 * Obtiene un resumen analítico nacional (media, max, min, volumen) de todos los
	 * tipos de combustibles procesados durante la jornada actual.
	 * * <p>
	 * Endpoint:
	 * - GET /api/v1/public/estaciones-de-servicio/precios-combustibles/analiticas
	 * - GET /api/v1/public/estaciones-de-servicio/precios-combustibles/analiticas?fecha=2026-06-03
	 * </p>
	 *
	 * @return {@link ResponseEntity} con el listado de métricas {@link EstadisticaCombustibleDto}
	 * @throws BadRequestException si la fecha proporcionada es inválida o está en el futuro
	 */
	@GetMapping("/precios-combustibles/analiticas")
	public ResponseEntity<ApiResponse<List<EstadisticaCombustibleDto>>> doGetEstadisticasPrecios(
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
			LocalDate fecha) throws BadRequestException {
		List<EstadisticaCombustibleDto> estadisticas = estacionDeServicioService.getEstadisticasCombustibles(
				fecha);

		return ResponseEntity.ok(ApiResponse.success(estadisticas));
	}

	/**
	 * Obtiene los datos detallados de múltiples Estaciones de Servicio por sus IDs sin incluir precios.
	 * Permite pasar coordenadas opcionales para inyectar la distancia desde un punto geográfico.
	 * <p>
	 * Endpoints:
	 * - GET /api/v1/public/estaciones-de-servicio?ids=1,5,12
	 * - GET /api/v1/public/estaciones-de-servicio?ids=1,5,12&latitud=43.53&longitud=-5.66</p>
	 * </p>
	 *
	 * @param ids Lista opcional de IDs de las estaciones de servicio
	 * @return {@link ResponseEntity} con la lista de {@link EstacionDeServicioDto}
	 * @throws BadRequestException si la lista de IDs es inválida o excede el límite
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesDeServicio(
			@RequestParam List<Integer> ids,
			@RequestParam(required = false) Double latitud,
			@RequestParam(required = false) Double longitud) throws BadRequestException {
		if (ids == null || ids.isEmpty())
			return ResponseEntity.ok(ApiResponse.success(List.of()));

		List<EstacionDeServicioDto> estaciones;

		if (latitud != null && longitud != null) {
			estaciones = estacionDeServicioService.getEstacionesDeServicioDtoByIdsConDistanciaYSinPrecios(
					ids, latitud, longitud);
		} else {
			estaciones = estacionDeServicioService.getEstacionesDeServicioDtoByIdsSinPrecios(
					ids);
		}

		return ResponseEntity.ok(ApiResponse.success(estaciones));
	}

	/**
	 * Obtiene la Estación de Servicio con el ID indicado.
	 *
	 * <p>
	 * Endpoint:
	 * GET /api/v1/public/estaciones-de-servicio/{id}
	 * GET /api/v1/public/estaciones-de-servicio/{id}?latitud={lat}&longitud={lon}
	 * </p>
	 *
	 * <p>
	 * Si se proporcionan latitud y longitud válidas, se incluye la distancia
	 * desde el punto indicado hasta la estación de servicio.
	 * En caso contrario, la estación se devuelve sin cálculo de distancia.
	 * </p>
	 *
	 * @param id identificador de la estación de servicio
	 * @param latitud latitud opcional del punto de referencia
	 * @param longitud longitud opcional del punto de referencia
	 * @return {@link ResponseEntity} con la {@link EstacionDeServicioDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<EstacionDeServicioDto>> doGetEstacionDeServicio(
			@PathVariable int id, @RequestParam(required = false) Double latitud,
			@RequestParam(required = false) Double longitud) {

		EstacionDeServicioDto es;

		boolean tieneCoordenadas =
				latitud != null && longitud != null && !Double.isNaN(latitud) &&
						!Double.isNaN(longitud);

		if (tieneCoordenadas) {
			es = estacionDeServicioService.getEstacionDeServicioDtoById(id, latitud,
																		longitud);
		} else {
			es = estacionDeServicioService.getEstacionDeServicioDtoById(id);
		}

		return ResponseEntity.ok(ApiResponse.success(es));
	}

	/**
	 * Obtiene las estaciones de servicio más cercanas a unas coordenadas.
	 * <p>
	 * Endpoint:
	 * GET /api/v1/public/estaciones-de-servicio/cercanas?latitud={lat}&longitud={lon}&limit={limit}
	 * <p>
	 * - latitud: latitud (obligatoria)
	 * - longitud: longitud (obligatoria)
	 * - limite: número máximo de resultados (opcional, default 1)
	 *
	 * @throws BadRequestException si las coordenadas son inválidas o el límite es negativo
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_CERCANAS)
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesCercanas(
			@RequestParam double latitud, @RequestParam double longitud,
			@RequestParam(required = false, defaultValue = "1") int limite)
			throws BadRequestException {
		if (limite <= 0 || limite > 10) limite = 10;

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoCercanas(
				latitud, longitud, limite);

		return ResponseEntity.ok(ApiResponse.success(result));
	}

	/**
	 * Obtiene estaciones por municipio.
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/municipio/{id}
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_MUNICIPIO + "/{id}")
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesDeServicioByMunicipio(
			@PathVariable short id) {

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoByMunicipio(
				id);

		return ResponseEntity.ok(ApiResponse.success(result));
	}


	/**
	 * Obtiene estaciones por provincia.
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/provincia/{id}
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_PROVINCIA + "/{id}")
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesDeServicioByProvincia(
			@PathVariable short id) {

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoByProvincia(
				id);

		return ResponseEntity.ok(ApiResponse.success(result));
	}


	/**
	 * Obtiene estaciones por comunidad autónoma.
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/comunidad-autonoma/{id}
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_COMUNIDAD_AUTONOMA + "/{id}")
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesDeServicioByComunidadAutonoma(
			@PathVariable short id) {

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoByComunidadAutonoma(
				id);

		return ResponseEntity.ok(ApiResponse.success(result));
	}

	// PRECIOS DE COMBUSTIBLES -----------------------------------------------------------

	/**
	 * Obtiene los precios actuales de los combustibles para un listado de estaciones de servicio.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/precios-combustibles?ids=1,2,3
	 * </p>
	 *
	 * @param ids Lista de IDs de las estaciones de servicio
	 * @return ResponseEntity con la lista de {@link PrecioCombustibleDto} y código HTTP 200 OK
	 * @throws BadRequestException si la lista de IDs está vacía o supera el límite permitido
	 */
	@GetMapping("/precios-combustibles")
	public ResponseEntity<ApiResponse<List<PrecioCombustibleDto>>> doGetPreciosDeCombustiblesByEstacionesIds(
			@RequestParam List<Integer> ids) throws BadRequestException {
		List<PrecioCombustibleDto> preciosDeCombustiblesDto = estacionDeServicioService.getPreciosDeCombustiblesDtoByEstacionesIds(
				ids);

		return ResponseEntity.ok(ApiResponse.success(preciosDeCombustiblesDto));
	}

	/**
	 * Obtiene los precios de los combustibles de la estacion con el ID indicado durante
	 * los últimos X días.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/{id}/precios-combustibles?dias=5
	 * </p>
	 *
	 * @param id   ID de la estación de servicio
	 * @param dias Número de días hacia atrás (opcional, default = 5)
	 * @return ResponseEntity con la {@link PrecioCombustibleDto} y código HTTP 200 OK
	 */
	@GetMapping("/{id}/precios-combustibles")
	public ResponseEntity<ApiResponse<List<PrecioCombustibleDto>>> doGetPreciosDeCombustiblesByEstacionDeServicioId(
			@PathVariable int id, @RequestParam(defaultValue = "1") int dias)
			throws BadRequestException {

		List<PrecioCombustibleDto> preciosDeCombustiblesDto = estacionDeServicioService.getPreciosDeCombustiblesDtoByEstacionDeServicioId(
				id, dias);

		return ResponseEntity.ok(ApiResponse.success(preciosDeCombustiblesDto));
	}

	/**
	 * Obtiene los precios de los combustibles de la estacion con el ID indicado de la fecha indicada.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-de-servicio/{id}/precios-combustibles/{fecha}
	 * </p>
	 *
	 * @param id    ID de la estación de servicio
	 * @param fecha Fecha del día en el que se desean los precios. Formato ISO (YYYY-MM-DD).
	 * @return ResponseEntity con la {@link PrecioCombustibleDto} y código HTTP 200 OK
	 */
	@GetMapping("/{id}/precios-combustibles/{fecha}")
	public ResponseEntity<ApiResponse<List<PrecioCombustibleDto>>> doGetPreciosDeCombustiblesByFecha(
			@PathVariable int id, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha)
			throws BadRequestException {

		List<PrecioCombustibleDto> precios = estacionDeServicioService.getPreciosDeCombustiblesDtoByEstacionDeServicioIdAndFecha(
				id, fecha);

		return ResponseEntity.ok(ApiResponse.success(precios));
	}
}