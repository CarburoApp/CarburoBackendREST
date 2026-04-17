package app.carburo.api.backend.controllers.utilities;

/**
 * Constantes HTTP utilizadas en la API.
 *
 * <p>
 * Centraliza headers, content-types, formatos de autorización
 * y rutas base para evitar duplicidad de strings.
 * </p>
 */
public final class HttpConstants {
	private HttpConstants() {
		// Evita instanciación
	}

	// =========================
	// DOMAIN
	// =========================
	public static final String DOMAIN_NAME = "Carburo";
	public static final String BASE_WEB_URI = "https://carburo.app";
	public static final String BASE_API_URI = "https://api.carburo.app";

	// =========================
	// API PATHS
	// =========================
	public static final String API_BASE_PATH = "/api";
	public static final String API_BASE_PATH_VERSION_V1 = "/v1";
	public static final String API_BASE_PATH_PUBLIC =
			API_BASE_PATH_VERSION_V1 + "/public";
	public static final String API_BASE_PATH_COMBUSTIBLES =
			API_BASE_PATH_PUBLIC + "/combustibles";
	public static final String API_PATH_COMUNIDADES_AUTONOMAS = "/comunidades-autonomas";
	public static final String API_PATH_PROVINCIAS = "/provincias";
	public static final String API_PATH_MUNICIPIOS = "/municipios";
	public static final String API_PATH_ESTACIONES_DE_SERVICIO = "/estaciones-de-servicio";

	// =========================
	// HEADERS
	// =========================
	public static final String HEADER_AUTHORIZATION = "Authorization";
	public static final String HEADER_CONTENT_TYPE = "Content-Type";
	public static final String HEADER_ACCEPT = "Accept";
	public static final String HEADER_API_KEY = "X-API-KEY";

	// =========================
	// AUTH
	// =========================
	public static final String AUTH_BEARER_PREFIX = "Bearer ";
	public static final String AUTH_BEARER_FORMAT = "Bearer %s";

	// =========================
	// CONTENT TYPES
	// =========================
	public static final String CONTENT_TYPE_JSON = "application/json";
	public static final String CONTENT_TYPE_FORM_URLENCODED = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_MULTIPART = "multipart/form-data";
	public static final String CONTENT_CHARACTER_ENCODING = "UTF-8";

	// =========================
	// COMMON RESPONSE HEADERS
	// =========================
	public static final String HEADER_CACHE_CONTROL = "Cache-Control";
	public static final String HEADER_EXPIRES = "Expires";

	// =========================
	// METHODS (por si los necesitas en filtros/logs)
	// =========================
	public static final String METHOD_GET = "GET";
	public static final String METHOD_POST = "POST";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_DELETE = "DELETE";
	public static final String METHOD_PATCH = "PATCH";
	public static final String METHOD_OPTIONS = "OPTIONS";
}