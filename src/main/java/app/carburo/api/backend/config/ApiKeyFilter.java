package app.carburo.api.backend.config;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Filtro de validación de API Key para endpoints públicos.
 * <p>
 * Aplica seguridad ligera a rutas /api/v1/public/** comprobando
 * la cabecera X-API-KEY contra la clave configurada en la aplicación.
 * <p>
 * Si la clave es inválida, se devuelve 401 UNAUTHORIZED con un JSON de error.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

	@Value("${security.api.key}")
	private String apiKey;
	private final ObjectMapper objectMapper;

	/**
	 * Constructor con inyección de dependencias.
	 *
	 * @param objectMapper serializador JSON para respuestas de error
	 */
	public ApiKeyFilter(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	/**
	 * Intercepta la petición y valida la API key en endpoints públicos.
	 *
	 * @param request     petición HTTP entrante
	 * @param response    respuesta HTTP a devolver
	 * @param filterChain cadena de filtros de Spring Security
	 */
	@Override
	protected void doFilterInternal(HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain)
			throws ServletException, IOException {

		String path = request.getRequestURI();

		if (path.startsWith(API_BASE_PATH + API_BASE_PATH_PUBLIC)) {
			String headerKey = request.getHeader(HEADER_API_KEY);
			if (headerKey == null || !headerKey.equals(apiKey)) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.setContentType(CONTENT_TYPE_JSON);
				response.setCharacterEncoding(CONTENT_CHARACTER_ENCODING);
				ApiResponse<?> error = ApiResponse.error(ERR_UNAUTHORIZED,
														 ERR_INVALID_API_KEY);
				objectMapper.writeValue(response.getOutputStream(), error);
				return;
			}
		}

		filterChain.doFilter(request, response);
	}
}