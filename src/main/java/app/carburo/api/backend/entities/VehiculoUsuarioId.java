package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class VehiculoUsuarioId implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer vehiculoId;
	private UUID usuarioUuid;
}