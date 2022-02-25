package br.com.codenation.hospital.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import br.com.codenation.hospital.domain.Product;


public interface ProductRepository extends MongoRepository<Product, String>{
	Product findBy_id(ObjectId _id);
	
	List<Product> findByNameLikeIgnoreCase(String name);
}