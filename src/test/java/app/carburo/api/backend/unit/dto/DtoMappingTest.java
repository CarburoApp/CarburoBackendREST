package app.carburo.api.backend.unit.dto;

import app.carburo.api.backend.dto.*;
import app.carburo.api.backend.entities.*;
import app.carburo.api.backend.entities.enums.Margen;
import app.carburo.api.backend.entities.enums.Remision;
import app.carburo.api.backend.entities.enums.Venta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Unitario - DTO Mapping Completo")
class DtoMappingTest {

	@Test
	@DisplayName("GrupoDeCombustible -> DTO")
	void grupoDeCombustibleToDto() {
		GrupoCombustible c = new GrupoCombustible((short) 1, "G95");
		GrupoCombustibleDto dto = GrupoCombustibleDto.from(c);

		assertEquals(1, dto.id());
		assertEquals("G95", dto.codigo());
	}

	@Test
	@DisplayName("Combustible -> DTO")
	void combustibleToDto() {
		Combustible c = new Combustible((short) 1, "Gasolina 95", "G95", (short) 10,
										null);
		CombustibleDto dto = CombustibleDto.from(c);

		assertEquals(1, dto.id());
		assertEquals("Gasolina 95", dto.denominacion());
		assertEquals("G95", dto.codigo());
		assertNull(dto.id_grupo_combustible());
	}

	@Test
	@DisplayName("ComunidadAutonoma -> DTO Básico y Completo")
	void comunidadAutonomaToDto() {
		ComunidadAutonoma ca = new ComunidadAutonoma((short) 1, "Andalucía", (short) 4);

		// Test from básico
		ComunidadAutonomaDto dtoBasico = ComunidadAutonomaDto.from(ca);
		assertEquals(1, dtoBasico.id());
		assertEquals("Andalucía", dtoBasico.denominacion());
		assertNull(dtoBasico.provincias());

		// Test con Provincias anidadas usando Mocks para aislar relaciones
		Provincia p1 = Mockito.mock(Provincia.class);
		ComunidadAutonoma mockCa = Mockito.mock(ComunidadAutonoma.class);
		when(p1.getId()).thenReturn((short) 11);
		when(p1.getDenominacion()).thenReturn("Sevilla");
		when(p1.getComunidadAutonoma()).thenReturn(mockCa);
		when(mockCa.getId()).thenReturn((short) 1);
		when(p1.getMunicipios()).thenReturn(Set.of());

		ca.setProvincias(Set.of(p1));
		ComunidadAutonomaDto dtoCompleto = ComunidadAutonomaDto.fromWithProvincias(ca);

		assertEquals(1, dtoCompleto.provincias().size());
		assertEquals("Sevilla", dtoCompleto.provincias().getFirst().denominacion());
	}

	@Test
	@DisplayName("Municipio -> DTO")
	void municipioToDto() {
		Provincia p = Mockito.mock(Provincia.class);
		when(p.getId()).thenReturn((short) 33);

		Municipio m = new Municipio((short) 5, "Gijón", (short) 501, p);
		MunicipioDto dto = MunicipioDto.from(m);

		assertEquals(5, dto.id());
		assertEquals("Gijón", dto.denominacion());
		assertEquals(33, dto.id_provincia());
	}

	@Test
	@DisplayName("Provincia -> DTO Básico y Completo")
	void provinciaToDto() {
		ComunidadAutonoma ca = Mockito.mock(ComunidadAutonoma.class);
		when(ca.getId()).thenReturn((short) 3);

		Provincia p = new Provincia();
		p.setId((short) 33);
		p.setDenominacion("Asturias");
		p.setComunidadAutonoma(ca);

		ProvinciaDto dto = ProvinciaDto.from(p);
		assertEquals(33, dto.id());
		assertNull(dto.municipios());

		// Con municipios
		Municipio m = new Municipio((short) 1, "Oviedo", (short) 101, p);
		p.setMunicipios(Set.of(m));

		ProvinciaDto dtoCompleto = ProvinciaDto.fromWithMunicipios(p);
		assertEquals(1, dtoCompleto.municipios().size());
		assertEquals("Oviedo", dtoCompleto.municipios().getFirst().denominacion());
	}

	@Test
	@DisplayName("PrecioCombustible -> DTO")
	void precioCombustibleToDto() {
		EstacionDeServicio eess = Mockito.mock(EstacionDeServicio.class);
		Combustible c = Mockito.mock(Combustible.class);
		when(eess.getId()).thenReturn(100);
		when(c.getId()).thenReturn((short) 2);

		LocalDate hoy = LocalDate.now();
		PrecioCombustible pc = new PrecioCombustible(eess, c, hoy, 1.649);

		PrecioCombustibleDto dto = PrecioCombustibleDto.from(pc);
		assertEquals(100, dto.id_estacion_de_servicio());
		assertEquals(2, dto.id_combustible());
		assertEquals(hoy, dto.fecha());
		assertEquals(1.649, dto.precio());
	}

