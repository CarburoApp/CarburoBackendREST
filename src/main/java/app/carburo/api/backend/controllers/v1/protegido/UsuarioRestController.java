package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.config.JwtUser;
import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.dto.UsuarioDto;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.services.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_USUARIOS;

/**
 * Controlador REST protegido responsable de la gestión de usuarios dentro de la API v1.
 *
 * <p>Este controlador expone operaciones de lectura y modificación de los datos de usuario,
 * incluyendo provincia favorita, combustibles favoritos y estaciones de servicio favoritas.</p>
 *
 * <p>Todas las operaciones están protegidas mediante autenticación JWT y verificación de
 * propiedad del recurso (el UUID del path debe coincidir con el UUID del token autenticado).</p>
 *
 * <p>Base path: {@code /api/v1/usuarios}</p>
 */
@RestController
@RequestMapping(API_ENDPOINT_USUARIOS)
public class UsuarioRestController {

	private final UsuarioService usuarioService;

	/**
	 * Constructor con inyección de dependencias del servicio de usuarios.
	 *
	 * @param usuarioService servicio de dominio encargado de la lógica de negocio de usuarios
	 */
	public UsuarioRestController(UsuarioService usuarioService) {
		this.usuarioService = usuarioService;
	}

	/**
	 * Obtiene el usuario completo identificado por su UUID.
	 *
	 * @param uuid identificador único del usuario
	 * @return usuario serializado en formato DTO
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@GetMapping("/{uuid}")
	public ResponseEntity<ApiResponse<UsuarioDto>> doGetUsuario(@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(ApiResponse.success(usuarioService.getUsuario(uuid)));
	}

	/**
	 * Obtiene la provincia favorita del usuario.
	 *
	 * @param uuid identificador único del usuario
	 * @return identificador de la provincia favorita
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@GetMapping("/{uuid}/provincia-favorita")
	public ResponseEntity<ApiResponse<Short>> doGetProvinciaFavorita(
			@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(
				ApiResponse.success(usuarioService.getProvinciaFavorita(uuid)));
	}

	/**
	 * Obtiene la lista de combustibles favoritos del usuario.
	 *
	 * @param uuid identificador único del usuario
	 * @return conjunto de identificadores de combustibles favoritos
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@GetMapping("/{uuid}/combustibles-favoritos")
	public ResponseEntity<ApiResponse<Set<Short>>> doGetCombustiblesFavoritos(
			@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(
				ApiResponse.success(usuarioService.getCombustiblesFavoritos(uuid)));
	}

	/**
	 * Obtiene la lista de estaciones de servicio favoritas del usuario.
	 *
	 * @param uuid identificador único del usuario
	 * @return Listado de {@link EstacionDeServicioDto} favoritas del usuario.
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@GetMapping("/{uuid}/estaciones-de-servicio-favoritas")
	public ResponseEntity<ApiResponse<List<EstacionDeServicioDto>>> doGetEstacionesFavoritas(
			@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(
				ApiResponse.success(usuarioService.getEstacionesDeServicioFavoritasDto(uuid)));
	}

	/**
	 * Crea un nuevo usuario en el sistema.
	 *
	 * <p>El UUID del usuario debe coincidir con el usuario autenticado mediante JWT.</p>
	 *
	 * @param dto datos del usuario a crear
	 * @return respuesta vacía con confirmación de creación
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@PostMapping
	public ResponseEntity<ApiResponse<Void>> doPostCreacionUsuario(@RequestBody UsuarioDto dto) {
		validateOwnership(dto.uuid());
		usuarioService.createUsuario(dto);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Actualiza la provincia favorita del usuario.
	 *
	 * @param uuid        identificador del usuario
	 * @param provinciaId identificador de la nueva provincia favorita
	 * @return respuesta vacía con confirmación de actualización
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@PatchMapping("/{uuid}/provincia-favorita/{provinciaId}")
	public ResponseEntity<ApiResponse<Void>> doPatchUpdateProvinciaFavorita(
			@PathVariable UUID uuid, @PathVariable short provinciaId) {

		validateOwnership(uuid);
		usuarioService.updateProvincia(uuid, provinciaId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Reemplaza la lista de combustibles favoritos del usuario.
	 *
	 * @param uuid                       identificador del usuario
	 * @param ids_combustibles_favoritos conjunto de IDs de combustibles
	 * @return respuesta vacía con confirmación de actualización
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@PatchMapping("/{uuid}/combustibles-favoritos")
	public ResponseEntity<ApiResponse<Void>> doPatchUpdateCombustiblesFavoritos(
			@PathVariable UUID uuid, @RequestBody Set<Short> ids_combustibles_favoritos) {

		validateOwnership(uuid);
		usuarioService.updateCombustiblesFavoritos(uuid, ids_combustibles_favoritos);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Añade una estación de servicio a la lista de favoritas del usuario.
	 *
	 * @param uuid       identificador del usuario
	 * @param estacionId identificador de la estación de servicio
	 * @return respuesta vacía con confirmación de actualización
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@PatchMapping("/{uuid}/estaciones-de-servicio-favoritas/{estacionId}")
	public ResponseEntity<ApiResponse<Void>> doPatchAddEstacionFavorita(
			@PathVariable UUID uuid, @PathVariable int estacionId) {

		validateOwnership(uuid);
		usuarioService.addEstacionDeServicioFavorita(uuid, estacionId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Elimina una estación de servicio de la lista de favoritas del usuario.
	 *
	 * @param uuid       identificador del usuario
	 * @param estacionId identificador de la estación de servicio
	 * @return respuesta vacía con confirmación de eliminación
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	@DeleteMapping("/{uuid}/estaciones-de-servicio-favoritas/{estacionId}")
	public ResponseEntity<ApiResponse<Void>> doDeleteEstacionFavorita(
			@PathVariable UUID uuid, @PathVariable int estacionId) {

		validateOwnership(uuid);
		usuarioService.removeEstacionDeServicioFavorita(uuid, estacionId);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Obtiene el usuario autenticado desde el contexto de seguridad.
	 *
	 * @return usuario autenticado en el contexto JWT
	 */
	private JwtUser getAuthUser() {
		return (JwtUser) Objects.requireNonNull(
				SecurityContextHolder.getContext().getAuthentication()).getPrincipal();
	}

	/**
	 * Valida que el UUID solicitado pertenece al usuario autenticado.
	 *
	 * @param requestUuid UUID del recurso solicitado
	 * @throws UnauthorizedException si el UUID no coincide con el usuario autenticado
	 */
	private void validateOwnership(UUID requestUuid) {
		JwtUser authUser = getAuthUser();
		if (requestUuid == null || authUser == null || !requestUuid.equals(authUser.uuid())) {
			throw new UnauthorizedException("UUID mismatch");
		}
	}
}