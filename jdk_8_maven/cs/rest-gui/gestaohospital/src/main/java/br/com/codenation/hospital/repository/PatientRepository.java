package br.com.codenation.hospital.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import br.com.codenation.hospital.domain.Patient;

@Repository
public interface PatientRepository extends MongoRepository<Patient, String>{
	
}