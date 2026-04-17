package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.services.CombustibleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_COMBUSTIBLES;

/**
 * Controlador REST público de combustibles.
 * <p>
 * Expone endpoints de lectura de combustibles dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/combustibles
 */
@RestController
@RequestMapping(API_ENDPOINT_COMBUSTIBLES)
public class CombustibleRestController {

	private final CombustibleService combustibleService;

	/**
	 * Inyección de dependencias del servicio de combustibles.
	 */
	public CombustibleRestController(CombustibleService combustibleService) {
		this.combustibleService = combustibleService;
	}

	/**
	 * Obtiene el listado completo de combustibles disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/combustibles
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link CombustibleDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<CombustibleDto>>> doGetCombustibles() {
		List<CombustibleDto> combustibles = combustibleService.getCombustiblesDto();
		return ResponseEntity.ok(ApiResponse.success(combustibles));
	}
}