package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "vehiculo_usuario")
@Getter
@Setter
@NoArgsConstructor
public class VehiculoUsuario {

	@EmbeddedId
	private VehiculoUsuarioId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("vehiculoId")
	@JoinColumn(name = "id_vehiculo")
	private Vehiculo vehiculo;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("usuarioUuid")
	@JoinColumn(name = "uuid_usuario", referencedColumnName = "uuid")
	private Usuario usuario;

	@Column(name = "propietario", nullable = false)
	private Boolean propietario = false;

	public VehiculoUsuario(Vehiculo vehiculo,
						   Usuario usuario,
						   Boolean propietario) {
		this.vehiculo = vehiculo;
		this.usuario = usuario;
		this.propietario = propietario;
		this.id = new VehiculoUsuarioId(vehiculo.getId(), usuario.getUuid());
	}
}
