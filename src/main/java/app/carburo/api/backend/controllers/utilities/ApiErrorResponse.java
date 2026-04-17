package app.carburo.api.backend.controllers.utilities;

/**
 * DTO estándar de respuesta de error de la API.
 */
public record ApiErrorResponse(
		String error,
		String message
) {}