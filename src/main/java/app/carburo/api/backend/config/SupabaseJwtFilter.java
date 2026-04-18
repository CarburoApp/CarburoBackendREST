package app.carburo.api.backend.config;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.controllers.utilities.HttpConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Filtro de autenticación basado en JWT de Supabase.
 * <p>
 * Se aplica únicamente a endpoints protegidos:
 * </p>
 * <ul>
 *     <li>/api/v1/**</li>
 *     <li>EXCLUYENDO /api/v1/public/**</li>
 * </ul>
 * <p>
 * Funcionalidad:
 * </p>
 * <ul>
 *     <li>Extrae el JWT del header Authorization</li>
 *     <li>Valida el token contra JWKS de Supabase</li>
 *     <li>Construye el usuario autenticado (JwtUser)</li>
 *     <li>Lo almacena en el SecurityContext</li>
 * </ul>
 * <p>
 * Si el token es inválido o no está presente,devuelve 401 UNAUTHORIZED con respuesta JSON.
 * </p>
 */
@Component
public class SupabaseJwtFilter extends OncePerRequestFilter {

	@Value("${security.jwt.issuer}")
	private String issuer;

	@Value("${security.jwt.jwks-url}")
	private String jwksUrl;

	private final ObjectMapper objectMapper;

	public SupabaseJwtFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Determina si el filtro debe ejecutarse para la petición actual.
	 *
	 * <p>
	 * Se excluyen:
	 * </p>
	 * <ul>
	 *     <li>Rutas fuera de /api/v1</li>
	 *     <li>Rutas públicas (/api/v1/public/**)</li>
	 * </ul>
	 */
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();

		// NO filtrar public ni rutas no API
		return !path.startsWith(
				HttpConstants.API_BASE_PATH + HttpConstants.API_BASE_PATH_VERSION_V1) ||
				path.startsWith(
						HttpConstants.API_BASE_PATH + HttpConstants.API_BASE_PATH_PUBLIC);
	}

	/**
	 * Procesa la autenticación JWT de la petición.
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader(HttpConstants.HEADER_AUTHORIZATION);

		// Validación básica del header Authorization
		if (authHeader == null ||
				!authHeader.startsWith(HttpConstants.AUTH_BEARER_PREFIX)) {
			writeError(response, ERR_UNAUTHORIZED, ERR_INVALID_TOKEN);
			return;
		}

		String token = authHeader.substring(HttpConstants.AUTH_BEARER_PREFIX.length());
		JwtUser user = JwtValidator.validateWithJwks(token, jwksUrl, issuer);

		if (user == null) {
			writeError(response, ERR_UNAUTHORIZED, ERR_INVALID_TOKEN);
			return;
		}

		// Construye el contexto de autenticación
		UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
				user, null, List.of());
		auth.setDetails(user);
		SecurityContextHolder.getContext().setAuthentication(auth);

		filterChain.doFilter(request, response);
	}

	/**
	 * Escribe una respuesta de error estándar en formato JSON.
	 *
	 * @param response objeto HttpServletResponse
	 * @param code     código interno de error
	 * @param message  mensaje descriptivo
	 */
	private void writeError(HttpServletResponse response, String code, String message)
			throws IOException {

		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(CONTENT_TYPE_JSON);
		response.setCharacterEncoding(CONTENT_CHARACTER_ENCODING);

		ApiResponse<?> error = ApiResponse.error(code, message);
		objectMapper.writeValue(response.getOutputStream(), error);
	}
}