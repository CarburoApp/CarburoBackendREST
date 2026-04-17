package app.carburo.api.backend.controllers;

import app.carburo.api.backend.services.AuthService;
import app.carburo.api.backend.services.ProvinciaService;
import app.carburo.api.backend.services.UsuarioService;
import org.springframework.stereotype.Controller;

/**
 * Controlador de autenticación y gestión de acceso de usuarios.
 *
 * <p>
 * Centraliza los flujos de login, registro, recuperación de contraseña
 * y acceso al perfil del usuario autenticado.
 * </p>
 */
@Controller
public class AuthController {

	private final ProvinciaService provinciaService;
	private final AuthService authService;
	private final UsuarioService usuarioService;

	/**
	 * Constructor con inyección de dependencias de servicios y validadores.
	 */
	public AuthController(AuthService authService, UsuarioService usuarioService,
						  ProvinciaService provinciaService) {
		this.authService      = authService;
		this.usuarioService   = usuarioService;
		this.provinciaService = provinciaService;
	}

}
