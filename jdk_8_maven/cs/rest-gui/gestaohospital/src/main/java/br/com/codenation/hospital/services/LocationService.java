package br.com.codenation.hospital.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.LocationBuilder;
import br.com.codenation.hospital.domain.LocationCategory;
import br.com.codenation.hospital.dto.HospitalDTO;
import br.com.codenation.hospital.dto.LocationDTO;
import br.com.codenation.hospital.integration.LocationIQResponse;
import br.com.codenation.hospital.integration.LocationIQService;
import br.com.codenation.hospital.repository.HospitalRepository;
import br.com.codenation.hospital.repository.LocationRepository;
import br.com.codenation.hospital.services.exception.ObjectNotFoundException;

@Service
public class LocationService {
	
	@Autowired
	private  LocationRepository locationRepository;
	
	@Autowired
	private  HospitalRepository hospitalRepository;
	
	@Autowired
	private  HospitalService hospitalService;
	
	@Autowired
	private LocationIQService locationIQService;
	
	public List<LocationDTO> findByNameAndLocationNear(String name, String longitude, String latitude, Double distance) {
		return  convertToDTOs(locationRepository.findByNameAndLocationNear(name, 
				new Point(Double.valueOf(longitude), Double.valueOf(latitude)), 
				new Distance(distance, Metrics.KILOMETERS)));
	}
	
	public List<LocationDTO> findByPositionNear(String longitude, String latitude, Double distance) {
		return  convertToDTOs(locationRepository.findByPositionNear(
				new Point(Double.valueOf(longitude), Double.valueOf(latitude)), 
				new Distance(distance, Metrics.KILOMETERS)));
	}
	
	public List<LocationDTO> findLocationNearHospitalBy(String id) {
		Hospital hospital = hospitalService.findById(id);
		

		Double longitude = hospital.getLocation().getPosition().getX();
		Double latitude  = hospital.getLocation().getPosition().getY();
		Double distance  = 100.0d;
		
		List<Location> locations = locationRepository.findByPositionNear(
				new Point(longitude, latitude),
				new Distance(distance, Metrics.KILOMETERS));
		
		List<Location> filterLocations = locations.stream()
				.filter(f -> !f.getReferenceId().equals(id))
				.collect(Collectors.toList());
				
		return  convertToDTOs(filterLocations);
	}
	
	// Usar para encontrar hospital perto de hospital
	public List<HospitalDTO> findHospitalNearHospitalBy(String id, Double raio) {
		Hospital hospital = hospitalService.findById(id);

		Double longitude = hospital.getLocation().getPosition().getX();
		Double latitude  = hospital.getLocation().getPosition().getY();

		List<Location> locations = locationRepository.findByPositionNear(
				new Point(longitude, latitude),
				new Distance(raio != null ? raio : 10000, Metrics.KILOMETERS));
		
		List<Location> filterLocations = locations.stream()
				.filter(f -> f.getLocationCategory() == LocationCategory.HOSPITAL && !f.getName().equals(hospital.getName()))
				.collect(Collectors.toList());
				
		List<HospitalDTO> hospitaisDTO = new ArrayList<HospitalDTO>();
		
		for (Location location : filterLocations) {
			Hospital hosp = hospitalRepository.findByNameLikeIgnoreCase(location.getName())
					.stream()
					.findFirst()
					.orElse(null);
			
			if (hosp != null) {
				hospitaisDTO.add(hospitalService.convertToDTO(hosp));
			}
		}
		
		return  hospitaisDTO;
	}
	
	// Usar para encontrar hospital perto de paciente, enviar endereço do paciente
	public List<HospitalDTO> findHospitalNearLocationBy(Double longitude, Double latitude, Double distance) {
		if(distance == null)
			distance  = 100.0d;
		
		List<Location> locations = locationRepository.findByPositionNear(
				new Point(longitude, latitude),
				new Distance(distance, Metrics.KILOMETERS));
		
		List<Location> filterLocations = locations.stream()
				.filter(f -> f.getLocationCategory() == LocationCategory.HOSPITAL)
				.collect(Collectors.toList());
				
		List<HospitalDTO> hospitaisDTO = new ArrayList<HospitalDTO>();
		
		for (Location location : filterLocations) {
			Hospital hospital = hospitalRepository.findByNameLikeIgnoreCase(location.getName())
					.stream()
					.findFirst()
					.orElse(null);
			
			if (hospital != null) {
				hospitaisDTO.add(hospitalService.convertToDTO(hospital));
			}
		}
		
		return  hospitaisDTO;
	}
	
	public List<LocationDTO> findAll(){
		return convertToDTOs(locationRepository.findAll());
	}

	public LocationDTO findById(String id) {
		return convertToDTO(findLocationById(id));
    }
		
	public List<LocationDTO> findByNameLikeIgnoreCase(String subject) {
		List<Location> locations = locationRepository.findByNameLikeIgnoreCase(subject);
		return convertToDTOs(locations);
    }

	public LocationDTO insert(LocationDTO locationDTO) {
		Location location = fromDTO(locationDTO);
		return convertToDTO(locationRepository.save(location));
	}
	
	public void delete(String id) {
	    locationRepository.deleteById(id);
	}
	
	public LocationDTO update(String id, LocationDTO locationDTO) {
		Location updateLocation = findLocationById(id);
		Location locationData = fromDTO(locationDTO);
		updateLocation.setName(locationData.getName());
		updateLocation.setPosition(locationData.getPosition());
		updateLocation.setLocationCategory(LocationCategory.valueOf(locationDTO.getCategory()));
		return convertToDTO(locationRepository.save(updateLocation));
	}
	
	private Location findLocationById(String id) {
        Optional<Location> result = locationRepository.findById(id);
		return result.orElseThrow(() -> new ObjectNotFoundException("Location não encontrado! ID: "+ id));
    }
	
	public Location fromDTO(LocationDTO locationDTO) {
		GeoJsonPoint locationPoint = new GeoJsonPoint(
		        Double.valueOf(locationDTO.getLatitude()),
		        Double.valueOf(locationDTO.getLongitude()));

		return new Location(LocationCategory.valueOf(locationDTO.getCategory()), locationDTO.getName(), locationPoint);
	}
	
	private LocationDTO convertToDTO(Location model) {
        return new LocationDTO(model);
    }
	
	private List<LocationDTO> convertToDTOs(List<Location> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }	
	
	
	public Location insertLocationByHospital(Hospital hospital) {
		Location locationHospital = new Location();
		
		List<LocationIQResponse> locationsResponse = locationIQService.getLocationIQResponse(hospital.getAddress());
		
		LocationIQResponse locationResponse = new LocationIQResponse();
		if (!locationsResponse.isEmpty()) {
			locationResponse = locationsResponse.get(0);
			
			locationHospital = new LocationBuilder()
					.setReferenceId(hospital.getAddress())
					.setLocationCategory(LocationCategory.HOSPITAL)
					.setName(hospital.getName())
					.setLatitude(Double.valueOf(locationResponse.getLat()))
					.setLongitude(Double.valueOf(locationResponse.getLon()))
					.build();
		} else {
			locationHospital = new LocationBuilder()
					.setReferenceId(hospital.getAddress())
					.setLocationCategory(LocationCategory.HOSPITAL)
					.setName(hospital.getName())
					.setLatitude(0D)
					.setLongitude(0D)
					.build();
		}
		
		locationRepository.save(locationHospital);
		
		return locationHospital;
	}
}
