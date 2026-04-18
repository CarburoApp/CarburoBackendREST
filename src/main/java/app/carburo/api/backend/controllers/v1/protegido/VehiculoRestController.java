package app.carburo.api.backend.controllers.v1.protegido;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_VEHICULOS;

/**
 * Controlador REST público de vehículos.
 * <p>
 * Expone endpoints de lectura y modificación de usuarios dentro de la API v1.
 * Requiere autenticación JWT.
 * <p>
 * Ruta: /api/v1/vehículos
 */
@RestController
@RequestMapping(API_ENDPOINT_VEHICULOS)
public class VehiculoRestController {

	//private final VehiculoService vehiculoService;


	/**
	 * Inyección de dependencias del servicio de usuarios.
	 */
	//public VehiculoRestController(VehiculoService vehiculoService) {
	//	this.vehiculoService = vehiculoService;
	//}
}