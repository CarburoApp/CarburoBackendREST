package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.ComunidadAutonoma;

import java.util.List;

/**
 * DTO de {@link ComunidadAutonoma}. Diseñado para su uso en peticiones REST.
 */
public record ComunidadAutonomaDto(
		short id,
		String denominacion,
		List<ProvinciaDto> provincias
) {
	public static ComunidadAutonomaDto from(ComunidadAutonoma p) {
		return new ComunidadAutonomaDto(
				p.getId(),
				p.getDenominacion(),
				null
		);
	}

	public static ComunidadAutonomaDto fromWithProvincias(ComunidadAutonoma p) {
		return new ComunidadAutonomaDto(
				p.getId(),
				p.getDenominacion(),
				p.getProvincias().stream().map(ProvinciaDto::fromWithMunicipios).toList()
		);
	}
}