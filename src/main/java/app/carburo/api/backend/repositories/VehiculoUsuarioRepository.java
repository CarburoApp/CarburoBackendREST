package app.carburo.api.backend.repositories;

import app.carburo.api.backend.entities.VehiculoUsuario;
import app.carburo.api.backend.entities.VehiculoUsuarioId;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para la entidad {@link VehiculoUsuario}
 * Proporciona métodos de acceso y comprobación de existencia de usuarios.
 */
public interface VehiculoUsuarioRepository
		extends CrudRepository<VehiculoUsuario, VehiculoUsuarioId> {

	/**
	 * Obtiene todas las relaciones usuario-vehículo de un usuario.
	 *
	 * @param uuid UUID del usuario
	 * @return listado de relaciones
	 */
	List<VehiculoUsuario> findAllByUsuarioUuid(UUID uuid);

	/**
	 * Obtiene la relación concreta entre un usuario y un vehículo.
	 *
	 * @param uuid       UUID del usuario
	 * @param vehiculoId ID del vehículo
	 * @return relación usuario-vehículo
	 */
	Optional<VehiculoUsuario> findByUsuarioUuidAndVehiculoId(UUID uuid,
															 Integer vehiculoId);

	/**
	 * Comprueba si un usuario está vinculado a un vehículo.
	 *
	 * @param uuid       UUID del usuario
	 * @param vehiculoId ID del vehículo
	 * @return true si existe vinculación
	 */
	boolean existsByUsuarioUuidAndVehiculoId(UUID uuid, Integer vehiculoId);

	/**
	 * Comprueba si un usuario es propietario de un vehículo.
	 *
	 * @param uuid       UUID del usuario
	 * @param vehiculoId ID del vehículo
	 * @return true si es propietario
	 */
	boolean existsByUsuarioUuidAndVehiculoIdAndPropietarioTrue(UUID uuid,
															   Integer vehiculoId);
}
