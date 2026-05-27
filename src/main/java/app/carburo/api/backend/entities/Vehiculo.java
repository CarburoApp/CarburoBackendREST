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
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

	// PK
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(nullable = false, updatable = false)
	private Integer id;

	@Column(name = "denominacion", nullable = false, length = 20)
	private String denominacion;

	@Column(name = "odometro_inicial", nullable = false, precision = 9, scale = 2)
	private BigDecimal odometroInicial;

	@Column(name = "odometro_actual", nullable = false, precision = 9, scale = 2)
	private BigDecimal odometroActual;

	@Column(name = "capacidad_deposito",
			nullable = false,
			precision = 5,
			scale = 1)
	private BigDecimal capacidadDeposito;

	@Column(name = "fecha_registro")
	private OffsetDateTime fechaRegistro;

	@Column(name = "fecha_modificacion")
	private OffsetDateTime fechaModificacion;

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<Repostaje> repostajes = new HashSet<>();

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<VehiculoUsuario> usuarios = new HashSet<>();

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(
			name = "vehiculo_combustible",
			joinColumns = @JoinColumn(
					name = "id_vehiculo",
					referencedColumnName = "id"
			),
			inverseJoinColumns = @JoinColumn(
					name = "id_combustible",
					referencedColumnName = "id"
			)
	)
	private Set<Combustible> combustibles = new HashSet<>();

	@PrePersist
	public void prePersist() {
		if (fechaRegistro == null) fechaRegistro = OffsetDateTime.now();
		if (fechaModificacion == null) fechaModificacion = OffsetDateTime.now();
	}

	@PreUpdate
	public void preUpdate() {
		fechaModificacion = OffsetDateTime.now();
	}


	public Vehiculo(Integer id, String denominacion, BigDecimal odometroInicial,
					BigDecimal odometroActual, OffsetDateTime fechaRegistro,
					OffsetDateTime fechaModificacion) {
		this.id                = id;
		this.denominacion      = denominacion;
		this.odometroInicial   = odometroInicial;
		this.odometroActual    = odometroActual;
		this.fechaRegistro     = fechaRegistro;
		this.fechaModificacion = fechaModificacion;
	}

	public Vehiculo(String denominacion, BigDecimal odometroInicial,
					BigDecimal odometroActual) {
		this.denominacion      = denominacion;
		this.odometroInicial   = odometroInicial;
		this.odometroActual    = odometroActual;
		this.fechaRegistro     = OffsetDateTime.now();
		this.fechaModificacion = OffsetDateTime.now();
	}
}
