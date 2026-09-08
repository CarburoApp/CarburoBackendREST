package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.MunicipioDto;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.MunicipioRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import org.springframework.cache.annotation.Cacheable;
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

	@Cacheable(value = "municipios")
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

	@Cacheable(value = "municipiosConEESS", key = "#idProvincia")
	public List<MunicipioDto> getMunicipiosDTOByProvinciaConEESS(short idProvincia) {
		if (!provinciaRepository.existsById(idProvincia))
			throw new ResourceNotFoundException(
					"Provincia no encontrada con id: " + idProvincia);

		return municipioRepository.findMunicipioByProvinciaConEESS(idProvincia).stream()
				.map(MunicipioDto::from).toList();
	}

	/**
	 * Obtiene todos los municipios de una comunidad autónoma que
	 * dispongan de estaciones de servicio.
	 *
	 * @param idComunidadAutonoma ID de la comunidad autónoma
	 * @return Lista de municipios con estaciones de servicio
	 */
	@Cacheable(value = "municipiosConEESSPorComunidadAutonoma",
			   key = "#idComunidadAutonoma")
	public List<MunicipioDto> getMunicipiosDTOByComunidadAutonomaConEESS(
			short idComunidadAutonoma) {

		return municipioRepository
				.findMunicipioByComunidadAutonomaConEESS(idComunidadAutonoma)
				.stream()
				.map(MunicipioDto::from)
				.toList();
	}

}
