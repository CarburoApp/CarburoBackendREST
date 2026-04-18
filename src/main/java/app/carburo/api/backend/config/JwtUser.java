package app.carburo.api.backend.config;

import java.util.UUID;

/**
 * Representación mínima del usuario autenticado mediante JWT.
 *
 * <p>
 * Esta clase encapsula la información extraída del token validado,
 * y se utiliza como principal dentro del contexto de seguridad
 * de Spring Security.
 * </p>
 *
 * <p>
 * Actualmente contiene únicamente:
 * </p>
 * <ul>
 *     <li>UUID del usuario (claim "sub")</li>
 * </ul>
 *
 * <p>
 * Este objeto es inyectado en el SecurityContext y permite
 * implementar un control de acceso tipo "RLS casero" en la API,
 * garantizando que cada usuario solo acceda a sus propios datos.
 * </p>
 */
public record JwtUser(
		UUID uuid
) {}