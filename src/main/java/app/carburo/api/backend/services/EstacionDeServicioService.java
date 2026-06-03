package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.CombustibleDto;
import app.carburo.api.backend.dto.EstacionDeServicioDto;
import app.carburo.api.backend.dto.EstadisticaCombustibleDto;
import app.carburo.api.backend.dto.PrecioCombustibleDto;
import app.carburo.api.backend.entities.EstacionDeServicio;
import app.carburo.api.backend.entities.PrecioCombustible;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.*;
import org.apache.coyote.BadRequestException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Servicio para gestionar estaciones de servicio.
 * Permite obtener estaciones, filtrarlas por ubicación, combustible, estado, marca y ordenarlas.
 */
@Service
public class EstacionDeServicioService {

	private static final int MAX_DIAS = 30;
	private static final int MAX_ESTACIONES_CERCANAS = 10;

	private final EstacionDeServicioRepository estacionDeServicioRepository;
	private final PrecioCombustibleRepository precioCombustibleRepository;
	private final ComunidadAutonomaRepoository comunidadAutonomaRepository;
	private final ProvinciaRepository provinciaRepository;
	private final MunicipioRepository municipioRepository;
	private final PrecioStateService precioStateService;

	/**
	 * Inyección de dependencias de los servicios.
	 */

	public EstacionDeServicioService(
			EstacionDeServicioRepository estacionDeServicioRepository,
			PrecioCombustibleRepository precioCombustibleRepository,
			ComunidadAutonomaRepoository comunidadAutonomaRepository,
			ProvinciaRepository provinciaRepository,
			MunicipioRepository municipioRepository,
			PrecioStateService precioStateService) {
		this.estacionDeServicioRepository = estacionDeServicioRepository;
		this.precioCombustibleRepository = precioCombustibleRepository;
		this.comunidadAutonomaRepository  = comunidadAutonomaRepository;
		this.provinciaRepository          = provinciaRepository;
		this.municipioRepository          = municipioRepository;
		this.precioStateService = precioStateService;
	}

	@Cacheable(value = "eess_count")
	public long getTotalEstacionesDeServicio() {
		return estacionDeServicioRepository.count();
	}

	@Cacheable(value = "combustibles_analiticas_hoy", key = "#fecha ?: 'hoy'")
	public List<EstadisticaCombustibleDto> getEstadisticasCombustibles(LocalDate fecha)
			throws BadRequestException {
		LocalDate fechaTarget = (fecha == null) ? precioStateService.getBestDate() : fecha;

		if (fechaTarget.isAfter(LocalDate.now())) throw new BadRequestException(
				"No se pueden consultar analíticas de una fecha futura.");
		if (fechaTarget.isBefore(PrecioCombustible.FECHA_MINIMA))
			throw new BadRequestException("La fecha mínima permitida para consulta es " +
												  PrecioCombustible.FECHA_MINIMA);

		List<Object[]> rows = precioCombustibleRepository.findRawEstadisticasGlobalesPorFecha(
				fechaTarget);

		return rows.stream().map(row -> {
			// Reconstruimos el CombustibleDto original usando los primeros 4 elementos de la fila
			CombustibleDto combustibleDto = new CombustibleDto((short) row[0],
															   // id
															   (String) row[1],
															   // denominacion
															   (String) row[2],
															   // codigo
															   (Short) row[3]
															   // id_grupo_combustible
			);

			// Devolvemos el DTO completo con su objeto compuesto
			return new EstadisticaCombustibleDto(combustibleDto, (double) row[4],
												 // precioMedio
												 (double) row[5],        // precioMaximo
												 (double) row[6],        // precioMinimo
												 (long) row[7]
												 // totalEstaciones
			);
		}).toList();
	}

	public List<EstacionDeServicioDto> getEstacionesDeServicioDto() {
		return estacionDeServicioRepository.findAll().stream()
				.map(EstacionDeServicioDto::from).toList();
	}

	/**
	 * Devuelve una estación de servicio según su ID
	 */
	public EstacionDeServicioDto getEstacionDeServicioDtoById(int id) {
		existsOrThrow(id);
		return mapToDtoConPreciosHoy(
				estacionDeServicioRepository.findEstacionDeServicioById(id), null);
	}

