package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "vehiculo")
@Getter
@Setter
@NoArgsConstructor
public class Vehiculo {

	// Constantes de validación
	public static final int ODOMETRO_MIN_VALUE = 0;
	public static final double ODOMETRO_MAX_VALUE = 9_999_999.99;
	public static final double CAPACIDAD_DEPOSITO_MIN_VALUE = 0;
	public static final double CAPACIDAD_DEPOSITO_MAX_VALUE = 99_999.99;

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

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<Repostaje> repostajes = new HashSet<>();

	@OneToMany(mappedBy = "vehiculo", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
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

	public void setMarca(String marca) {
		if (marca == null || marca.trim().isEmpty())
			throw new IllegalArgumentException("La marca no puede estar vacía.");
		if (marca.trim().length() > 40) throw new IllegalArgumentException(
				"La marca no puede superar los 40 caracteres.");
		this.marca = marca.trim();
	}

	public void setModelo(String modelo) {
		if (modelo == null || modelo.trim().isEmpty())
			throw new IllegalArgumentException("El modelo no puede estar vacío.");
		if (modelo.trim().length() > 40) throw new IllegalArgumentException(
				"El modelo no puede superar los 40 caracteres.");
		this.modelo = modelo.trim();
	}

	public void setMatricula(String matricula) {
		if (matricula == null || matricula.trim().isEmpty()) {
			this.matricula = null;
			return;
		}
		if (matricula.trim().length() > 20) throw new IllegalArgumentException(
				"La matrícula no puede superar los 20 caracteres.");
		this.matricula = matricula.trim();
	}

	public void setOdometroActual(double odometroActual) {
		if (odometroActual < ODOMETRO_MIN_VALUE || odometroActual > ODOMETRO_MAX_VALUE)
			throw new IllegalArgumentException(
					"El odómetro actual no puede ser negativo ni mayor a 9.999.999,99");
		this.odometroActual = BigDecimal.valueOf(odometroActual)
				.setScale(2, RoundingMode.HALF_UP);
	}

	public void setCapacidadDeposito(double capacidadDeposito) {
		if (capacidadDeposito < CAPACIDAD_DEPOSITO_MIN_VALUE || capacidadDeposito > CAPACIDAD_DEPOSITO_MAX_VALUE)
			throw new IllegalArgumentException(
					"La capacidad del deposito no puede ser negativo ni mayor a 99.999,99");
		this.capacidadDeposito = BigDecimal.valueOf(capacidadDeposito)
				.setScale(2, RoundingMode.HALF_UP);
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Vehiculo vehiculo = (Vehiculo) o;
		return Objects.equals(id, vehiculo.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}
