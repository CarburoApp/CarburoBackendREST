package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.services.queryServices.CombustibleQueryService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CombustibleService {

	private final CombustibleQueryService combustibleQueryService;

	public CombustibleService(CombustibleQueryService combustibleQueryService) {
		this.combustibleQueryService = combustibleQueryService;
	}

	@Cacheable(value = "combustibles")
	public List<CombustibleDto> getCombustiblesDto() {
		return combustibleQueryService.findAllCached()
				.stream()
				.map(CombustibleDto::from)
				.toList();
	}
}