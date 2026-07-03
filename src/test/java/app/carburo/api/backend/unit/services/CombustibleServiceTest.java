package app.carburo.api.backend.unit.services;

import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.dto.GrupoCombustibleDto;
import app.carburo.api.backend.entities.Combustible;
import app.carburo.api.backend.entities.GrupoCombustible;
import app.carburo.api.backend.services.CombustibleService;
import app.carburo.api.backend.services.queryServices.CombustibleQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("Unitario - CombustibleService")
class CombustibleServiceTest {

	@Mock
	private CombustibleQueryService queryService;

	@InjectMocks
	private CombustibleService service;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("Debe transformar combustibles a DTO correctamente")
	void shouldMapCombustiblesToDto() {

		Combustible c = buildCombustible((short) 1, "Gasolina 95", "G95", null);

		when(queryService.findAllCombustiblesCached()).thenReturn(List.of(c));

		List<CombustibleDto> result = service.getCombustiblesDto();

		assertEquals(1, result.size());
		assertEquals("Gasolina 95", result.getFirst().denominacion());
		assertEquals("G95", result.getFirst().codigo());
		assertEquals((short) 1, result.getFirst().id());
	}

	@Test
	@DisplayName("Debe agrupar combustibles por grupo correctamente")
	void shouldGroupCombustiblesByGroup() {

		GrupoCombustible g = buildGrupo((short) 1, "GAS");

		Combustible c1 = buildCombustible((short) 1, "Gasolina 95", "G95", g);
		Combustible c2 = buildCombustible((short) 2, "Gasolina 98", "G98", g);


		when(queryService.findAllCombustiblesCached()).thenReturn(List.of(c1, c2));
		when(queryService.findAllGruposDeCombustiblesCached()).thenReturn(List.of(g));

		List<GrupoCombustibleDto> result = service.getGruposDeCombustiblesConCombustibles();

		assertEquals(1, result.size());
		assertEquals(2, result.getFirst().combustibles().size());
	}

	@Test
	@DisplayName("Debe devolver lista vacía si no hay datos")
	void shouldReturnEmptySafely() {

		when(queryService.findAllCombustiblesCached()).thenReturn(List.of());
		when(queryService.findAllGruposDeCombustiblesCached()).thenReturn(List.of());

		List<GrupoCombustibleDto> result = service.getGruposDeCombustiblesConCombustibles();

		assertNotNull(result);
		assertTrue(result.isEmpty());
	}

	// helpers
	private Combustible buildCombustible(short id, String denom, String code, GrupoCombustible g) {
		Combustible c = new Combustible();
		c.setId(id);
		c.setDenominacion(denom);
		c.setCodigo(code);
		c.setExtCode((short) 10);
		c.setGrupoCombustible(g);
		return c;
	}

	private GrupoCombustible buildGrupo(short id, String code) {
		GrupoCombustible g = new GrupoCombustible();
		g.setId(id);
		g.setCodigo(code);
		return g;
	}
}