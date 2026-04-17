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

	/**
	 * Define la configuración CORS global de la aplicación.
	 *
	 * <p>
	 * Permite:
	 * - Peticiones desde el frontend (carburo.app)
	 * - Envío de headers como Authorization (JWT)
	 * - Métodos HTTP habituales
	 * </p>
	 */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {

		CorsConfiguration config = new CorsConfiguration();

		// Necesario para enviar JWT en Authorization
		config.setAllowCredentials(true);
		// Dominio del frontend en producción
		List<String> allowOrigins = List.of(HttpConstants.BASE_WEB_URI
											//, app.carburo.mobile -> Cors no afecta a apps móviles, solo a navegadores web
										   );
		config.setAllowedOrigins(allowOrigins);

		// Métodos permitidos
		List<HttpMethod> allowMethods = List.of(HttpMethod.GET, HttpMethod.POST,
												HttpMethod.PATCH, HttpMethod.DELETE);
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