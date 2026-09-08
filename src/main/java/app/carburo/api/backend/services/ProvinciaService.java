package app.carburo.api.backend.services;

import app.carburo.api.backend.dto.ProvinciaDto;
import app.carburo.api.backend.entities.ComunidadAutonoma;
import app.carburo.api.backend.entities.Provincia;
import app.carburo.api.backend.exceptions.ResourceNotFoundException;
import app.carburo.api.backend.repositories.ComunidadAutonomaRepository;
import app.carburo.api.backend.repositories.ProvinciaRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Servicio encargado de la gestión de provincias.
 * <p>
 * Proporciona métodos de acceso a los datos relacionados con la entidad
 * {@link Provincia}, encapsulando la lógica de acceso al repositorio.
 * </p>
 */
@Service
public class ProvinciaService {

	/**
	 * Repositorio de acceso a datos de provincias.
	 */
	private final ProvinciaRepository provinciaRepository;
	private final ComunidadAutonomaRepository comunidadAutonomaRepository;

	/**
	 * Constructor del servicio con inyección de dependencias.
	 *
	 * @param provinciaRepository repositorio de provincias
	 * @param comunidadAutonomaRepository repositorio de comunidades autónomas
	 */
	public ProvinciaService(ProvinciaRepository provinciaRepository,
							ComunidadAutonomaRepository comunidadAutonomaRepository) {
		this.provinciaRepository = provinciaRepository;
		this.comunidadAutonomaRepository = comunidadAutonomaRepository;
	}

	@Cacheable(value = "provincias")
	public List<ProvinciaDto> getProvinciasDTO() {
		List<ProvinciaDto> provincias = new ArrayList<>();
		provinciaRepository.findAll()
				.forEach(provincia -> provincias.add(ProvinciaDto.from(provincia)));
		return provincias;
	}

	/**
	 * Obtiene todas las provincias ordenadas alfabéticamente por su denominación.
	 *
	 * @return lista de provincias ordenadas por denominación;
	 *         si no existen provincias, se devuelve una lista vacía
	 */
	public List<Provincia> getProvinciasOrderByDenominacion() {
		List<Provincia> provincias = new ArrayList<>();
		provinciaRepository.findAllOrderByDenominacion()
				.forEach(provincias::add);
		return provincias;
	}

	public List<ProvinciaDto> getProvinciasDTOByComunidadAutonoma(
			short idComunidadAutonoma) {
		if (!comunidadAutonomaRepository.existsById(idComunidadAutonoma))
			throw new ResourceNotFoundException(
					"Comunidad Autónoma no encontrada con id: " + idComunidadAutonoma);

		return provinciaRepository
				.findProvinciaByComunidadAutonoma(idComunidadAutonoma)
				.stream()
				.map(ProvinciaDto::from)
				.toList();
	}

	/**
	 * Obtiene todas las provincias pertenecientes a una comunidad autónoma concreta.
	 *
	 * @param comunidadAutonoma comunidad autónoma por la que se desea filtrar
	 * @return lista de provincias asociadas a la comunidad autónoma indicada;
	 *         si no existen coincidencias, se devuelve una lista vacía
	 * @throws IllegalArgumentException si {@code comunidadAutonoma} es {@code null}
	 */
	public List<Provincia> getProvinciasByComunidadAutonoma(
			ComunidadAutonoma comunidadAutonoma) {

		if (comunidadAutonoma == null) {
			throw new IllegalArgumentException("La comunidad autónoma no puede ser null.");
		}

		List<Provincia> provincias = new ArrayList<>();
		provinciaRepository.findAllByComunidadAutonoma(comunidadAutonoma)
				.forEach(provincias::add);
		return provincias;
	}

	/**
	 * Obtiene el número total de provincias registradas en el sistema.
	 *
	 * @return número total de provincias
	 */
	public long getTotalProvincias() {
		return provinciaRepository.count();
	}

	/**
	 * Busca una provincia por su identificador.
	 *
	 * @param provinciaFavorita identificador de la provincia
	 * @return {@link Optional} que contiene la provincia si existe;
	 *         {@link Optional#empty()} en caso contrario
	 */
	public Optional<Provincia> getProvinciaById(short provinciaFavorita) {
		return provinciaRepository.findProvinciaById(provinciaFavorita);
	}

	public boolean existsById(short id) {
		return provinciaRepository.existsById(id);
	}
}
