package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.dto.PrecioCombustibleDto;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.PrecioCombustible;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.*;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar estaciones de servicio.
 * Permite obtener estaciones, filtrarlas por ubicación, combustible, estado, marca y ordenarlas.
 */
@Service
public class EstacionDeServicioService {

	private static final int MAX_DIAS = 30;

	private final EstacionDeServicioRepository estacionDeServicioRepository;
	private final PrecioCombustibleRepository precioCombustibleRepository;
	private final ComunidadAutonomaRepoository comunidadAutonomaRepository;
	private final ProvinciaRepository provinciaRepository;
	private final MunicipioRepository municipioRepository;

	/**
	 * Inyección de dependencias de los servicios.
	 */

	public EstacionDeServicioService(
			EstacionDeServicioRepository estacionDeServicioRepository,
			PrecioCombustibleRepository precioCombustibleRepository,
			ComunidadAutonomaRepoository comunidadAutonomaRepository,
			ProvinciaRepository provinciaRepository,
			MunicipioRepository municipioRepository) {
		this.estacionDeServicioRepository = estacionDeServicioRepository;
		this.precioCombustibleRepository = precioCombustibleRepository;
		this.comunidadAutonomaRepository  = comunidadAutonomaRepository;
		this.provinciaRepository          = provinciaRepository;
		this.municipioRepository          = municipioRepository;
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
		if (!estacionDeServicioRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Estación de servicio no encontrada con id: " + id);
		return EstacionDeServicioDto.from(
				estacionDeServicioRepository.findEstacionDeServicioById(id));
	}

	public EstacionDeServicioDto getEstacionDeServicioDtoById(int id, double latitud,
															  double longitud) {
		if (!estacionDeServicioRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Estación de servicio no encontrada con id: " + id);
		EstacionDeServicio es = estacionDeServicioRepository.findEstacionDeServicioById(
				id);
		Long d = estacionDeServicioRepository.findDistanciaById(es.getId(), latitud,
																longitud);
		return EstacionDeServicioDto.from(es, d);
	}

	public List<PrecioCombustibleDto> getPreciosDeCombustiblesDtoByEstacionDeServicioId(
			int id, int dias) throws BadRequestException {
		if (!estacionDeServicioRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Estación de servicio no encontrada con id: " + id);

		if (dias <= 0)
			throw new BadRequestException("El parámetro 'dias' debe ser mayor que 0");

		if (dias > MAX_DIAS) dias = MAX_DIAS;

		LocalDate hoy = LocalDate.now();
		LocalDate fechaInicio = hoy.minusDays(Integer.valueOf(dias - 1).longValue());

		return precioCombustibleRepository.findByEstacion_IdAndId_FechaBetween(id,
																			   fechaInicio,
																			   hoy)
				.stream().map(PrecioCombustibleDto::from).toList();
	}

	public List<PrecioCombustibleDto> getPreciosDeCombustiblesDtoByEstacionDeServicioIdAndFecha(
			int id, LocalDate fecha) throws BadRequestException {
		if (!estacionDeServicioRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Estación de servicio no encontrada con id: " + id);

		LocalDate hoy = LocalDate.now();

		if (fecha.isAfter(hoy))
			throw new BadRequestException("La fecha no puede ser futura");

		if (fecha.isBefore(PrecioCombustible.FECHA_MINIMA)) throw new BadRequestException(
				"La fecha mínima permitida es " + PrecioCombustible.FECHA_MINIMA);

		return precioCombustibleRepository.findByEstacion_IdAndId_Fecha(id, fecha)
				.stream().map(PrecioCombustibleDto::from).toList();
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


		return estacionDeServicioRepository.findEstacionDeServicioMasCercana(lat, lon,
																			 limit)
				.stream().map(eess -> {
					Long distancia = estacionDeServicioRepository.findDistanciaById(
							eess.getId(), lat, lon);
					return EstacionDeServicioDto.from(eess, distancia);
				}).toList();
	}
}
