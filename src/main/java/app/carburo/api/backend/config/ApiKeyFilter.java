package app.carburo.api.backend.config;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

/**
 * Filtro de validación de API Key para las peticiones de la API.
 * <p>
 * Acepta la clave móvil sin Origin y la clave web solo con un Origin
 * incluido en la lista exacta permitida por CORS.
 * <p>
 * Si la clave no es válida para su cliente, devuelve 401 UNAUTHORIZED.
 */
@Component
public class ApiKeyFilter extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;
	private final CorsOriginPolicy corsOriginPolicy;
	private final List<String> webApiKeys;
	private final List<String> mobileApiKeys;

	/**
	 * Constructor con inyección de dependencias.
	 *
	 * @param objectMapper serializador JSON para respuestas de error
	 */
	public ApiKeyFilter(ObjectMapper objectMapper,
						CorsOriginPolicy corsOriginPolicy,
						@Value("${security.api.keys.web}") String webApiKey,
						@Value("${security.api.keys.mobile}") String mobileApiKey,
						@Value("${security.api.keys.web-previous:}") String previousWebApiKey,
						@Value("${security.api.keys.mobile-previous:}") String previousMobileApiKey) {
		this.objectMapper = objectMapper;
		this.corsOriginPolicy = corsOriginPolicy;

		if (!StringUtils.hasText(webApiKey) || !StringUtils.hasText(mobileApiKey))
			throw new IllegalStateException("Deben configurarse las API keys de web y móvil.");
		this.webApiKeys = keysForClient(webApiKey, previousWebApiKey);
		this.mobileApiKeys = keysForClient(mobileApiKey, previousMobileApiKey);

		for (String webKey : this.webApiKeys)
			for (String mobileKey : this.mobileApiKeys)
				if (constantTimeEquals(webKey, mobileKey))
					throw new IllegalStateException("Las API keys web y móvil deben ser distintas.");
	}

	/**
	 * Intercepta la petición y valída la API key del cliente.
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

		if (path.startsWith(API_BASE_PATH)) {
			// CORS preflight no debe necesitar API key.
			if (HttpMethod.OPTIONS.name().equalsIgnoreCase(request.getMethod())) {
				filterChain.doFilter(request, response);
				return;
			}

			String headerKey = request.getHeader(HEADER_API_KEY);
			boolean validMobileKey = matchesAny(headerKey, mobileApiKeys);
			boolean validWebKey = matchesAny(headerKey, webApiKeys)
					&& corsOriginPolicy.allows(request.getHeader("Origin"));

			if (!validMobileKey && !validWebKey) {
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

	private boolean constantTimeEquals(String candidate, String configuredKey) {
		return candidate != null && MessageDigest.isEqual(
				candidate.getBytes(StandardCharsets.UTF_8),
				configuredKey.getBytes(StandardCharsets.UTF_8));
	}

	private boolean matchesAny(String candidate, List<String> configuredKeys) {
		return configuredKeys.stream()
				.anyMatch(configuredKey -> constantTimeEquals(candidate, configuredKey));
	}

	private List<String> keysForClient(String currentKey, String previousKey) {
		List<String> keys = new ArrayList<>();
		keys.add(currentKey);
		if (StringUtils.hasText(previousKey))
			keys.add(previousKey);
		return List.copyOf(keys);
	}
}
