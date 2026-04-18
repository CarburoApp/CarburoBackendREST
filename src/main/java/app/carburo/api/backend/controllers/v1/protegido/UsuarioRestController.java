package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.config.JwtUser;
import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.UsuarioDto;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.UUID;

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


	private final UsuarioService usuarioService;

	/**
	 * Inyección de dependencias del servicio de usuarios.
	 */
	public UsuarioRestController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<ApiResponse<UsuarioDto>> getUsuario(@PathVariable UUID uuid) {

		JwtUser auth = getAuthUser();
		validateOwnership(uuid, auth);

		return ResponseEntity.ok(ApiResponse.success(usuarioService.getUsuario(uuid)));
	}

	@PostMapping
	public ResponseEntity<ApiResponse<Void>> doPostCreacionUsuario(
			@RequestBody UsuarioDto dto) {

		JwtUser auth = getAuthUser();
		validateOwnership(dto.uuid(), auth);

		usuarioService.createUsuario(dto);

		return ResponseEntity.ok(ApiResponse.success(null));
	}

	private JwtUser getAuthUser() {
		return (JwtUser) Objects.requireNonNull(
				SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
	}

	private void validateOwnership(UUID requestUuid, JwtUser authUser) {
		if (requestUuid == null || authUser == null ||
				!requestUuid.equals(authUser.uuid())) {

			throw new UnauthorizedException("UUID mismatch");
		}
	}
}