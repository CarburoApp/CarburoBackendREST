package app.carburo.api.backend.controllers.utilities;

import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static app.carburo.api.backend.controllers.utilities.HttpConstants.ERR_BAD_REQUEST;
import static app.carburo.api.backend.controllers.utilities.HttpConstants.ERR_NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiResponse<Void>> handleNotFound(
			ResourceNotFoundException ex) {

		return ResponseEntity.status(HttpStatus.NOT_FOUND)
				.body(ApiResponse.error(ERR_NOT_FOUND, ex.getMessage()));
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiResponse<Void>> handleBadRequest(Exception ex) {

		return ResponseEntity.status(HttpStatus.BAD_REQUEST)
				.body(ApiResponse.error(ERR_BAD_REQUEST, "Parámetro inválido"));
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
}