	public EstacionDeServicio getEstacionDeServicioById(int id) {
		return estacionDeServicioRepository.findEstacionDeServicioById(id);
	}


	public EstacionDeServicioDto getEstacionDeServicioDtoById(int id, double latitud,
															  double longitud) {
		existsOrThrow(id);

		EstacionDeServicio es = estacionDeServicioRepository.findEstacionDeServicioById(
				id);
		Long d = estacionDeServicioRepository.findDistanciaById(es.getId(), latitud,
																longitud);
		return mapToDtoConPreciosHoy(es, d);
	}

	/**
	 * Obtiene los datos de múltiples Estaciones de Servicio por sus IDs sin incluir precios.
	 *
	 * @param ids Lista de IDs de las estaciones de servicio
	 * @return Lista de Estaciones de Servicio en formato DTO sin precios
	 * @throws BadRequestException si la lista de IDs está vacía o supera el límite
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByIdsSinPrecios(
			List<Integer> ids) throws BadRequestException {
		if (ids == null || ids.isEmpty()) throw new BadRequestException(
				"Debe proporcionar al menos un ID de estación de servicio.");

		if (ids.size() > 100) throw new BadRequestException(
				"No se permite la consulta de más de 100 estaciones simultáneamente.");

		return estacionDeServicioRepository.findAllById(ids).stream()
				.map(EstacionDeServicioDto::from).toList();
	}

	/**
	 * Obtiene los datos de múltiples Estaciones de Servicio por sus IDs inyectando el cálculo
	 * de la distancia desde un punto geográfico dado, sin incluir precios.
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByIdsConDistanciaYSinPrecios(
			List<Integer> ids, double latitud, double longitud)
			throws BadRequestException {
		if (ids == null || ids.isEmpty()) throw new BadRequestException(
				"Debe proporcionar al menos un ID de estación de servicio.");

		if (ids.size() > 100) throw new BadRequestException(
				"No se permite la consulta de más de 100 estaciones simultáneamente.");

		validarCoordenadas(latitud, longitud);

		return estacionDeServicioRepository.findAllById(ids).stream().map(eess -> {
			Long distancia = estacionDeServicioRepository.findDistanciaById(eess.getId(),
																			latitud,
																			longitud);
			return EstacionDeServicioDto.from(eess, distancia);
		}).toList();
	}

	/**
	 * Devuelve estaciones por comunidad autónoma (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByComunidadAutonoma(
			short id) {
		if (!comunidadAutonomaRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Comunidad autónoma no encontrada con id: " + id);

		return mapToDtoConPreciosHoy(
				estacionDeServicioRepository.findEstacionDeServicioByComunidadAutonoma(
						id));
	}


	/**
	 * Devuelve estaciones por provincia (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByProvincia(short id) {
		if (!provinciaRepository.existsById(id))
			throw new ResourceNotFoundException("Provincia no encontrada con id: " + id);

		return mapToDtoConPreciosHoy(
				estacionDeServicioRepository.findEstacionDeServicioByProvincia(id));
	}


	/**
	 * Devuelve estaciones por municipio (DTO)
	 */
	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoByMunicipio(short id) {
		if (!municipioRepository.existsById(id))
			throw new ResourceNotFoundException("Municipio no encontrado con id: " + id);

		return mapToDtoConPreciosHoy(
				estacionDeServicioRepository.findEstacionDeServicioByMunicipio(id));
	}

	public List<EstacionDeServicioDto> getEstacionesDeServicioDtoCercanas(double lat,
																		  double lon,
																		  int limit)
			throws BadRequestException {
		validarCoordenadas(lat, lon);
		if (limit <= 0) limit = 1;
		if (limit > MAX_ESTACIONES_CERCANAS) limit = MAX_ESTACIONES_CERCANAS;


		return estacionDeServicioRepository.findEstacionDeServicioMasCercana(lat, lon,
																			 limit)
				.stream().map(eess -> {
					Long distancia = estacionDeServicioRepository.findDistanciaById(
							eess.getId(), lat, lon);
					List<PrecioCombustibleDto> precios = precioCombustibleRepository.findByEstacion_IdAndId_Fecha(
									eess.getId(), precioStateService.getBestDate()).stream()
							.map(PrecioCombustibleDto::from).toList();
					return EstacionDeServicioDto.from(eess, distancia, precios);
				}).toList();
	}

