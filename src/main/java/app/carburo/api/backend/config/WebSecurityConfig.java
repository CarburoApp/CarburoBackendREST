package app.carburo.api.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_BASE_PATH_PUBLIC;
import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_BASE_PATH_VERSION_V1;

/**
 * Configuración de seguridad web de la aplicación.
 *
 * <p>
 * Esta clase define las reglas de seguridad para una API stateless:
 * </p>
 * <ul>
 *     <li>Sin sesiones (STATELESS)</li>
 *     <li>Sin formularios de login</li>
 *     <li>Sin CSRF</li>
 *     <li>Autenticación basada en tokens (JWT)</li>
 * </ul>
 * <p>
 * Define qué endpoints son públicos y cuáles requieren autenticación.
 * </p>
 */
@Configuration
public class WebSecurityConfig {

	/**
	 * Configura la cadena de filtros de seguridad.
	 *
	 * <p>
	 * - Desactiva CSRF (no necesario en APIs REST)
	 * - Configura la aplicación como stateless (sin sesiones)
	 * - Habilita CORS para permitir peticiones desde el frontend
	 * - Define reglas de acceso a endpoints
	 * </p>
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) {
		http

				.csrf(AbstractHttpConfigurer::disable)

				.sessionManagement(session -> session

						.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

				.cors(cors -> {})

				.authorizeHttpRequests(auth -> auth

						.requestMatchers(API_BASE_PATH_PUBLIC + "/**").permitAll()
						.requestMatchers(API_BASE_PATH_VERSION_V1 + "/**").authenticated()

						.anyRequest().denyAll());
		return http.build();
	}

	@Bean
	public AuthenticationManager authenticationManager(
			AuthenticationConfiguration config) {
		return config.getAuthenticationManager();
	}
}
