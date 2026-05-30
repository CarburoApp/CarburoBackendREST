package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.dto.GrupoCombustibleDto;
import app.carburo.api.backend.services.CombustibleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Controlador REST público de combustibles.
 * <p>
 * Expone endpoints de lectura de combustibles dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 */
@RestController
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
	@GetMapping(API_ENDPOINT_COMBUSTIBLES)
	public ResponseEntity<ApiResponse<List<CombustibleDto>>> doGetCombustibles() {
		List<CombustibleDto> combustibles = combustibleService.getCombustiblesDto();
		return ResponseEntity.ok(ApiResponse.success(combustibles));
	}

	/**
	 * Obtiene el listado completo de grupos de combustibles disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/grupos-de-combustibles
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link GrupoCombustibleDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping(API_ENDPOINT_GRUPO_COMBUSTIBLES)
	public ResponseEntity<ApiResponse<List<GrupoCombustibleDto>>> doGetGrupoCombustibles() {
		List<GrupoCombustibleDto> grupoCombustibles = combustibleService.getGruposDeCombustibles();
		return ResponseEntity.ok(ApiResponse.success(grupoCombustibles));
	}

	/**
	 * Obtiene el listado completo de grupos de combustibles disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/grupos-de-combustibles/combustibles
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link GrupoCombustibleDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping(API_ENDPOINT_GRUPO_COMBUSTIBLES_CON_COMBUSTIBLES)
	public ResponseEntity<ApiResponse<List<GrupoCombustibleDto>>> doGetGrupoCombustiblesConCombustibles() {
		List<GrupoCombustibleDto> combustibles = combustibleService.getGruposDeCombustiblesConCombustibles();
		return ResponseEntity.ok(ApiResponse.success(combustibles));
	}
}