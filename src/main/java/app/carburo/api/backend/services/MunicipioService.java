package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.MunicipioDto;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.MunicipioRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MunicipioService {

	private final MunicipioRepository municipioRepository;
	private final ProvinciaRepository provinciaRepository;

	public MunicipioService(MunicipioRepository municipioRepository,
							ProvinciaRepository provinciaRepository) {
		this.municipioRepository = municipioRepository;
		this.provinciaRepository = provinciaRepository;
	}

	public List<MunicipioDto> getMunicipiosDTO() {
		List<MunicipioDto> municipios = new ArrayList<>();
		municipioRepository.findAll()
				.forEach(municipio -> municipios.add(MunicipioDto.from(municipio)));
		return municipios;
	}

	public List<MunicipioDto> getMunicipiosDTOByProvincia(short idProvincia) {
		if (!provinciaRepository.existsById(idProvincia))
			throw new ResourceNotFoundException(
					"Provincia no encontrada con id: " + idProvincia);

		return municipioRepository.findMunicipioByProvincia(idProvincia).stream()
				.map(MunicipioDto::from).toList();
	}

	public List<MunicipioDto> getMunicipiosDTOByProvinciaConEESS(short idProvincia) {
		if (!provinciaRepository.existsById(idProvincia))
			throw new ResourceNotFoundException(
					"Provincia no encontrada con id: " + idProvincia);

		return municipioRepository.findMunicipioByProvinciaConEESS(idProvincia).stream()
				.map(MunicipioDto::from).toList();
	}

}
