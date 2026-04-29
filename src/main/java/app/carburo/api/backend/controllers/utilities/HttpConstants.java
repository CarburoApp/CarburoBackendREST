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

	public static final String API_PATH_COMBUSTIBLES = "/combustibles";
	public static final String API_PATH_COMUNIDADES_AUTONOMAS = "/comunidades-autonomas";
	public static final String API_PATH_PROVINCIAS = "/provincias";
	public static final String API_PATH_MUNICIPIOS = "/municipios";
	public static final String API_PATH_ESTACIONES_DE_SERVICIO = "/estaciones-de-servicio";
	public static final String API_PATH_USUARIOS = "/usuarios";
	public static final String API_PATH_VEHICULOS = "/vehiculos";

	// PUBLICOS
	public static final String API_ENDPOINT_COMBUSTIBLES =
			API_BASE_PATH_PUBLIC + API_PATH_COMBUSTIBLES;
	public static final String API_ENDPOINT_MUNICIPIOS =
			API_BASE_PATH_PUBLIC + API_PATH_MUNICIPIOS;
	public static final String API_ENDPOINT_MUNICIPIOS_PROVINCIA = "/provincia";
	public static final String API_ENDPOINT_MUNICIPIOS_EESS_EXISTENTES = "/con-estaciones-de-servicio";
	public static final String API_ENDPOINT_PROVINCIAS =
			API_BASE_PATH_PUBLIC + API_PATH_PROVINCIAS;
	public static final String API_ENDPOINT_COMUNIDADES_AUTONOMAS =
			API_BASE_PATH_PUBLIC + API_PATH_COMUNIDADES_AUTONOMAS;
	public static final String API_ENDPOINT_COMUNIDADES_AUTONOMAS_PROVINCIAS_MUNICIPIOS =
			"/provincias/municipios";
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO =
			API_BASE_PATH_PUBLIC + API_PATH_ESTACIONES_DE_SERVICIO;
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO_TOTALES = "/count";
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO_MUNICIPIO = "/municipio";
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO_PROVINCIA = "/provincia";
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO_COMUNIDAD_AUTONOMA = "/comunidad-autonoma";
	public static final String API_ENDPOINT_ESTACIONES_DE_SERVICIO_CERCANAS = "/cercanas";

	// PROTEGIDOS
	public static final String API_ENDPOINT_USUARIOS =
			API_BASE_PATH_VERSION_V1 + API_PATH_USUARIOS;
	public static final String API_ENDPOINT_VEHICULOS =
			API_BASE_PATH_VERSION_V1 + API_PATH_VEHICULOS;

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

	// =========================
	// ERROR CODES
	// =========================

	// --- SEGURIDAD ---
	public static final String ERR_UNAUTHORIZED = "UNAUTHORIZED";
	public static final String ERR_FORBIDDEN = "FORBIDDEN";
	public static final String ERR_INVALID_API_KEY = "Invalid API key";
	public static final String ERR_INVALID_TOKEN = "Invalid token";
	public static final String ERR_EXPIRED_TOKEN = "Expired token";

	// --- VALIDACIÓN ---
	public static final String ERR_BAD_REQUEST = "BAD_REQUEST";
	public static final String ERR_INTERNAL = "INTERNAL_ERROR";
	public static final String ERR_INVALID_PARAMETER = "Invalid parameter";
	public static final String ERR_MISSING_PARAMETER = "Missing parameter";
	public static final String ERR_TYPE_MISMATCH = "Type mismatch";
	public static final String ERR_VALIDATION_FAILED = "Validation failed";
	public static final String ERR_USER_ALREADY_EXITS = "USER ALREADY EXISTS";

	// --- RECURSOS ---
	public static final String ERR_NOT_FOUND = "NOT_FOUND";
	public static final String ERR_RESOURCE_NOT_FOUND = "Resource not found";

	// --- DOMINIO ---
	public static final String ERR_COMUNIDAD_NOT_FOUND = "Comunidad autónoma not found";
	public static final String ERR_PROVINCIA_NOT_FOUND = "Provincia not found";
	public static final String ERR_MUNICIPIO_NOT_FOUND = "Municipio not found";
	public static final String ERR_ESTACION_NOT_FOUND = "Estación de servicio not found";

	// --- NEGOCIO ---
	public static final String ERR_CONFLICT = "Conflict";
	public static final String ERR_DUPLICATE_RESOURCE = "Duplicate resource";
	public static final String ERR_INVALID_STATE = "Invalid state";

	// --- REQUEST ---
	public static final String ERR_UNSUPPORTED_MEDIA_TYPE = "Unsupported media type";
	public static final String ERR_NOT_ACCEPTABLE = "Not acceptable";
	public static final String ERR_METHOD_NOT_ALLOWED = "Method not allowed";
}