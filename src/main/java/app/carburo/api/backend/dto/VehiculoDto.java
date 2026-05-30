package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Vehiculo;

import java.util.UUID;

/**
 * DTO de {@link Vehiculo}. Diseñado para su uso en peticiones REST.
 */
public record VehiculoDto(
		int id,
		UUID uuid_usuario_solicitante,
		boolean is_usuario_solicitante_propietario,
		String matricula,
		String marca,
		String modelo,
		double odometro_actual,
		double capacidad_deposito,
		String notas,
		short id_grupo_combustible
) {

	public static VehiculoDto from(Vehiculo vehiculo, UUID uuidUsuarioActual, boolean isPropietario) {
		return new VehiculoDto(
				vehiculo.getId(),
				uuidUsuarioActual,
				isPropietario,
				vehiculo.getMatricula(),
				vehiculo.getMarca(),
				vehiculo.getModelo(),
				vehiculo.getOdometroActual().doubleValue(),
				vehiculo.getCapacidadDeposito().doubleValue(),
				vehiculo.getNotas(),
				vehiculo.getGrupoCombustible().getId()
		);
	}
}