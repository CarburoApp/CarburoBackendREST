package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Provincia;

/**
 * DTO de {@link Provincia}. Diseñado para su uso en peticiones REST.
 */
public record ProvinciaDto(
		short id,
		String denominacion,
		short id_comunidadAutonoma
) {
	public static ProvinciaDto from(Provincia p) {
		return new ProvinciaDto(
				p.getId(),
				p.getDenominacion(),
				p.getComunidadAutonoma().getId()
		);
	}
}