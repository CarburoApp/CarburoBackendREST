package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.Vehiculo;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

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
		Set<Short> ids_combustibles_utilizados
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
				vehiculo.getCombustibles()
						.stream().map(Combustible::getId)
						.collect(Collectors.toSet())
		);
	}
}