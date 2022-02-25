package br.com.codenation.hospital.resource;

import java.util.List;
import java.util.Optional;import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.LocationCategory;
import br.com.codenation.hospital.dto.HospitalDTO;
import br.com.codenation.hospital.dto.LocationDTO;
import br.com.codenation.hospital.dto.ProductDTO;
import br.com.codenation.hospital.repository.LocationRepository;
import br.com.codenation.hospital.services.HospitalService;
import br.com.codenation.hospital.services.LocationService;

@CrossOrigin("http://localhost:4200") // permissão para o Angular
@RestController
@RequestMapping(path = Constant.V1Path)
public class LocationResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationResource.class);
	 @Autowired
	 private LocationService locationService;
	 
	 @GetMapping(path = "/proximidades")
	 public ResponseEntity<List<LocationDTO>> findLocationNearHospitalBy(@PathVariable String hospital_id) {
		try {
			List<LocationDTO> locations = locationService.findLocationNearHospitalBy(hospital_id);

			return Optional.ofNullable(locations).map(productReponse -> ResponseEntity.ok().body(productReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("findLocationNearHospitalBy - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	 }
	  
	 @GetMapping(path = "/hospitaisProximos")
	 public ResponseEntity<List<HospitalDTO>> findHospitalNearHospitalBy(@PathVariable String hospital_id, @RequestParam Double raio) {
		try {
			List<HospitalDTO> locations = locationService.findHospitalNearHospitalBy(hospital_id, raio);

			return Optional.ofNullable(locations).map(productReponse -> ResponseEntity.ok().body(productReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("findHospitalNearHospitalBy - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	 }
}
