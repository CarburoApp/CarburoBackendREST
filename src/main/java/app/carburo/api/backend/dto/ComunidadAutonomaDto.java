package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.ComunidadAutonoma;

/**
 * DTO de {@link ComunidadAutonoma}. Diseñado para su uso en peticiones REST.
 */
public record ComunidadAutonomaDto(
		short id,
		String denominacion
) {
	public static ComunidadAutonomaDto from(ComunidadAutonoma p) {
		return new ComunidadAutonomaDto(
				p.getId(),
				p.getDenominacion()
		);
	}
}