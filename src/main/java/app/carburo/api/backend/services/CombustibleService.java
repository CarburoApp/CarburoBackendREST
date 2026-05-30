package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.dto.GrupoCombustibleDto;
import app.carburo.api.backend.services.queryServices.CombustibleQueryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CombustibleService {

	private final CombustibleQueryService combustibleQueryService;

	public CombustibleService(CombustibleQueryService combustibleQueryService) {
		this.combustibleQueryService = combustibleQueryService;
	}

	@Cacheable(value = "dtos_combustibles")
	public List<CombustibleDto> getCombustiblesDto() {
		return combustibleQueryService.findAllCombustiblesCached()
				.stream()
				.map(CombustibleDto::from)
				.toList();
	}

	@Cacheable(value = "dtos_grupo_combustibles")
	public List<GrupoCombustibleDto> getGruposDeCombustibles() {
		return combustibleQueryService.findAllGruposDeCombustiblesCached().stream()
				.map(GrupoCombustibleDto::from).toList();
	}

	public List<GrupoCombustibleDto> getGruposDeCombustiblesConCombustibles() {
		// Nota - el warning de saltarse la caché de los métodos nos da igual por la caché de query.
		Map<Short, List<CombustibleDto>> combustiblesPorGrupo = getCombustiblesDto().stream()
				.filter(c -> c.id_grupo_combustible() != null)
				.collect(Collectors.groupingBy(CombustibleDto::id_grupo_combustible));

		return getGruposDeCombustibles().stream().
						map(gc -> gc.withCombustibles(
						combustiblesPorGrupo.getOrDefault(gc.id(), Collections.emptyList())))
				.toList();
	}


}