package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.ComunidadAutonomaDto;
import app.carburo.api.backend.services.ComunidadAutonomaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_COMUNIDADES_AUTONOMAS;

/**
 * Controlador REST público de comunidadesAutonomas.
 * <p>
 * Expone endpoints de lectura de comunidadesAutonomas dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/comunidadesAutonomas
 */
@RestController
@RequestMapping(API_ENDPOINT_COMUNIDADES_AUTONOMAS)
public class ComunidadAutonomaRestController {

	private final ComunidadAutonomaService comunidadesAutonomasService;

	/**
	 * Inyección de dependencias del servicio de comunidadesAutonomas.
	 */
	public ComunidadAutonomaRestController(
			ComunidadAutonomaService comunidadesAutonomasService) {
		this.comunidadesAutonomasService = comunidadesAutonomasService;
	}

	/**
	 * Obtiene el listado completo de comunidadesAutonomas disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/comunidadesAutonomas
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link ComunidadAutonomaDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<ComunidadAutonomaDto>>> doGetComunidadesAutonomas() {
		List<ComunidadAutonomaDto> comunidadesAutonomas = comunidadesAutonomasService.getComunidadesAutonomasDTO();
		return ResponseEntity.ok(ApiResponse.success(comunidadesAutonomas));
	}
}