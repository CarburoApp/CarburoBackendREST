package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.EstacionDeServicio;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * DTO de {@link EstacionDeServicio}. Diseñado para su uso en peticiones REST.
 */
public record EstacionDeServicioDto(
		int id,
		String rotulo,
		String horario, String direccion, String localidad, int codigo_postal,

		short id_municipio, short id_provincia,

		double latitud,
		double longitud,

		String margen,
		String remision,
		String venta,

		double x100BioEtanol,
		double x100EsterMetilico,

		boolean abierto,

		Long distancia_metros,

		Set<Short> id_combustibles_disponibles,

		List<PrecioCombustibleDto> precios_de_combustibles
) {
	/**
	 * Convierte una entidad EstacionDeServicio a DTO.
	 *
	 * @param e entidad origen
	 * @return DTO con los datos mapeados
	 */
	public static EstacionDeServicioDto from(EstacionDeServicio e) {
		return EstacionDeServicioDto.from(e, null);
	}

	/**
	 * Convierte una entidad EstacionDeServicio a DTO indicando la distancia en metros también.
	 *
	 * @param e                entidad origen
	 * @param distancia_metros distancia en metros
	 * @return DTO con los datos mapeados
	 */
	public static EstacionDeServicioDto from(EstacionDeServicio e,
											 Long distancia_metros) {
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

				distancia_metros,
				e.getCombustiblesDisponibles().stream().map(Combustible::getId).collect(
						Collectors.toSet()),
				e.getPreciosCombustibles().stream().map(PrecioCombustibleDto::from)
						.toList()
		);
	}

}