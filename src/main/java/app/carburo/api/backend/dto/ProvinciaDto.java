package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Provincia;

import java.util.List;

/**
 * DTO de {@link Provincia}. Diseñado para su uso en peticiones REST.
 */
public record ProvinciaDto(
		short id,
		String denominacion,
		short id_comunidadAutonoma,
		List<MunicipioDto> municipios
) {
	public static ProvinciaDto from(Provincia p) {
		return new ProvinciaDto(
				p.getId(),
				p.getDenominacion(),
				p.getComunidadAutonoma().getId(),
				null
		);
	}

	public static ProvinciaDto fromWithMunicipios(Provincia p) {
		return new ProvinciaDto(
				p.getId(),
				p.getDenominacion(),
				p.getComunidadAutonoma().getId(),
				p.getMunicipios().stream().map(MunicipioDto::from).toList()
		);
	}
}