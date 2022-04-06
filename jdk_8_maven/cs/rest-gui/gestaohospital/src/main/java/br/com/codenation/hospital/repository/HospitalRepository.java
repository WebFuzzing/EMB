package br.com.codenation.hospital.repository;

import java.util.List;

import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;

@Repository
public interface HospitalRepository extends MongoRepository<Hospital, String>{
	List<Hospital> findByNameLikeIgnoreCase(String name);

//	List<Hospital> findByPositionNearAndAvailableBeds(Point p, int beds);
}