	public List<PrecioCombustibleDto> getPreciosDeCombustiblesDtoByEstacionDeServicioId(
			int id, int dias) throws BadRequestException {
		existsOrThrow(id);

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

	public List<PrecioCombustibleDto> getPreciosDeCombustiblesDtoByEstacionesIds(
			List<Integer> ids) throws BadRequestException {
		if (ids == null || ids.isEmpty()) throw new BadRequestException(
				"Debe proporcionar al menos un ID de estación de servicio.");

		// Control de rendimiento (Opcional, ajustable)
		if (ids.size() > 100) throw new BadRequestException(
				"No se permite la consulta de más de 100 estaciones simultáneamente.");

		return precioCombustibleRepository.findPreciosHoyByListadoIdEstaciones(ids,
																			   precioStateService.getBestDate())
				.stream().map(PrecioCombustibleDto::from).toList();
	}


	public List<PrecioCombustibleDto> getPreciosDeCombustiblesDtoByEstacionDeServicioIdAndFecha(
			int id, LocalDate fecha) throws BadRequestException {
		existsOrThrow(id);

		LocalDate hoy = LocalDate.now();

		if (fecha.isAfter(hoy))
			throw new BadRequestException("La fecha no puede ser futura");

		if (fecha.isBefore(PrecioCombustible.FECHA_MINIMA)) throw new BadRequestException(
				"La fecha mínima permitida es " + PrecioCombustible.FECHA_MINIMA);

		return precioCombustibleRepository.findByEstacion_IdAndId_Fecha(id, fecha)
				.stream().map(PrecioCombustibleDto::from).toList();
	}

	private List<EstacionDeServicioDto> mapToDtoConPreciosHoy(
			List<EstacionDeServicio> estaciones) {
		if (estaciones.isEmpty()) return List.of();

		List<Integer> idsEess;
		List<PrecioCombustibleDto> precioCombustibleDtos;
		Map<Integer, List<PrecioCombustibleDto>> preciosPorEstacion;

		idsEess = estaciones.stream().map(EstacionDeServicio::getId).toList();

		precioCombustibleDtos = precioCombustibleRepository.findPreciosHoyByListadoIdEstaciones(
						idsEess, precioStateService.getBestDate()).stream().map(PrecioCombustibleDto::from)
				.toList();
		preciosPorEstacion    = precioCombustibleDtos.stream().collect(
				Collectors.groupingBy(PrecioCombustibleDto::id_estacion_de_servicio));

		return estaciones.stream().map(e -> EstacionDeServicioDto.from(e, null,
																	   preciosPorEstacion.getOrDefault(
																			   e.getId(),
																			   List.of())))
				.toList();
	}

	private EstacionDeServicioDto mapToDtoConPreciosHoy(EstacionDeServicio estacion,
														Long distancia) {

		List<PrecioCombustibleDto> precios = precioCombustibleRepository.findPreciosHoyByListadoIdEstaciones(
						List.of(estacion.getId()), precioStateService.getBestDate()).stream()
				.map(PrecioCombustibleDto::from).toList();

		return EstacionDeServicioDto.from(estacion, distancia, precios);
	}

	/**
	 * Método privado utilitario para validar rangos geográficos.
	 */
	private void validarCoordenadas(double lat, double lon) throws BadRequestException {
		if (Double.isNaN(lat) || Double.isNaN(lon))
			throw new BadRequestException("Latitud y longitud son obligatorias");
		if (lat < -90 || lat > 90)
			throw new BadRequestException("Latitud fuera de rango válido (-90 a 90)");
		if (lon < -180 || lon > 180)
			throw new BadRequestException("Longitud fuera de rango válido (-180 a 180)");
	}

	public void existsOrThrow(Integer id) {
		if (!estacionDeServicioRepository.existsById(id))
			throw new ResourceNotFoundException(
					"Estación de servicio no encontrada con id: " + id);
	}

}
