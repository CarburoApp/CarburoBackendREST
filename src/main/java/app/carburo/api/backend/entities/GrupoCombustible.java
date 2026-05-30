package app.carburo.api.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Entidad que representa un agrupamiento lógico de combustibles por compatibilidad de vehículos.
 */
@Entity
@Table(name = "grupo_combustible")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class GrupoCombustible {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private short id;

	@Column(length = 10, unique = true, nullable = false)
	private String codigo;

	@OneToMany(
			mappedBy = "grupoCombustible",
			cascade = {CascadeType.PERSIST, CascadeType.MERGE}
	)
	private final Set<Combustible> combustibles = new HashSet<>();


	public void setId(short id) {
		if (id < 0)
			throw new IllegalArgumentException("El ID del grupo no puede ser negativo");
		this.id = id;
	}

	public void setCodigo(String codigo) {
		if (codigo == null || codigo.trim().isEmpty())
			throw new IllegalArgumentException("El código del grupo es obligatorio");
		if (codigo.length() > 10) throw new IllegalArgumentException(
				"El código del grupo no puede superar los 10 caracteres");
		this.codigo = codigo.trim().toUpperCase();
	}

	/**
	 * Añade un combustible a este grupo manteniendo la sincronización bidireccional.
	 */
	public void addCombustible(Combustible combustible) {
		this.combustibles.add(combustible);
		combustible.setGrupoCombustible(this);
	}

	/**
	 * Elimina un combustible de este grupo manteniendo la sincronización bidireccional.
	 */
	public void removeCombustible(Combustible combustible) {
		this.combustibles.remove(combustible);
		combustible.setGrupoCombustible(null);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof GrupoCombustible that)) return false;
		return id == that.id;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public String toString() {
		return "GrupoCombustible{" + "id=" + id + ", codigo='" + codigo + '\'' + '}';
	}
}