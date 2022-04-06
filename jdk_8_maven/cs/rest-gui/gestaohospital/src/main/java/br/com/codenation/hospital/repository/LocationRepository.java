package br.com.codenation.hospital.repository;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.Product;

import java.util.List;

import org.springframework.data.geo.Box;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends MongoRepository<Location, String> {
	
	List<Location> findByNameAndLocationNear(String sid, Point p, Distance d);
	
	List<Location> findByNameLikeIgnoreCase(String subject);
	
	List<Location> findByPositionNear(Point p, Distance d);


}
