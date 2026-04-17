package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.ComunidadAutonomaRepoository;
import app.carburo.api.backend.repositories.EstacionDeServicioRepository;
import app.carburo.api.backend.repositories.MunicipioRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar estaciones de servicio.
 * Permite obtener estaciones, filtrarlas por ubicación, combustible, estado, marca y ordenarlas.
 */
@Service
public class EstacionDeServicioService {

	private final EstacionDeServicioRepository estacionDeServicioRepository;
	private final CombustibleService combustibleService;
	private final ComunidadAutonomaRepoository comunidadAutonomaRepository;
	private final ProvinciaRepository provinciaRepository;
	private final MunicipioRepository municipioRepository;

	/**
	 * Inyección de dependencias de los servicios.
	 */

	public EstacionDeServicioService(
			EstacionDeServicioRepository estacionDeServicioRepository,
			ComunidadAutonomaRepoository comunidadAutonomaRepository,
			ProvinciaRepository provinciaRepository,
			MunicipioRepository municipioRepository,
			CombustibleService combustibleService) {
		this.estacionDeServicioRepository = estacionDeServicioRepository;
		this.comunidadAutonomaRepository  = comunidadAutonomaRepository;
		this.provinciaRepository          = provinciaRepository;
		this.municipioRepository          = municipioRepository;
		this.combustibleService           = combustibleService;
	}

	public List<EstacionDeServicioDto> getEstacionesDeServicioDto() {
		List<EstacionDeServicioDto> estaciones = new ArrayList<>();
		estacionDeServicioRepository.findAll()
				.forEach(eess -> estaciones.add(EstacionDeServicioDto.from(eess)));
		return estaciones;
	}

	/**
	 * Devuelve una estación de servicio según su ID
	 */
	public EstacionDeServicioDto getEstacionDeServicioDtoById(int id) {
		return EstacionDeServicioDto.from(
				estacionDeServicioRepository.findEstacionDeServicioById(id));
	}

	/**
	 * Obtiene la estación de servicio más cercana a una ubicación dada.
	 *
	 * <p>La búsqueda se realiza utilizando coordenadas geográficas
	 * en sistema WGS84 (latitud y longitud). El cálculo de proximidad
	 * se delega al repositorio, que hace uso de funciones espaciales
	 * de PostGIS.</p>
	 *
	 * @param latitud  latitud del punto de referencia (WGS84)
	 * @param longitud longitud del punto de referencia (WGS84)
	 * @return la estación de servicio más cercana a las coordenadas indicadas,
	 *         o {@code null} si no existe ninguna
	 */
	public EstacionDeServicioDto getEstacionDeServicioMasProxima(double latitud,
																 double longitud) {
		return EstacionDeServicioDto.from(
				estacionDeServicioRepository.findEstacionDeServicioMasCercana(latitud,
																			  longitud));
	}

	/**
	 * Devuelve estaciones por comunidad autónoma (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByComunidadAutonoma(
			short id) {
		if (!comunidadAutonomaRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Comunidad autónoma no encontrada con id: " + id);


		return estacionDeServicioRepository.findEstacionDeServicioByComunidadAutonoma(id)
				.stream().map(EstacionDeServicioDto::from).toList();
	}


	/**
	 * Devuelve estaciones por provincia (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByProvincia(short id) {
		if (!provinciaRepository.existsById(id))
			throw new ResourceNotFoundException("Provincia no encontrada con id: " + id);

		return estacionDeServicioRepository.findEstacionDeServicioByProvincia(id).stream()
				.map(EstacionDeServicioDto::from).toList();
	}


	/**
	 * Devuelve estaciones por municipio (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByMunicipio(short id) {
		if (!municipioRepository.existsById(id))
			throw new ResourceNotFoundException("Municipio no encontrado con id: " + id);

		return estacionDeServicioRepository.findEstacionDeServicioByMunicipio(id).stream()
				.map(EstacionDeServicioDto::from).toList();
	}

	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoCercanas(double lat,
																		  double lon,
																		  int limit) {
		if (Double.isNaN(lat) || Double.isNaN(lon))
			throw new IllegalArgumentException("Latitud y longitud son obligatorias");
		if (lat < -90 || lat > 90) throw new IllegalArgumentException(
				"Latitud fuera de rango válido (-90 a 90)");
		if (lon < -180 || lon > 180) throw new IllegalArgumentException(
				"Longitud fuera de rango válido (-180 a 180)");
		if (limit <= 0) limit = 1;
		if (limit > 10) limit = 10;

		List<EstacionDeServicio> estaciones = estacionDeServicioRepository.findEstacionDeServicioMasCercana(
				lat, lon, limit);
		return estaciones.stream().map(EstacionDeServicioDto::from).toList();
	}
}
