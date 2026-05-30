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

	@Column(name = "notas")
	private String notas;

	@Column(name = "fecha_registro")
	private OffsetDateTime fechaRegistro;

	@Column(name = "fecha_modificacion")
	private OffsetDateTime fechaModificacion;

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<Repostaje> repostajes = new HashSet<>();

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY)
	private Set<VehiculoUsuario> usuariosPropietarios = new HashSet<>();

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_combustible", nullable = false)
	private GrupoCombustible grupoCombustible;

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
					GrupoCombustible grupoCombustible, OffsetDateTime fechaRegistro,
					OffsetDateTime fechaModificacion) {
		setId(id);
		setMatricula(matricula);
		setMarca(marca);
		setModelo(modelo);
		setOdometroActual(odometroActual);
		setCapacidadDeposito(capacidadDeposito);
		setNotas(notas);
		setGrupoCombustible(grupoCombustible);
		setFechaRegistro(fechaRegistro);
		setFechaModificacion(fechaModificacion);
	}

	public Vehiculo(int id, String matricula, String marca, String modelo,
					double odometroActual, double capacidadDeposito,
					GrupoCombustible grupoCombustible, String notas) {
		this(id, matricula, marca, modelo, odometroActual, capacidadDeposito, notas,
			 grupoCombustible, OffsetDateTime.now(), OffsetDateTime.now());
	}

	public Vehiculo(String matricula, String marca, String modelo, double odometroActual,
					double capacidadDeposito, GrupoCombustible grupoCombustible,
					String notas) {
		this(null, matricula, marca, modelo, odometroActual, capacidadDeposito, notas,
			 grupoCombustible, OffsetDateTime.now(), OffsetDateTime.now());
	}

	public void setOdometroActual(double odometroActual) {
		if (odometroActual < 0) throw new IllegalArgumentException(
				"El odómetro actual no puede ser negativo ni nulo.");
		this.odometroActual = BigDecimal.valueOf(odometroActual);
	}

	public void setCapacidadDeposito(double capacidadDeposito) {
		if (capacidadDeposito < 0) throw new IllegalArgumentException(
				"La capacidad del deposito no puede ser negativo ni nulo.");
		this.capacidadDeposito = BigDecimal.valueOf(capacidadDeposito);
	}
}
