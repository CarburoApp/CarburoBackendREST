package app.carburo.api.backend.controllers.v1.publicc;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.services.EstacionDeServicioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Controlador REST público de EstacionesDeServicio.
 * <p>
 * Expone endpoints de lectura de EstacionesDeServicio dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/EstacionesDeServicio
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

	/**
	 * Obtiene el listado completo de EstacionesDeServicio disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/EstacionesDeServicio
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link EstacionDeServicioDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesDeServicio() {
		List<EstacionDeServicioDto> estacionesDeServicio = estacionDeServicioService.getEstacionesDeServicioDto();
		return ResponseEntity.ok(ApiResponse.success(estacionesDeServicio));
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

	/**
	 * Obtiene las estaciones de servicio más cercanas a unas coordenadas.
	 * <p>
	 * Endpoint:
	 * GET /api/v1/public/estaciones-de-servicio/cercanas?lat={lat}&lon={lon}&limit={limit}
	 * <p>
	 * - lat: latitud (obligatoria)
	 * - lon: longitud (obligatoria)
	 * - limit: número máximo de resultados (opcional, default 1)
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_CERCANAS)
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesCercanas(
			@RequestParam double lat, @RequestParam double lon,
			@RequestParam(required = false, defaultValue = "1") int limit) {
		if (lat < -90 || lat > 90) {
			return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST",
																	  "latitud fuera de rango válido (-90 a 90)"));
		}

		if (lon < -180 || lon > 180) {
			return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST",
																	  "longitud fuera de rango válido (-180 a 180)"));
		}

		if (limit <= 0 || limit > 10) {
			limit = 10;
		}

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoCercanas(
				lat, lon, limit);

		return ResponseEntity.ok(ApiResponse.success(result));
	}
}