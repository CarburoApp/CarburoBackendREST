package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "repostaje")
@Getter
@Setter
@NoArgsConstructor
public class Repostaje {

	// PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, updatable = false)
	private Integer id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_vehiculo", nullable = false)
	private Vehiculo vehiculo;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_combustible", nullable = false)
	private Combustible combustible;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "id_eess", nullable = false)
	private EstacionDeServicio estacion;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "uuid_usuario", referencedColumnName = "uuid", nullable = false)
	private Usuario usuario;

	@Column(name = "fecha_repostaje")
	private OffsetDateTime fechaRepostaje;

	@Column(name = "cantidad", nullable = false, precision = 6, scale = 2)
	private BigDecimal cantidad;

	@Column(name = "coste_unitario", nullable = false, precision = 6, scale = 2)
	private BigDecimal costeUnitario;

	@Column(name = "odometro_inicial", precision = 9, scale = 2)
	private BigDecimal odometroInicial;

	@Column(name = "odometro_final", nullable = false, precision = 9, scale = 2)
	private BigDecimal odometroFinal;

	@Column(name = "deposito_lleno", nullable = false)
	private Boolean depositoLleno;

	@Column(name = "nota", length = 200)
	private String nota;

	@PrePersist
	public void prePersist() {
		if (fechaRepostaje == null) fechaRepostaje = OffsetDateTime.now();
	}

	public Repostaje(Vehiculo vehiculo, Combustible combustible,
					 EstacionDeServicio estacion, Usuario usuario, BigDecimal cantidad,
					 BigDecimal costeUnitario, BigDecimal odometroFinal,
					 Boolean depositoLleno) {
		this.vehiculo       = vehiculo;
		this.combustible    = combustible;
		this.estacion       = estacion;
		this.usuario        = usuario;
		this.cantidad       = cantidad;
		this.costeUnitario  = costeUnitario;
		this.odometroFinal  = odometroFinal;
		this.depositoLleno  = depositoLleno;
		this.fechaRepostaje = OffsetDateTime.now();
	}
}
