package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;

/**
 * DTO de {@link Combustible}. Diseñado para su uso en peticiones REST.
 */
public record CombustibleDto(
		short id,
		String denominacion,
		String codigo,
		Short id_grupo_combustible
) {

	public static CombustibleDto from(Combustible c) {
		return new CombustibleDto(
				c.getId(),
				c.getDenominacion(),
				c.getCodigo(),
				c.getIdGrupoCombustible()
		);
	}
}