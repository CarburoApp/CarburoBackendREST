package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.PrecioCombustible;

import java.time.LocalDate;

/**
 * DTO de {@link PrecioCombustible}. Diseñado para su uso en peticiones REST.
 */
public record PrecioCombustibleDto(
		int id_estacion_de_servicio,
		short id_combustible,
		LocalDate fecha,
		double precio
) {

	public static PrecioCombustibleDto from(PrecioCombustible c) {
		return new PrecioCombustibleDto(
				c.getEstacion().getId(),
				c.getCombustible().getId(),
				c.getId().getFecha(),
				c.getPrecio()
		);
	}
}