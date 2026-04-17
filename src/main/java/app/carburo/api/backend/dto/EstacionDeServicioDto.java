package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.EstacionDeServicio;

import java.util.List;

/**
 * DTO de {@link EstacionDeServicio}. Diseñado para su uso en peticiones REST.
 */
public record EstacionDeServicioDto(
		int id,
		String rotulo,
		String horario,
		String direccion,
		String localidad,
		int codigoPostal,

		short municipio,
		short provincia,

		double latitud,
		double longitud,

		String margen,
		String remision,
		String venta,

		double x100BioEtanol,
		double x100EsterMetilico,

		boolean abierto,

		List<Short> combustibles
) {

	/**
	 * Convierte una entidad EstacionDeServicio a DTO.
	 *
	 * @param e entidad origen
	 * @return DTO con los datos mapeados
	 */
	public static EstacionDeServicioDto from(EstacionDeServicio e) {
		return new EstacionDeServicioDto(
				e.getId(),
				e.getRotulo(),
				e.getHorario(),
				e.getDireccion(),
				e.getLocalidad(),
				e.getCodigoPostal(),

				e.getMunicipio().getId(),
				e.getProvincia().getId(),

				e.getCoordenada().getLatitud(),
				e.getCoordenada().getLongitud(),

				e.getMargen().name(),
				e.getRemision().name(),
				e.getVenta().name(),

				e.getX100BioEtanol(),
				e.getX100EsterMetilico(),

				e.isAbierto(),

				e.getCombustiblesDisponibles()
						.stream()
						.map(Combustible::getId)
						.toList()
		);
	}
}