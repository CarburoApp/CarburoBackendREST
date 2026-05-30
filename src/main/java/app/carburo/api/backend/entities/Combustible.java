package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa un tipo de combustible.
 * <p>
 * Cada combustible puede estar disponible en varias estaciones de servicio,
 * manteniendo así una relación Muchos a Muchos con {@link EstacionDeServicio}.
 * <p>
 * Campos principales:
 * <ul>
 *     <li>id: Identificador interno único</li>
 *     <li>denominacion: Nombre del combustible (Ej: Gasolina 95, GLP, AdBlue)</li>
 *     <li>codigo: Código abreviado interno</li>
 *     <li>extCode: Código externo único para sincronización con otros sistemas</li>
 * </ul>
 */
@Entity
@Getter
@NoArgsConstructor
public class Combustible {

	// ==============================
	// CAMPOS
	// ==============================

	/** Identificador único del combustible. Generado automáticamente por la BD */
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private short id;

	/** Nombre completo del combustible. Obligatorio, longitud máxima 50 */
	@Column(nullable = false, length = 50)
	private String denominacion;

	/** Código abreviado o interno del combustible. Longitud máxima 10, único */
	@Column(length = 10, unique = true, nullable = false)
	private String codigo;

	/** Código externo único para sincronización con sistemas externos */
	@Column(name = "ext_code", unique = true, nullable = false)
	private short extCode;

	/**
	 * Asigna el grupo lógico al que pertenece el combustible (puede ser null).
	 */
	@Setter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_grupo_combustible", nullable = true, insertable = false, updatable = false)
	private GrupoCombustible grupoCombustible;

	// Campo espejo que apunta a la misma columna de la BD, pero es un tipo primitivo/básico
	@Column(name = "id_grupo_combustible")
	private Short idGrupoCombustible;

	/**
	 * Relación Muchos a Muchos con estaciones de servicio.
	 * Un combustible puede estar disponible en varias estaciones.
	 * Se mapea desde la propiedad "combustiblesDisponibles" de {@link EstacionDeServicio}.
	 */
	@ManyToMany(mappedBy = "combustiblesDisponibles")
	private final Set<EstacionDeServicio> estacionesDeServicio = new HashSet<>();

	// ==============================
	// CONSTRUCTORES
	// ==============================

	/**
	 * Constructor completo.
	 *
	 * @param id           Identificador interno
	 * @param denominacion Nombre del combustible
	 * @param codigo       Código abreviado interno
	 * @param extCode      Código externo único
	 * @param grupoCombustible Grupo lógico al que pertenece el combustible (puede ser null)
	 */
	public Combustible(short id, String denominacion, String codigo, short extCode,
					   GrupoCombustible grupoCombustible) {
		setId(id);
		setDenominacion(denominacion);
		setCodigo(codigo);
		setExtCode(extCode);
		setGrupoCombustible(grupoCombustible);
		this.idGrupoCombustible = grupoCombustible.getId();
	}

	// ==============================
	// SETTERS CON VALIDACIÓN
	// ==============================

	/**
	 * Asigna el ID del combustible.
	 * @param id Debe ser positivo
	 */
	public void setId(short id) {
		if (id < 0) throw new IllegalArgumentException(
				"El ID del combustible no puede ser negativo");
		this.id = id;
	}

	/**
	 * Asigna la denominación del combustible.
	 * @param denominacion Obligatorio, máximo 50 caracteres
	 */
	public void setDenominacion(String denominacion) {
		if (denominacion == null || denominacion.trim().isEmpty())
			throw new IllegalArgumentException(
					"La denominación del combustible es obligatoria");
		if (denominacion.length() > 50)
			throw new IllegalArgumentException(
					"La denominación no puede superar los 50 caracteres");
		this.denominacion = denominacion.trim();
	}

	/**
	 * Asigna el código abreviado interno.
	 * @param codigo Máximo 10 caracteres
	 */
	public void setCodigo(String codigo) {
		if (codigo != null && codigo.length() > 10)
			throw new IllegalArgumentException(
					"El código del combustible no puede superar los 10 caracteres");
		this.codigo = (codigo != null) ? codigo.trim() : null;
	}

	/**
	 * Asigna el código externo.
	 * @param extCode Debe ser positivo
	 */
	public void setExtCode(short extCode) {
		if (extCode < 0)
			throw new IllegalArgumentException(
					"El Ext_Code del combustible no puede ser negativo");
		this.extCode = extCode;
	}

	// ==============================
	// MÉTODOS COMUNES
	// ==============================

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Combustible that)) return false;
		return Objects.equals(id, that.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	/**
	 * Representación en cadena del combustible.
	 */
	@Override
	public String toString() {
		return "Combustible{" +
				"id=" + id +
				", denominacion='" + denominacion + '\'' +
				", codigo='" + codigo + '\'' +
				", extCode=" + extCode +
				'}';
	}
}
