package app.carburo.api.backend.exceptions;

import java.util.UUID;

public class UsuarioAlreadyExistsException extends RuntimeException {
	public UsuarioAlreadyExistsException(UUID uuid) {
		super("El usuario ya existe con UUID: " + uuid);
	}
}
