package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collection;
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

	@Column(name = "marca", nullable = false, length = 40)
	private String marca;

	@Column(name = "modelo", nullable = false, length = 40)
	private String modelo;

	@Column(name = "matricula", length = 20)
	private String matricula;

	@Column(name = "odometro_actual", nullable = false, precision = 9, scale = 2)
	private BigDecimal odometroActual;

	@Column(name = "capacidad_deposito",
			nullable = false,
			precision = 5,
			scale = 1)
	private BigDecimal capacidadDeposito;

	@Column(name = "matricula")
	private String notas;

	@Column(name = "fecha_registro")
	private OffsetDateTime fechaRegistro;

	@Column(name = "fecha_modificacion")
	private OffsetDateTime fechaModificacion;

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<Repostaje> repostajes = new HashSet<>();

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<VehiculoUsuario> usuariosPropietarios = new HashSet<>();

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


	public Vehiculo(Integer id, String matricula, String marca, String modelo,
					double odometroActual, double capacidadDeposito, String notas,
					Collection<Combustible> combustibles, OffsetDateTime fechaRegistro,
					OffsetDateTime fechaModificacion) {
		setId(id);
		setMatricula(matricula);
		setMarca(marca);
		setModelo(modelo);
		setOdometroActual(odometroActual);
		setCapacidadDeposito(capacidadDeposito);
		setNotas(notas);
		setCombustibles(new HashSet<>(combustibles));
		setFechaRegistro(fechaRegistro);
		setFechaModificacion(fechaModificacion);
	}

	public Vehiculo(int id, String matricula, String marca, String modelo,
					double odometroActual, double capacidadDeposito,
					Collection<Combustible> combustibles, String notas) {
		this(id, matricula, marca, modelo, odometroActual, capacidadDeposito, notas,
			 combustibles, OffsetDateTime.now(), OffsetDateTime.now());
	}

	public Vehiculo(String matricula, String marca, String modelo, double odometroActual,
					double capacidadDeposito, Collection<Combustible> combustibles,
					String notas) {
		this(null, matricula, marca, modelo, odometroActual, capacidadDeposito, notas,
			 combustibles, OffsetDateTime.now(), OffsetDateTime.now());
	}

	public void setOdometroActual(double odometroActual) {
		if (odometroActual < 0) throw new IllegalArgumentException(
				"El odómetro actual no puede ser negativo ni nulo.");
		this.odometroActual = BigDecimal.valueOf(odometroActual);
	}

	public void setCapacidadDeposito(double capacidadDeposito) {
		if (capacidadDeposito < 0) throw new IllegalArgumentException(
				"La capacidad del deposito no puede ser negativo ni nulo.");
		this.odometroActual = BigDecimal.valueOf(capacidadDeposito);
	}
}
