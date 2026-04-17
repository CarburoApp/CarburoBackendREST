package app.carburo.api.backend.controllers.v1.publico;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.MunicipioDto;
import app.carburo.api.backend.services.MunicipioService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Controlador REST público de municipios.
 * <p>
 * Expone endpoints de lectura de municipios dentro de la API v1 pública.
 * No requiere autenticación JWT (está protegido por API Key a nivel global).
 * <p>
 * Ruta: /api/v1/public/municipios
 */
@RestController
@RequestMapping(API_ENDPOINT_MUNICIPIOS)
public class MunicipioRestController {

	private final MunicipioService municipiosService;

	/**
	 * Inyección de dependencias del servicio de municipios.
	 */
	public MunicipioRestController(MunicipioService municipiosService) {
		this.municipiosService = municipiosService;
	}

	/**
	 * Obtiene el listado completo de municipios disponibles.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/municipios
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link MunicipioDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping
	public ResponseEntity<ApiResponse<List<MunicipioDto>>> doGetMunicipios() {
		List<MunicipioDto> municipios = municipiosService.getMunicipiosDTO();
		return ResponseEntity.ok(ApiResponse.success(municipios));
	}

	/**
	 * Obtiene el listado completo de municipios disponibles filtrados por la provincia.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/municipios/provincia/{id}
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link MunicipioDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping(API_ENDPOINT_MUNICIPIOS_PROVINCIA + "/{id}")
	public ResponseEntity<ApiResponse<List<MunicipioDto>>> doGetMunicipiosByProvincia(
			@PathVariable short id) {
		List<MunicipioDto> municipios = municipiosService.getMunicipiosDTOByProvincia(id);
		return ResponseEntity.ok(ApiResponse.success(municipios));
	}

	/**
	 * Obtiene el listado completo de municipios disponibles que dispongan de estaciones de servicio filtrados por la provincia.
	 *
	 * <p>
	 * Endpoint: GET /api/v1/public/municipios/provincia/{id}/con-estaciones-de-servicio
	 * </p>
	 *
	 * @return {@link ResponseEntity} con lista de {@link MunicipioDto}
	 * y código HTTP 200 OK
	 */
	@GetMapping(
			API_ENDPOINT_MUNICIPIOS_PROVINCIA + "{id}" +
					API_ENDPOINT_MUNICIPIOS_EESS_EXISTENTES
	)
	public ResponseEntity<ApiResponse<List<MunicipioDto>>> doGetMunicipiosByProvinciaConEstacionesDeServicio(
			@PathVariable short id) {
		List<MunicipioDto> municipios = municipiosService.getMunicipiosDTOByProvinciaConEESS(
				id);
		return ResponseEntity.ok(ApiResponse.success(municipios));
	}
}