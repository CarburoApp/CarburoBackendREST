package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.ProvinciaDto;
import app.carburo.api.backend.services.ProvinciaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_PROVINCIAS;
import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_PROVINCIAS_COMUNIDAD_AUTONOMA;

/**
 * Controlador REST público de provincias.
 * <p>
 * Expone endpoints de lectura de provincias dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/provincias
 */
@RestController
@RequestMapping(API_ENDPOINT_PROVINCIAS)
public class ProvinciaRestController {

	private final ProvinciaService provinciaService;

	/**
	 * Inyección de dependencias del servicio de provincias.
	 */
	public ProvinciaRestController(ProvinciaService provinciaService) {
		this.provinciaService = provinciaService;
	}

	/**
	 * Obtiene el listado completo de provincias disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/provincias
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link ProvinciaDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<ProvinciaDto>>> doGetProvincias() {
		List<ProvinciaDto> provincias = provinciaService.getProvinciasDTO();
		return ResponseEntity.ok(ApiResponse.success(provincias));
	}

	/**
	 * Obtiene el listado de provincias pertenecientes a una comunidad autónoma.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/provincias/comunidad-autonoma/{id}
	 * </p>
	 *
	 * @param id identificador de la comunidad autónoma
	 * @return {@link ResponseEntity} con lista de {@link ProvinciaDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping(API_ENDPOINT_PROVINCIAS_COMUNIDAD_AUTONOMA + "/{id}")
	public ResponseEntity<ApiResponse<List<ProvinciaDto>>> doGetProvinciasByComunidadAutonoma(
			@PathVariable short id) {

		List<ProvinciaDto> provincias = provinciaService.getProvinciasDTOByComunidadAutonoma(
				id);

		return ResponseEntity.ok(ApiResponse.success(provincias));
	}

}