package app.carburo.api.backend.config;

import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.net.URL;
import java.security.interfaces.ECPublicKey;
import java.util.concurrent.TimeUnit;

/**
 * Utilidad para validación de JWT emitidos por Supabase.
 *
 * <p>
 * Se encarga de:
 * </p>
 * <ul>
 *     <li>Obtener la clave pública desde el endpoint JWKS</li>
 *     <li>Validar la firma del token (ES256)</li>
 *     <li>Verificar issuer y audience</li>
 *     <li>Extraer el identificador único del usuario (sub)</li>
 * </ul>
 *
 * <p>
 * Implementa cache y rate limiting sobre el proveedor JWKS
 * para evitar sobrecarga de red y mejorar rendimiento.
 * </p>
 *
 * <p>
 * Si el token no es válido (firma incorrecta, expirado, issuer incorrecto, etc.),
 * devuelve null.
 * </p>
 */
public class JwtValidator {

	/**
	 * Proveedor de claves públicas JWKS (cacheado).
	 */
	private static JwkProvider provider;

	/**
	 * Valida un JWT contra el JWKS de Supabase.
	 *
	 * @param token   JWT recibido en la cabecera Authorization
	 * @param jwksUrl URL del endpoint JWKS de Supabase
	 * @param issuer  Issuer esperado del token
	 * @return JwtUser con el UUID del usuario si es válido, null en caso contrario
	 */
	public static JwtUser validateWithJwks(String token, String jwksUrl, String issuer) {
		if (token == null || jwksUrl == null || issuer == null || token.isEmpty() ||
				jwksUrl.isEmpty() || issuer.isEmpty()) {
			throw new IllegalArgumentException("Token, JWKS URL and ISSUER are required");
		}
		try {
			// Inicializa el proveedor JWKS con cache y rate limiting
			if (provider == null) {
				provider = new JwkProviderBuilder(new URL(jwksUrl))
						.cached(10, 24, TimeUnit.HOURS)
						.rateLimited(10, 1, TimeUnit.MINUTES)
						.build();
			}

			// Decodifica el token sin verificar (para obtener el kid)
			DecodedJWT decodedJWT = JWT.decode(token);

			// Obtiene la clave pública correspondiente al token
			Jwk jwk = provider.get(decodedJWT.getKeyId());

			ECPublicKey publicKey = (ECPublicKey) jwk.getPublicKey();

			// Configura el algoritmo ES256 (Elliptic Curve)
			Algorithm algorithm = Algorithm.ECDSA256(publicKey, null);

			// Construye el verificador con validaciones obligatorias
			JWTVerifier verifier = JWT.require(algorithm)
					.withIssuer(issuer)
					.withAudience("authenticated")
					.acceptLeeway(60) // tolerancia de tiempo (clock skew)
					.build();

			// Verifica firma, expiración, issuer, audience, etc.
			DecodedJWT jwt = verifier.verify(token);

			// Extrae el UUID del usuario (claim "sub")
			String userId = jwt.getSubject();

			return new JwtUser(userId);

		} catch (Exception e) {
			// Error en validación → token inválido
			e.printStackTrace();
			return null;
		}
	}
}