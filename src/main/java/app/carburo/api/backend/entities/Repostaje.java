package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.Objects;

import static app.carburo.api.backend.entities.Vehiculo.*;

@Entity
@Table(name = "repostaje")
@Getter
@NoArgsConstructor
public class Repostaje {

	public static final double COSTE_UNITARIO_MIN_VALUE = 0;
	public static final double COSTE_UNITARIO_MAX_VALUE = 999.999;

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
	private EstacionDeServicio estacionDeServicio;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "uuid_usuario", referencedColumnName = "uuid", nullable = false)
	private Usuario usuario;

	@Column(name = "fecha_repostaje", nullable = false)
	private OffsetDateTime fechaRepostaje;

	@Column(name = "fecha_registro", nullable = false)
	private OffsetDateTime fechaRegistro;

	@Column(name = "cantidad", nullable = false, precision = 7, scale = 2)
	private BigDecimal cantidad;

	@Column(name = "coste_unitario", nullable = false, precision = 6, scale = 3)
	private BigDecimal costeUnitario;

	@Column(name = "odometro_inicial", precision = 9, scale = 2)
	private BigDecimal odometroInicial;

	@Column(name = "odometro_final", nullable = false, precision = 9, scale = 2)
	private BigDecimal odometroFinal;

	@Column(name = "deposito_lleno", nullable = false)
	private Boolean depositoLleno;

	@Column(name = "nota", length = 100)
	private String nota;

	@PrePersist
	public void prePersist() {
		if (fechaRepostaje == null) fechaRepostaje = OffsetDateTime.now();
	}

	public Repostaje(Vehiculo vehiculo, Combustible combustible,
					 EstacionDeServicio estacionDeServicio, Usuario usuario,
					 double cantidad, double costeUnitario, Double odometroInicial,
					 double odometroFinal, Boolean depositoLleno, String nota) {
		setVehiculo(vehiculo);
		setCombustible(combustible);
		setEstacionDeServicio(estacionDeServicio);
		setUsuario(usuario);
		setCantidad(cantidad);
		setCosteUnitario(costeUnitario);
		setOdometroInicial(odometroInicial);
		setOdometroFinal(odometroFinal);
		setDepositoLleno(depositoLleno);
		setNota(nota);
		setFechaRepostaje(OffsetDateTime.now());
		setFechaRegistro(OffsetDateTime.now());
	}

	/// SETTERS

	public void setVehiculo(Vehiculo vehiculo) {
		if (vehiculo == null) throw new IllegalArgumentException(
				"El vehículo del repostaje no puede ser nulo.");

		this.vehiculo = vehiculo;
	}

	public void setCombustible(Combustible combustible) {
		if (combustible == null) throw new IllegalArgumentException(
				"El combustible del repostaje no puede ser nulo.");

		this.combustible = combustible;
	}

	public void setEstacionDeServicio(EstacionDeServicio estacionDeServicio) {
		if (estacionDeServicio == null) throw new IllegalArgumentException(
				"La estación de servicio no puede ser nula.");

		this.estacionDeServicio = estacionDeServicio;
	}

	public void setUsuario(Usuario usuario) {
		if (usuario == null) throw new IllegalArgumentException(
				"El usuario creador del repostaje no puede ser nulo.");

		this.usuario = usuario;
	}

	public void setFechaRepostaje(OffsetDateTime fechaRepostaje) {
		if (fechaRepostaje == null) throw new IllegalArgumentException(
				"La fecha del repostaje no puede ser nula.");

		this.fechaRepostaje = fechaRepostaje;
	}

	public void setFechaRegistro(OffsetDateTime fechaRegistro) {
		if (fechaRegistro == null)
			throw new IllegalArgumentException("La fecha de registro no puede ser nula.");

		this.fechaRegistro = fechaRegistro;
	}

	public void setCantidad(double cantidad) {
		if (cantidad <= CAPACIDAD_DEPOSITO_MIN_VALUE || cantidad > CAPACIDAD_DEPOSITO_MAX_VALUE) throw new IllegalArgumentException(
				"La cantidad repostada no puede ser menor o igual a 0 ni mayor a 99.999,99.");
		this.cantidad = BigDecimal.valueOf(cantidad).setScale(2, RoundingMode.HALF_UP);
	}

	public void setCosteUnitario(double costeUnitario) {
		if (costeUnitario < COSTE_UNITARIO_MIN_VALUE || costeUnitario > COSTE_UNITARIO_MAX_VALUE) throw new IllegalArgumentException(
				"El coste unitario no puede ser menor o igual a cero ni mayor 999,999.");

		this.costeUnitario = BigDecimal.valueOf(costeUnitario).setScale(3, RoundingMode.HALF_UP);
	}

	public void setOdometroInicial(Double odometroInicial) {
		if (odometroInicial != null &&
				(odometroInicial < ODOMETRO_MIN_VALUE || odometroInicial > ODOMETRO_MAX_VALUE))
			throw new IllegalArgumentException(
					"El odómetro inicial no puede ser negativo ni mayor a 9.999.999,99.");

		this.odometroInicial = (odometroInicial == null) ? null : BigDecimal.valueOf(
				odometroInicial).setScale(2, RoundingMode.HALF_UP);
	}

	public void setOdometroFinal(double odometroFinal) {
		if (odometroFinal < ODOMETRO_MIN_VALUE || odometroFinal > ODOMETRO_MAX_VALUE)
			throw new IllegalArgumentException(
					"El odómetro final no puede ser negativo ni mayor a 9.999.999,99.");
		if (this.odometroInicial != null &&
				BigDecimal.valueOf(odometroFinal).compareTo(this.odometroInicial) < 0)
			throw new IllegalArgumentException(
					"El odómetro final no puede ser menor al inicial.");

		this.odometroFinal = BigDecimal.valueOf(odometroFinal)
				.setScale(2, RoundingMode.HALF_UP);
	}

	public void setDepositoLleno(Boolean depositoLleno) {
		if (depositoLleno == null) throw new IllegalArgumentException(
				"El estado del depósito no puede ser nulo.");

		this.depositoLleno = depositoLleno;
	}

	public void setNota(String nota) {
		if (nota != null && nota.trim().length() > 100)
			throw new IllegalArgumentException(
				"La nota no puede superar los 100 caracteres.");

		this.nota = nota == null ? null : nota.trim();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Repostaje repostaje = (Repostaje) o;
		return Objects.equals(id, repostaje.id);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
	}
}


