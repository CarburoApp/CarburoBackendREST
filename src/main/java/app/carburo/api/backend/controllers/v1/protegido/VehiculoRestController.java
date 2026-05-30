package app.carburo.api.backend.controllers.v1.protegido;

import app.carburo.api.backend.controllers.utilities.ApiResponse;
import app.carburo.api.backend.dto.RepostajeDto;
import app.carburo.api.backend.dto.VehiculoDto;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.services.RepostajeService;
import app.carburo.api.backend.services.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.API_ENDPOINT_VEHICULOS;

/**
 * Controlador REST público de vehículos.
 * <p>
 * Expone endpoints de lectura y modificación de vehiculos dentro de la API v1.
 * Requiere autenticación JWT.
 * <p>
 * Ruta: /api/v1/vehículos
 */
@RestController
@RequestMapping(API_ENDPOINT_VEHICULOS)
public class VehiculoRestController extends BaseProtectedRestController {

	private final VehiculoService vehiculoService;
	private final RepostajeService repostajeService;


	/**
	 * Inyección de dependencias del servicio de vehiculos.
	 */
	public VehiculoRestController(VehiculoService vehiculoService,
								  RepostajeService repostajeService) {
		this.vehiculoService  = vehiculoService;
		this.repostajeService = repostajeService;
	}

