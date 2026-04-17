package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Municipio;

/**
 * DTO de {@link Municipio}. Diseñado para su uso en peticiones REST.
 */
public record MunicipioDto(
		short id,
		String denominacion,
		short id_provincia
) {
	public static MunicipioDto from(Municipio p) {
		return new MunicipioDto(
				p.getId(),
				p.getDenominacion(),
				p.getProvincia().getId()
		);
	}
}