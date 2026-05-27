package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.config.JwtUser;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.UUID;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_USUARIOS;

@RestController
@RequestMapping(API_ENDPOINT_USUARIOS)
public class BaseProtectedRestController {
	/**
	 * Obtiene el usuario autenticado desde el contexto de seguridad.
	 *
	 * @return usuario autenticado en el contexto JWT
	 */
	protected JwtUser getAuthUser() {
		return (JwtUser) Objects.requireNonNull(
				SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
	}

	/**
	 * Valida que el UUID solicitado pertenece al usuario autenticado.
	 *
	 * @param requestUuid UUID del recurso solicitado
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	protected void validateOwnership(UUID requestUuid) {
		JwtUser authUser = getAuthUser();
		if (requestUuid == null || authUser == null ||
				!requestUuid.equals(authUser.uuid())) {
			throw new UnauthorizedException("UUID mismatch");
		}
	}
}
