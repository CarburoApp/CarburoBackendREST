package app.carburo.api.backend.controllers.utilities;

import app.carburo.api.backend.exceptions.InvalidUsuarioDataException;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.exceptions.UnauthorizedException;
import app.carburo.api.backend.exceptions.UsuarioAlreadyExistsException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(
			ResourceNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(ERR_NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(
			MethodArgumentTypeMismatchException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST,
										"Parámetro inválido. " + ex.getMessage()));
	}

	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(NotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(ERR_NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST, ex.getMessage()));
	}

	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ApiResponse<Void>> handleUnauthorized(
			UnauthorizedException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(ERR_UNAUTHORIZED, ex.getMessage()));
	}

	@ExceptionHandler(TokenExpiredException.class)
	public ResponseEntity<ApiResponse<Void>> handleTokenExpired(
			TokenExpiredException ex) {
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
				.body(ApiResponse.error(ERR_UNAUTHORIZED, ex.getMessage()));
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadJson(HttpMessageNotReadableException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST, "JSON mal formado"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidation(
			MethodArgumentNotValidException ex) {

		String msg = ex.getBindingResult().getFieldErrors().stream()
				.map(e -> e.getField() + ": " + e.getDefaultMessage()).findFirst()
				.orElse("Validación incorrecta");

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST, msg));
	}

	@ExceptionHandler(UsuarioAlreadyExistsException.class)
	public ResponseEntity<ApiResponse<Void>> handleUsuarioExists(
			UsuarioAlreadyExistsException ex) {

		return ResponseEntity.status(HttpStatus.CONFLICT)
				.body(ApiResponse.error(ERR_USER_ALREADY_EXITS, ex.getMessage()));
	}

	@ExceptionHandler(InvalidUsuarioDataException.class)
	public ResponseEntity<ApiResponse<Void>> handleInvalidUsuario(
			InvalidUsuarioDataException ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST, ex.getMessage()));
	}
}