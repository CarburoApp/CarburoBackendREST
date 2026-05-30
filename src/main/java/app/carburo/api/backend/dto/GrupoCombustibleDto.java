package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.GrupoCombustible;

import java.util.List;

/**
 * DTO de {@link GrupoCombustible}. Diseñado para su uso en peticiones REST.
 */
public record GrupoCombustibleDto(
		short id,
		String codigo,
		List<CombustibleDto> combustibles
) {

	public static GrupoCombustibleDto from(GrupoCombustible c) {
		return new GrupoCombustibleDto(
				c.getId(),
				c.getCodigo(),
				null
		);
	}

	public static GrupoCombustibleDto from(GrupoCombustible c, List<CombustibleDto> combustibles) {
		return new GrupoCombustibleDto(
				c.getId(),
				c.getCodigo(),
				combustibles
		);
	}

	public GrupoCombustibleDto withCombustibles(List<CombustibleDto> nuevosCombustibles) {
		return new GrupoCombustibleDto(this.id, this.codigo, nuevosCombustibles);
	}
}