	/**
	 * Obtiene todos los vehículos del usuario autenticado.
	 * Comprobaciones realizadas:
	 * <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 * <li>El usuario debe existir.</li>
	 *
	 * No incluye repostajes asociados.
	 * <p>
	 * Endpoint: GET /api/v1/vehiculos/{uuid}
	 *
	 * @param uuid UUID del usuario autenticado
	 * @return listado de vehículos del usuario
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@GetMapping("/{uuid}")
	public ResponseEntity<ApiResponse<List<VehiculoDto>>> doGetVehiculos(
			@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(
				ApiResponse.success(vehiculoService.getVehiculoFromUsuario(uuid)));
	}

	/**
	 * Obtiene todos los repostajes recientes del usuario autenticado.
	 * <p>
	 * Endpoint: GET /api/v1/vehiculos/{uuid}/repostajes
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El usuario debe existir.</li>
	 *
	 * @param uuid UUID del usuario autenticado
	 * @return listado de repostajes del usuario
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@GetMapping("/{uuid}/repostajes")
	public ResponseEntity<ApiResponse<List<RepostajeDto>>> doGetRepostajesUsuario(
			@PathVariable UUID uuid) {
		validateOwnership(uuid);
		return ResponseEntity.ok(ApiResponse.success(
				repostajeService.getRepostajesUsuario(uuid)));
	}

	/**
	 * Obtiene un vehículo concreto junto con todos sus repostajes.
	 * <p>
	 * Endpoint: GET /api/v1/vehiculos/{uuid}/{idVehiculo}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * @param uuid       UUID del usuario autenticado
	 * @param idVehiculo ID del vehículo
	 * @return vehículo completo con repostajes
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@GetMapping("/{uuid}/{idVehiculo}")
	public ResponseEntity<ApiResponse<VehiculoDto>> doGetVehiculo(@PathVariable UUID uuid,
																  @PathVariable
																  Integer idVehiculo) {

		validateOwnership(uuid);
		return ResponseEntity.ok(ApiResponse.success(
				vehiculoService.getVehiculo(uuid, idVehiculo)));
	}

	/**
	 * Crea un nuevo vehículo asociado al usuario autenticado.
	 * <p>
	 * Endpoint: POST /api/v1/vehiculos/{uuid}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El usuario debe existir.</li>
	 *     <li>Los datos del vehículo deben ser válidos.</li>
	 *
	 * @param uuid UUID del usuario autenticado
	 * @param dto  datos del nuevo vehículo
	 * @return confirmación de creación con el uuid del vehículo
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@PostMapping("/{uuid}")
	public ResponseEntity<ApiResponse<Integer>> doPostVehiculo(@PathVariable UUID uuid,
															  @RequestBody
															  VehiculoDto dto) {

		validateOwnership(uuid);
		int id = vehiculoService.createVehiculo(uuid, dto);
		return ResponseEntity.ok(ApiResponse.success(id));
	}

	/**
	 * Actualiza los datos de un vehículo existente.
	 * <p>
	 * Endpoint: PATCH /api/v1/vehiculos/{uuid}/{idVehiculo}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * Se permite modificar únicamente:
	 * matrícula, marca, modelo, odometro_actual, capacidad_deposito y las notas
	 *
	 * @param uuid       UUID del usuario autenticado
	 * @param idVehiculo ID del vehículo
	 * @param dto        datos actualizados
	 * @return confirmación de actualización
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@PatchMapping("/{uuid}/{idVehiculo}")
	public ResponseEntity<ApiResponse<Void>> doPatchVehiculo(@PathVariable UUID uuid,
															 @PathVariable
															 Integer idVehiculo,
															 @RequestBody
															 VehiculoDto dto) {

		validateOwnership(uuid);
		vehiculoService.updateVehiculo(uuid, idVehiculo, dto);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Elimina un vehículo y todos sus repostajes asociados.
	 * <p>
	 * Endpoint: DELETE /api/v1/vehiculos/{uuid}/{idVehiculo}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * @param uuid       UUID del usuario autenticado
	 * @param idVehiculo ID del vehículo
	 * @return confirmación de eliminación
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@DeleteMapping("/{uuid}/{idVehiculo}")
	public ResponseEntity<ApiResponse<Void>> doDeleteVehiculo(@PathVariable UUID uuid,
															  @PathVariable
															  Integer idVehiculo) {
		validateOwnership(uuid);
		vehiculoService.deleteVehiculo(uuid, idVehiculo);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Obtiene todos los repostajes de un vehículo concreto.
	 * <p>
	 * Endpoint: GET /api/v1/vehiculos/{uuid}/{idVehiculo}/repostajes
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * @param uuid       UUID del usuario autenticado
	 * @param idVehiculo ID del vehículo
	 * @return listado de repostajes del vehículo
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@GetMapping("/{uuid}/{idVehiculo}/repostajes")
	public ResponseEntity<ApiResponse<List<RepostajeDto>>> doGetRepostajesVehiculo(
			@PathVariable UUID uuid, @PathVariable Integer idVehiculo) {
		validateOwnership(uuid);
		return ResponseEntity.ok(ApiResponse.success(
				repostajeService.getRepostajesVehiculo(uuid, idVehiculo)));
	}

	/**
	 * Añade un nuevo repostaje a un vehículo.
	 * <p>
	 * Endpoint: POST /api/v1/vehiculos/{uuid}/{idVehiculo}/repostajes
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *     <li>La estación de servicio debe existir.</li>
	 *     <li>El combustible debe existir.</li>
	 *
	 * @param uuid       UUID del usuario autenticado
	 * @param idVehiculo ID del vehículo
	 * @param dto        datos del repostaje
	 * @return confirmación de creación con el id del repostaje
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@PostMapping("/{uuid}/{idVehiculo}/repostajes")
	public ResponseEntity<ApiResponse<Integer>> doPostRepostaje(@PathVariable UUID uuid,
															 @PathVariable
															 Integer idVehiculo,
															 @RequestBody
															 RepostajeDto dto) {

		validateOwnership(uuid);
		int id = repostajeService.createRepostaje(uuid, idVehiculo, dto);
		return ResponseEntity.ok(ApiResponse.success(id));
	}

	/**
	 * Actualiza un repostaje existente.
	 * Se permite actualizar únicamente la cantidad, coste unitario, odómetro inicial, odómetro final, si el depósito se llenó o no, y la nota.
	 * <p>
	 * Endpoint: PATCH /api/v1/vehiculos/{uuid}/{idVehiculo}/repostajes/{idRepostaje}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El repostaje debe existir.</li>
	 *     <li>El repostaje debe pertenecer al vehículo indicado.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * @param uuid        UUID del usuario autenticado
	 * @param idVehiculo  ID del vehículo
	 * @param idRepostaje ID del repostaje
	 * @param dto         datos actualizados
	 * @return confirmación de actualización
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@PatchMapping("/{uuid}/{idVehiculo}/repostajes/{idRepostaje}")
	public ResponseEntity<ApiResponse<Void>> doPatchRepostaje(@PathVariable UUID uuid,
															  @PathVariable
															  Integer idVehiculo,
															  @PathVariable
															  Integer idRepostaje,
															  @RequestBody
															  RepostajeDto dto) {

		validateOwnership(uuid);
		repostajeService.updateRepostaje(uuid, idVehiculo, idRepostaje, dto);
		return ResponseEntity.ok(ApiResponse.success(null));
	}

	/**
	 * Elimina un repostaje existente.
	 * <p>
	 * Endpoint: DELETE /api/v1/vehiculos/{uuid}/{idVehiculo}/repostajes/{idRepostaje}
	 * <p>
	 * Comprobaciones realizadas:
	 *     <li>El UUID debe coincidir con el UUID del token JWT.</li>
	 *     <li>El vehículo debe existir.</li>
	 *     <li>El repostaje debe existir.</li>
	 *     <li>El repostaje debe pertenecer al vehículo indicado.</li>
	 *     <li>El vehículo debe pertenecer al usuario indicado.</li>
	 *
	 * @param uuid        UUID del usuario autenticado
	 * @param idVehiculo  ID del vehículo
	 * @param idRepostaje ID del repostaje
	 * @return confirmación de eliminación
	 * @throws UnauthorizedException si el UUID no coincide con el token JWT
	 */
	@DeleteMapping("/{uuid}/{idVehiculo}/repostajes/{idRepostaje}")
	public ResponseEntity<ApiResponse<Void>> doDeleteRepostaje(@PathVariable UUID uuid,
															   @PathVariable
															   Integer idVehiculo,
															   @PathVariable
															   Integer idRepostaje) {

		validateOwnership(uuid);
		repostajeService.deleteRepostaje(uuid, idVehiculo, idRepostaje);
		return ResponseEntity.ok(ApiResponse.success(null));
	}
}