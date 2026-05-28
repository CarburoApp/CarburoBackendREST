package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "repostaje")
@Getter
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
	private EstacionDeServicio estacionDeServicio;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "uuid_usuario", referencedColumnName = "uuid", nullable = false)
	private Usuario usuario;

	@Column(name = "fecha_repostaje", nullable = false)
	private OffsetDateTime fechaRepostaje;

	@Column(name = "fecha_registro", nullable = false)
	private OffsetDateTime fechaRegistro;

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
		if (cantidad <= 0) throw new IllegalArgumentException(
				"La cantidad repostada debe ser mayor que 0.");

		this.cantidad = BigDecimal.valueOf(cantidad);
	}

	public void setCosteUnitario(double costeUnitario) {
		if (costeUnitario < 0) throw new IllegalArgumentException(
				"El coste unitario no puede ser negativo.");

		this.costeUnitario = BigDecimal.valueOf(costeUnitario);
	}

	public void setOdometroInicial(Double odometroInicial) {
		if (odometroInicial != null && odometroInicial < 0)
			throw new IllegalArgumentException(
					"El odómetro inicial no puede ser negativo.");

		this.odometroInicial = (odometroInicial == null) ? null : BigDecimal.valueOf(
				odometroInicial);
	}

	public void setOdometroFinal(double odometroFinal) {
		if (odometroFinal < 0) throw new IllegalArgumentException(
				"El odómetro final no puede ser negativo.");
		if (this.odometroInicial != null &&
				BigDecimal.valueOf(odometroFinal).compareTo(this.odometroInicial) < 0)
			throw new IllegalArgumentException(
					"El odómetro final no puede ser menor al inicial.");

		this.odometroFinal = BigDecimal.valueOf(odometroFinal);
	}

	public void setDepositoLleno(Boolean depositoLleno) {
		if (depositoLleno == null) throw new IllegalArgumentException(
				"El estado del depósito no puede ser nulo.");

		this.depositoLleno = depositoLleno;
	}

	public void setNota(String nota) {

		if (nota != null && nota.length() > 100) throw new IllegalArgumentException(
				"La nota no puede superar los 100 caracteres.");

		this.nota = nota;
	}
}


