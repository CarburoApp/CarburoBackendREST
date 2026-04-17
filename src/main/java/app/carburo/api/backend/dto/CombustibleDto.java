package app.carburo.api.backend.dto;

import app.carburo.api.backend.entities.Combustible;
import lombok.Getter;

@Getter
public class CombustibleDto {

	public short id;
	public String denominacion;
	public String codigo;

	public CombustibleDto(short id, String denominacion, String codigo) {
		this.id             = id;
		this.denominacion   = denominacion;
		this.codigo         = codigo;
	}

	public CombustibleDto(Combustible combustible) {
		this.id             = combustible.getId();
		this.denominacion   = combustible.getDenominacion();
		this.codigo         = combustible.getCodigo();
	}
}
