package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.dto.PrecioCombustibleDto;
import app.carburo.api.backend.services.EstacionDeServicioService;
import org.apache.coyote.BadRequestException;
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
	 * Obtiene las estaciones de servicio más cercanas a unas coordenadas.
	 * <p>
	 * Endpoint:
	 * GET /api/v1/public/estaciones-de-servicio/cercanas?latitud={lat}&longitud={lon}&limit={limit}
	 * <p>
	 * - latitud: latitud (obligatoria)
	 * - longitud: longitud (obligatoria)
	 * - limite: número máximo de resultados (opcional, default 1)
	 */
	@GetMapping(API_ENDPOINT_ESTACIONES_DE_SERVICIO_CERCANAS)
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesCercanas(
			@RequestParam double latitud, @RequestParam double longitud,
			@RequestParam(required = false, defaultValue = "1") int limite) {
		if (latitud < -90 || latitud > 90) {
			return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST",
																	  "latitud fuera de rango válido (-90 a 90)"));
		}

		if (longitud < -180 || longitud > 180) {
			return ResponseEntity.badRequest().body(ApiResponse.error("BAD_REQUEST",
																	  "longitud fuera de rango válido (-180 a 180)"));
		}

		if (limite <= 0 || limite > 10) {
			limite = 10;
		}

		List<EstacionDeServicioDto> result = estacionDeServicioService.getEstacionesDeServicioDtoCercanas(
				latitud, longitud, limite);

		return ResponseEntity.ok(ApiResponse.success(result));
	}

	/**
	 * Obtiene la Estación de Servicio con el ID indicado.
	 *
	 * <p>
	 * Endpoint:
	 * GET /api/v1/public/estaciones-servicio/{id}
	 * GET /api/v1/public/estaciones-servicio/{id}?latitud={lat}&longitud={lon}
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
	 * Obtiene los precios de los combustibles de la estacion con el ID indicado durante
	 * los últimos X días.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-servicio/{id}/precios-combustibles?dias=5
	 * </p>
	 *
	 * @param id   ID de la estación de servicio
	 * @param dias Número de días hacia atrás (opcional, default = 5)
	 * @return ResponseEntity con la {@link PrecioCombustibleDto} y código HTTP 200 OK
	 */
	@GetMapping("/{id}/precios-combustibles")
	public ResponseEntity<ApiResponse<List<PrecioCombustibleDto>>> doGetPreciosDeCombustiblesByEstacionDeServicioId(
			@PathVariable int id, @RequestParam(defaultValue = "5") int dias)
			throws BadRequestException {

		List<PrecioCombustibleDto> preciosDeCombustiblesDto = estacionDeServicioService.getPreciosDeCombustiblesDtoByEstacionDeServicioId(
				id, dias);

		return ResponseEntity.ok(ApiResponse.success(preciosDeCombustiblesDto));
	}

	/**
	 * Obtiene los precios de los combustibles de la estacion con el ID indicado de la fecha indicada.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/estaciones-servicio/{id}/precios-combustibles/{fecha}
	 * </p>
	 *
	 * @param id    ID de la estación de servicio
	 * @param fecha Fecha del día en el que se desean los precios
	 * @return ResponseEntity con la {@link PrecioCombustibleDto} y código HTTP 200 OK
	 */
	@GetMapping("/{id}/precios-combustibles/{fecha}")
	public ResponseEntity<ApiResponse<List<PrecioCombustibleDto>>> doGetPreciosDeCombustiblesByFecha(
			@PathVariable int id, @PathVariable LocalDate fecha)
			throws BadRequestException {

		List<PrecioCombustibleDto> precios = estacionDeServicioService.getPreciosDeCombustiblesDtoByEstacionDeServicioIdAndFecha(
				id, fecha);

		return ResponseEntity.ok(ApiResponse.success(precios));
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
}