	@Test
	@DisplayName("EstacionDeServicio -> DTO (Sobrecargas de Constructor)")
	void estacionDeServicioToDto() {
		EstacionDeServicio e = Mockito.mock(EstacionDeServicio.class);
		Municipio m = Mockito.mock(Municipio.class);
		Provincia p = Mockito.mock(Provincia.class);
		Coordenada coord = new Coordenada(43.53, -5.66);
		Combustible c = Mockito.mock(Combustible.class);

		when(e.getId()).thenReturn(50);
		when(e.getRotulo()).thenReturn("CEPSA");
		when(e.getMunicipio()).thenReturn(m);
		when(m.getId()).thenReturn((short) 5);
		when(e.getProvincia()).thenReturn(p);
		when(p.getId()).thenReturn((short) 33);
		when(e.getCoordenada()).thenReturn(coord);
		when(e.getMargen()).thenReturn(Margen.DERECHO); // Asumiendo Enums válidos
		when(e.getRemision()).thenReturn(Remision.DM);
		when(e.getVenta()).thenReturn(Venta.PUBLICA);
		when(c.getId()).thenReturn((short) 1);
		when(e.getCombustiblesDisponibles()).thenReturn(Set.of(c));

		// Test sobrecarga básica (sin distancia ni precios)
		EstacionDeServicioDto dto = EstacionDeServicioDto.from(e);
		assertNull(dto.distancia_metros());
		assertNull(dto.precios_de_combustibles());
		assertEquals(1, dto.id_combustibles_disponibles().size());

		// Test sobrecarga completa
		List<PrecioCombustibleDto> precios = List.of(
				new PrecioCombustibleDto(50, (short) 1, LocalDate.now(), 1.50));
		EstacionDeServicioDto dtoCompleto = EstacionDeServicioDto.from(e, 1200L, precios);

		assertEquals(1200L, dtoCompleto.distancia_metros());
		assertEquals(1, dtoCompleto.precios_de_combustibles().size());
		assertEquals(43.53, dtoCompleto.latitud());
	}

	@Test
	@DisplayName("Repostaje -> DTO")
	void repostajeToDto() {
		Repostaje r = Mockito.mock(Repostaje.class);
		Vehiculo v = Mockito.mock(Vehiculo.class);
		Combustible c = Mockito.mock(Combustible.class);
		EstacionDeServicio e = Mockito.mock(EstacionDeServicio.class);
		Usuario u = Mockito.mock(Usuario.class);
		UUID userUuid = UUID.randomUUID();

		when(r.getId()).thenReturn(1);
		when(r.getVehiculo()).thenReturn(v);
		when(v.getId()).thenReturn(10);
		when(r.getCombustible()).thenReturn(c);
		when(c.getId()).thenReturn((short) 1);
		when(r.getEstacionDeServicio()).thenReturn(e);
		when(e.getId()).thenReturn(20);
		when(r.getUsuario()).thenReturn(u);
		when(u.getUuid()).thenReturn(userUuid);
		when(r.getFechaRepostaje()).thenReturn(OffsetDateTime.MIN);
		when(r.getFechaRegistro()).thenReturn(OffsetDateTime.MAX);
		when(r.getCantidad()).thenReturn(java.math.BigDecimal.valueOf(50.5));
		when(r.getCosteUnitario()).thenReturn(java.math.BigDecimal.valueOf(1.55));
		when(r.getOdometroInicial()).thenReturn(java.math.BigDecimal.valueOf(10000));
		when(r.getOdometroFinal()).thenReturn(java.math.BigDecimal.valueOf(1050.5));
		when(r.getDepositoLleno()).thenReturn(true);
		when(r.getNota()).thenReturn("Sin plomo");

		RepostajeDto dto = RepostajeDto.from(r);
		assertEquals(1, dto.id());
		assertEquals(10, dto.id_vehiculo());
		assertEquals(50.5, dto.cantidad());
		assertEquals(10000.0, dto.odometro_inicial());
	}

	@Test
	@DisplayName("Usuario -> DTO")
	void usuarioToDto() {
		Usuario u = Mockito.mock(Usuario.class);
		Provincia p = Mockito.mock(Provincia.class);
		Combustible c = Mockito.mock(Combustible.class);
		EstacionDeServicio e = Mockito.mock(EstacionDeServicio.class);
		UUID uuid = UUID.randomUUID();

		when(u.getUuid()).thenReturn(uuid);
		when(u.getProvinciaFavorita()).thenReturn(p);
		when(p.getId()).thenReturn((short) 33);
		when(c.getId()).thenReturn((short) 1);
		when(u.getCombustiblesFavoritos()).thenReturn(Set.of(c));
		when(e.getId()).thenReturn(5);
		when(u.getEessFavoritas()).thenReturn(Set.of(e));

		UsuarioDto dto = UsuarioDto.from(u);
		assertEquals(uuid, dto.uuid());
		assertEquals(33, dto.id_provincia_favorita());
		assertTrue(dto.ids_combustibles_favoritos().contains((short) 1));
		assertTrue(dto.ids_estaciones_de_servicio_favoritas().contains(5));
	}

	@Test
	@DisplayName("Vehiculo -> DTO con flags de propiedad")
	void vehiculoToDto() {
		Vehiculo v = Mockito.mock(Vehiculo.class);
		GrupoCombustible g = Mockito.mock(GrupoCombustible.class);
		UUID uuid = UUID.randomUUID();

		when(v.getId()).thenReturn(9);
		when(v.getMatricula()).thenReturn("1234XYZ");
		when(v.getOdometroActual()).thenReturn(java.math.BigDecimal.valueOf(12000.50));
		when(v.getCapacidadDeposito()).thenReturn(java.math.BigDecimal.valueOf(55.0));
		when(v.getGrupoCombustible()).thenReturn(g);
		when(g.getId()).thenReturn((short) 2);

		VehiculoDto dto = VehiculoDto.from(v, uuid, true);
		assertEquals(9, dto.id());
		assertEquals(uuid, dto.uuid_usuario_solicitante());
		assertTrue(dto.is_usuario_solicitante_propietario());
		assertEquals(12000.50, dto.odometro_actual());
		assertEquals(2, dto.id_grupo_combustible());
	}
}