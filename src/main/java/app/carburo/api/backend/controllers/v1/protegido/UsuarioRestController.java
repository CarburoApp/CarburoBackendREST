package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.services.UsuarioService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_USUARIOS;

/**
 * Controlador REST público de usuarios.
 * <p>
 * Expone endpoints de lectura y modificación de usuarios dentro de la API v1.
 * Requiere autenticación JWT.
 * <p>
 * Ruta: /api/v1/usuarios
 */
@RestController
@RequestMapping(API_ENDPOINT_USUARIOS)
public class UsuarioRestController {

	//private final UsuarioService usuarioService;

	/**
	 * Inyección de dependencias del servicio de usuarios.
	 */
//	public UsuarioRestController(UsuarioService usuarioService) {
//		this.usuarioService = usuarioService;
//	}
}