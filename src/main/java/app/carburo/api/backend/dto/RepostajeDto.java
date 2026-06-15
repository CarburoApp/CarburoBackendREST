package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Repostaje;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * DTO de {@link Repostaje}. Diseñado para su uso en peticiones REST.
 */
public record RepostajeDto(
		int id,
		int id_vehiculo,
		short id_combustible,
		int id_estacion_de_servicio,
		UUID uuid_usuario_creador,
		OffsetDateTime fecha_repostaje,
		OffsetDateTime fecha_registro,
		double cantidad,
		double coste_unitario,
		Double odometro_inicial,
		double odometro_final,
		boolean deposito_lleno,
		String nota
		) {

	public static RepostajeDto from(Repostaje repostaje) {
		return new RepostajeDto(
				repostaje.getId(),
				repostaje.getVehiculo().getId(),
				repostaje.getCombustible().getId(),
				repostaje.getEstacionDeServicio().getId(),
				repostaje.getUsuario().getUuid(),
				repostaje.getFechaRepostaje(),
				repostaje.getFechaRegistro(),
				repostaje.getCantidad().doubleValue(),
				repostaje.getCosteUnitario().doubleValue(),
				(repostaje.getOdometroInicial() != null) ? repostaje.getOdometroInicial()
						.doubleValue() : null,
				repostaje.getOdometroFinal().doubleValue(),
				repostaje.getDepositoLleno(),
				repostaje.getNota()
		);
	}
}
