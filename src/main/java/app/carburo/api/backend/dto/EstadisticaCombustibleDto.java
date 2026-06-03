package app.carburo.api.backend.dto;


public record EstadisticaCombustibleDto(
		CombustibleDto combustible,
		double precioMedio,
		double precioMaximo,
		double precioMinimo,
		long totalEstaciones
) {}