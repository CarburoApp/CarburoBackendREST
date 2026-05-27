package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.Usuario;
import app.carburo.api.backend.entities.Vehiculo;
import app.carburo.api.backend.services.VehiculoService;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO de {@link Vehiculo}. Diseñado para su uso en peticiones REST.
 */
public record VehiculoDto(
		int id,
		String denominacion,
		UUID uuid_usuario_requerido,
		boolean es_usuario_propietario,
		double odometro_inicial,
		double odometro_actual,
		double capacidad_deposito,
		Set<Short> ids_combustibles_utilizados
) {

	public static VehiculoDto from(Vehiculo vehiculo, UUID uuidUsuarioActual, boolean isPropietario) {
		return new VehiculoDto(
				vehiculo.getId(),
				vehiculo.getDenominacion(),
				uuidUsuarioActual,
				isPropietario,
				vehiculo.getOdometroInicial().doubleValue(),
				vehiculo.getOdometroActual().doubleValue(),
				vehiculo.getCapacidadDeposito().doubleValue(),
				vehiculo.getCombustibles()
						.stream().map(Combustible::getId)
						.collect(Collectors.toSet())
		);
	}
}