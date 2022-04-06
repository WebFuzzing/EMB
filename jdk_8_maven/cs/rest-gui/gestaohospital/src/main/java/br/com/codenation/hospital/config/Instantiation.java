package br.com.codenation.hospital.config;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.TimeZone;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.LocationBuilder;
import br.com.codenation.hospital.domain.LocationCategory;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.domain.ProductType;
import br.com.codenation.hospital.repository.HospitalRepository;
import br.com.codenation.hospital.repository.LocationRepository;
import br.com.codenation.hospital.repository.PatientRepository;
import br.com.codenation.hospital.repository.ProductRepository;

//Operação de instanciação da base de dados

@Configuration
public class Instantiation implements CommandLineRunner{

	@Autowired
	private HospitalRepository hospitalRepository;
	
	@Autowired
	private PatientRepository patientRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Override
	public void run(String... args) throws Exception {
		DateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
		
		hospitalRepository.deleteAll(); //deleta todos dados do mongodb
		patientRepository.deleteAll();
		productRepository.deleteAll();
		locationRepository.deleteAll();

		Location locationUm = new LocationBuilder()
				.setReferenceId("Av. Albert Einstein, 627 - Jardim Leonor, São Paulo - SP, 05652-900")
				.setLocationCategory(LocationCategory.HOSPITAL)
				.setName("Hospital Israelita Albert Einstein")
				.setLatitude(-23.5920091D)
				.setLongitude(-46.6388042029871D)
				.build();

		Location locationDois = new LocationBuilder()
				.setReferenceId("Rua Engenheiro Oscar Americano, 840 - Jardim Guedala, São Paulo - SP, 05605-050")
				.setLocationCategory(LocationCategory.HOSPITAL)
				.setName("Hospital São Luiz Unidade Morumbi")
				.setLatitude(-23.591093D)
				.setLongitude(-46.703459)
				.build();

		Location locationTres = new LocationBuilder()
				.setReferenceId("Av. Prof. Francisco Morato, 719 - Butantã, São Paulo - SP, 05513-000")
				.setLocationCategory(LocationCategory.HOSPITAL)
				.setName("Hospital Next Butantã")
				.setLatitude(-23.578151D)
				.setLongitude(-46.708343D)
				.build();
		
		
		Location locationPatientUm = new LocationBuilder()
				.setReferenceId("R. José Pepe, 40-142 - Jardim Leonor, São Paulo - SP, 05652-080")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Maria")
				.setLatitude(-23.597442D)
				.setLongitude(-46.713830D)
				.build();

		Location locationPatientDois = new LocationBuilder()
				.setReferenceId("Rua Dr. Celso Dario Guimarães, 201 - Jardim Morumby, São Paulo - SP, 05655-030")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Pedro")
				.setLatitude(-23.608176D)
				.setLongitude(-46.71718D)
				.build();

		Location locationPatientTres = new LocationBuilder()
				.setReferenceId("R. Alvorada do Sul, 183 - Morumbi, São Paulo - SP, 05612-010")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Joana")
				.setLatitude(-23.591692D)
				.setLongitude(-46.708806D)
				.build();

		Location locationPatientQuatro = new LocationBuilder()
				.setReferenceId("Av. George Saville Dodd, 45 - Morumbi, São Paulo - SP, 05608-020")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Arya")
				.setLatitude(-23.580128D)
				.setLongitude(-46.708799D)
				.build();
		
		
		Location locationPatientCinco = new LocationBuilder()
				.setReferenceId("Rua dos Limantos, 156 - Cidade Jardim, São Paulo - SP, 05675-020")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("João")
				.setLatitude(-23.593855D)
				.setLongitude(-46.701794D)
				.build();
		
		Location locationPatientSeis = new LocationBuilder()
				.setReferenceId("Av. Morumbi, 354 - Morumbi, São Paulo - SP, 05606-010")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Gabriel")
				.setLatitude(-23.578307D)
				.setLongitude(-46.706638D)
				.build();
		
		Location locationPatientSete = new LocationBuilder()
				.setReferenceId("R. Campo Verde, 700 - Jardim Europa, São Paulo - SP, 04794-000")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Ana")
				.setLatitude(-23.577483D)
				.setLongitude(-46.694697D)
				.build();
		
		Location locationPatientOito = new LocationBuilder()
				.setReferenceId("R. Campo Verde, 516 - Jardim Europa, São Paulo - SP, 01456-010")
				.setLocationCategory(LocationCategory.PATIENT)
				.setName("Paula")
				.setLatitude(-23.576718D)
				.setLongitude(-46.693139D)
				.build();
		
		locationRepository.saveAll(Arrays.asList(locationUm,locationDois,locationTres)); //adiciona dados
		locationRepository.saveAll(Arrays.asList(locationPatientUm,locationPatientDois,locationPatientTres)); //adiciona dados
		locationRepository.saveAll(Arrays.asList(locationPatientQuatro,locationPatientCinco,locationPatientSeis)); //adiciona dados
		locationRepository.saveAll(Arrays.asList(locationPatientSete,locationPatientOito)); //adiciona dados
		
		Hospital hospitalUm = new Hospital("1", "Hospital Israelita Albert Einstein", "Av. Albert Einstein, 627 - Jardim Leonor, São Paulo - SP, 05652-900", 21,5, locationUm);
		Hospital hospitalDois = new Hospital("2", "Hospital São Luiz Unidade Morumbi", "Rua Engenheiro Oscar Americano, 840 - Jardim Guedala, São Paulo - SP, 05605-050", 11,6, locationDois);
		Hospital hospitalTres = new Hospital("3", "Hospital Next Butantã", "Av. Prof. Francisco Morato, 719 - Butantã, São Paulo - SP, 05513-000", 32,12, locationTres);
		
		hospitalRepository.saveAll(Arrays.asList(hospitalUm,hospitalDois,hospitalTres)); //adiciona dados
		
		Patient pacient1 = new Patient("1", "Maria", "864789205", sdf.parse("16/07/2003"), "feminino", sdf.parse("16/07/2019"), locationPatientUm);
		Patient pacient2 = new Patient("2", "Pedro", "864789205", sdf.parse("16/07/2003"), "masculino", sdf.parse("16/07/2019"), locationPatientDois);
		Patient pacient3 = new Patient("3", "Joana", "864789205", sdf.parse("16/07/2003"), "feminino", sdf.parse("16/07/2019"), locationPatientTres);
		Patient pacient4 = new Patient("4", "Arya", "864789205", sdf.parse("16/07/2003"), "feminino", sdf.parse("16/07/2019"), locationPatientQuatro);
		Patient pacient5 = new Patient("5", "João", "864789205", sdf.parse("16/07/2003"), "masculino", sdf.parse("16/07/2019"), locationPatientCinco);
		Patient pacient6 = new Patient("6", "Gabriel", "864789205", sdf.parse("16/07/2003"), "masculino", sdf.parse("16/07/2019"), locationPatientSeis);
		Patient pacient7 = new Patient("7", "Ana", "864789205", sdf.parse("16/07/2003"), "feminino", sdf.parse("16/07/2019"), locationPatientSete);
		Patient pacient8 = new Patient("8", "Paula", "864789205", sdf.parse("16/07/2003"), "feminino", sdf.parse("16/07/2019"), locationPatientOito);
		
		patientRepository.saveAll(Arrays.asList(pacient1,pacient2,pacient3,pacient4,pacient5,pacient6,pacient7,pacient8));
		

		Product produto1 = new Product(ObjectId.get(), "Alimento", "Maçã", 12, ProductType.COMMON);
		Product produto2 = new Product(ObjectId.get(), "Alimento", "Arroz", 3, ProductType.COMMON);
		Product produto3 = new Product(ObjectId.get(), "Alimento", "Feijão", 2, ProductType.COMMON);
		Product produto4 = new Product(ObjectId.get(), "Alimento", "Massa", 5, ProductType.COMMON);
		Product produto5 = new Product(ObjectId.get(), "Alimento", "Massa", 5, ProductType.COMMON);
		
		Product produto6 = new Product(ObjectId.get(), "Banco de Sangue", "Sangue", 8, ProductType.BLOOD);
		Product produto7 = new Product(ObjectId.get(), "Banco de Sangue", "Sangue", 1, ProductType.BLOOD);
		Product produto8 = new Product(ObjectId.get(), "Banco de Sangue", "Sangue", 4, ProductType.BLOOD);
		
		productRepository.saveAll(Arrays.asList(produto1,produto2,produto3,produto4,produto5,produto6,produto7,produto8));
		
		//referenciando pacientes e produtos ao hospital
		hospitalUm.getPatients().addAll(Arrays.asList(pacient1,pacient2));
		hospitalTres.getPatients().addAll(Arrays.asList(pacient3,pacient4,pacient5,pacient6,pacient7,pacient8));
		
		hospitalUm.getProducts().addAll(Arrays.asList(produto5,produto6));
		hospitalDois.getProducts().addAll(Arrays.asList(produto1,produto7));
		hospitalTres.getProducts().addAll(Arrays.asList(produto2,produto3,produto4,produto8));

		hospitalRepository.save(hospitalUm);
		hospitalRepository.save(hospitalDois);
		hospitalRepository.save(hospitalTres);

//		MongoCollection<Document> collection = database.getCollection("hospital_collection");
//		collection.createIndex(Indexes.geo2dsphere("location"));
	}
}