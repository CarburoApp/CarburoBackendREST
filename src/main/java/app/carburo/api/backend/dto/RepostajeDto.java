package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Repostaje;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO de {@link Repostaje}. Diseñado para su uso en peticiones REST.
 */
public record RepostajeDto(
		int id,
		int id_vehiculo,
		short id_combustible,
		int id_estacion_de_servicio,
		UUID uuid_usuario_creador,
		OffsetDateTime fecha,
		double cantidad,
		double coste_unitario,
		double odometro_inicial,
		double odometro_final,
		boolean desposito_lleno,
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
				repostaje.getCantidad().doubleValue(),
				repostaje.getCosteUnitario().doubleValue(),
				repostaje.getOdometroInicial().doubleValue(),
				repostaje.getOdometroFinal().doubleValue(),
				repostaje.getDepositoLleno(),
				repostaje.getNota()
		);
	}
}