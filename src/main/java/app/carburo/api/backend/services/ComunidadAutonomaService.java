package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.ComunidadAutonomaDto;
import app.carburo.api.backend.entities.ComunidadAutonoma;
import app.carburo.api.backend.repositories.ComunidadAutonomaRepoository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ComunidadAutonomaService {

	private final ComunidadAutonomaRepoository comunidadAutonomaRepository;

	public ComunidadAutonomaService(
			ComunidadAutonomaRepoository comunidadAutonomaRepository) {
		this.comunidadAutonomaRepository = comunidadAutonomaRepository;
	}

	public List<ComunidadAutonoma> getComunidadesAutonomas() {
		List<ComunidadAutonoma> ccaa = new ArrayList<>();
		comunidadAutonomaRepository.findAll().forEach(ccaa::add);
		return ccaa;
	}

	public List<ComunidadAutonomaDto> getComunidadesAutonomasDTO() {
		List<ComunidadAutonomaDto> ccaa = new ArrayList<>();
		comunidadAutonomaRepository.findAll()
				.forEach(ca -> ccaa.add(ComunidadAutonomaDto.from(ca)));
		return ccaa;
	}

	public List<ComunidadAutonomaDto> getComunidadesAutonomasDto() {
		List<ComunidadAutonomaDto> ccaa = new ArrayList<>();
		comunidadAutonomaRepository.findAll()
				.forEach(ca -> ccaa.add(ComunidadAutonomaDto.from(ca)));
		return ccaa;
	}

	public Optional<ComunidadAutonoma> getComunidadAutonomaById(short idCCAA) {
		return comunidadAutonomaRepository.findComunidadAutonomaById(idCCAA);
	}

	public boolean existsById(short id) {
		return comunidadAutonomaRepository.existsById(id);
	}

}
