package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.Usuario;

import java.util.List;
import java.util.UUID;

/**
 * DTO de {@link Usuario}. Diseñado para su uso en peticiones REST.
 */
public record UsuarioDto(
		UUID uuid,
		short id_provincia_favorita,
		List<Short> ids_combustibles_favoritos,
		List<Integer> ids_estaciones_de_servicio_favoritas
) {

	public static UsuarioDto from(Usuario usuario) {
		return new UsuarioDto(
				usuario.getUuid(),
				usuario.getProvinciaFavorita().getId(),
				List.of(),
				usuario.getEessFavoritas().stream().map(EstacionDeServicio::getId).toList()
		);
	}
}