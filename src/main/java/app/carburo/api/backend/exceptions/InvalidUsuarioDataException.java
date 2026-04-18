package app.carburo.api.backend.exceptions;

public class InvalidUsuarioDataException extends RuntimeException {
	public InvalidUsuarioDataException(String message) {
		super(message);
	}
}