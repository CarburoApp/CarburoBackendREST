package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
		setVehiculo(vehiculo);
		setUsuario(usuario);
		setPropietario(propietario);
		setId(new VehiculoUsuarioId(vehiculo.getId(), usuario.getUuid()));
	}

	public Boolean isPropietario() {return propietario;}
}
