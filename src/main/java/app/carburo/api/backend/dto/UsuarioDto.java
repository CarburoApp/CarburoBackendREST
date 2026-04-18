package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.Usuario;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO de {@link Usuario}. Diseñado para su uso en peticiones REST.
 */
public record UsuarioDto(
		UUID uuid,
		short id_provincia_favorita, Set<Short> ids_combustibles_favoritos,
		Set<Integer> ids_estaciones_de_servicio_favoritas
) {

	public static UsuarioDto from(Usuario usuario) {
		return new UsuarioDto(
				usuario.getUuid(),
				usuario.getProvinciaFavorita().getId(),
				usuario.getCombustiblesFavoritos().stream().map(Combustible::getId)
						.collect(Collectors.toSet()),
				usuario.getEessFavoritas().stream().map(EstacionDeServicio::getId)
						.collect(Collectors.toSet())
		);
	}
}