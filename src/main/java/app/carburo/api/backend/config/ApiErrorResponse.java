package app.carburo.api.backend.config;

/**
 * DTO estándar de respuesta de error de la API.
 */
public record ApiErrorResponse(
		String error,
		String message
) {}