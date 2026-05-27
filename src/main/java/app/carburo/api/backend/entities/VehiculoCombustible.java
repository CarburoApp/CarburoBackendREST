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
@Table(name = "vehiculo_combustible")
@Getter
@Setter
@NoArgsConstructor
public class VehiculoCombustible {

	@EmbeddedId
	private VehiculoCombustibleId id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("vehiculoId")
	@JoinColumn(name = "id_vehiculo")
	private Vehiculo vehiculo;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("combustibleId")
	@JoinColumn(name = "id_combustible")
	private Combustible combustible;

	@Column(name = "capacidad_deposito", nullable = false, precision = 5, scale = 1)
	private BigDecimal capacidadDeposito;

	public VehiculoCombustible(Vehiculo vehiculo,
							   Combustible combustible,
							   BigDecimal capacidadDeposito) {
		this.vehiculo = vehiculo;
		this.combustible = combustible;
		this.capacidadDeposito = capacidadDeposito;
		this.id = new VehiculoCombustibleId(vehiculo.getId(), combustible.getId());
	}
}
