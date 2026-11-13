package app.carburo.api.backend.config;

import app.carburo.api.backend.controllers.utilities.HttpConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración CORS para la API.
 *
 * <p>
 * Define qué orígenes pueden consumir la API REST.
 * Necesario porque el frontend (carburo.app) está en otro dominio.
 * </p>
 */
@Configuration
public class CorsConfig {

	private final CorsOriginPolicy corsOriginPolicy;

	public CorsConfig(CorsOriginPolicy corsOriginPolicy) {
		this.corsOriginPolicy = corsOriginPolicy;
	}

	/**
	 * Define la configuración CORS global de la aplicación.
	 * Permite:
	 * - Peticiones desde el frontend (carburo.app)
	 * - Envío de headers como Authorization (JWT)
	 * - Métodos HTTP habituales
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();

		// JWT se envía explícitamente en Authorization; no hacen falta cookies.
		config.setAllowCredentials(false);
		// Dominio del frontend en producción
		// Orígenes exactos compartidos con la política de la API key web.
		config.setAllowedOrigins(corsOriginPolicy.getAllowedOrigins());

		// Métodos permitidos
		List<HttpMethod> allowMethods = List.of(HttpMethod.GET, HttpMethod.POST,
												HttpMethod.PATCH, HttpMethod.DELETE,
												HttpMethod.HEAD, HttpMethod.OPTIONS);
		config.setAllowedMethods(allowMethods.stream().map(HttpMethod::name).toList());

		// Headers permitidos (Authorization, Content-Type, etc.)
		List<String> allowHeaders = List.of(
				HttpConstants.HEADER_AUTHORIZATION,
				HttpConstants.HEADER_CONTENT_TYPE,
				HttpConstants.HEADER_API_KEY,
				HttpConstants.HEADER_ACCEPT
										   );
		config.setAllowedHeaders(allowHeaders);

		// Registrar configuración para todas las rutas
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return source;
	}
}