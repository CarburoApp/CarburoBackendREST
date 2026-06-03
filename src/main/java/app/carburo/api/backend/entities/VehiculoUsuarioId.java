package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class VehiculoUsuarioId implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private Integer vehiculoId;
	private UUID usuarioUuid;

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		VehiculoUsuarioId that = (VehiculoUsuarioId) o;
		return Objects.equals(vehiculoId, that.vehiculoId) &&
				Objects.equals(usuarioUuid, that.usuarioUuid);
	}

	@Override
	public int hashCode() {
		return Objects.hash(vehiculoId, usuarioUuid);
	}

	@Override
	public String toString() {
		return "VehiculoUsuarioId{" + "vehiculoId=" + vehiculoId + ", usuarioUuid=" +
				usuarioUuid + '}';
	}
}