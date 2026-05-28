package app.carburo.api.backend.exceptions;

public class InvalidVehiculoDataException extends RuntimeException {
	public InvalidVehiculoDataException(String message) {
		super(message);
	}
}