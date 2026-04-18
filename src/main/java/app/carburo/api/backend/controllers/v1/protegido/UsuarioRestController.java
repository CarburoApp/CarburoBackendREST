package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.config.JwtUser;
import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.UsuarioDto;
import app.carburo.api.backend.services.UsuarioService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_USUARIOS;
import static app.carburo.api.backend.controllers.utilities.HttpConstants.ERR_UNAUTHORIZED;

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


	private final UsuarioService usuarioService;

	/**
	 * Inyección de dependencias del servicio de usuarios.
	 */
	public UsuarioRestController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}


	@GetMapping
	public ResponseEntity<ApiResponse<String>> doGetUsuario() {
		return ResponseEntity.ok(ApiResponse.success("Hola"));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> doPostCreacionUsuario(
			@RequestBody UsuarioDto dto) {
		JwtUser authUser = (JwtUser) Objects.requireNonNull(
				SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
		if (dto == null || dto.uuid() == null || authUser == null ||
				!dto.uuid().equals(authUser.uuid())) {
			return ResponseEntity.status(HttpServletResponse.SC_UNAUTHORIZED)
					.body(ApiResponse.error(ERR_UNAUTHORIZED, "UUID mismatch"));
		}
		usuarioService.createUsuario(dto);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

}