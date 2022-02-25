package br.com.codenation.hospital.services;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import br.com.codenation.hospital.resource.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.Patient;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.dto.HospitalDTO;
import br.com.codenation.hospital.repository.HospitalRepository;
import br.com.codenation.hospital.repository.PatientRepository;
import br.com.codenation.hospital.repository.ProductRepository;
import br.com.codenation.hospital.resource.exception.HospitalCheioException;
import br.com.codenation.hospital.services.exception.ObjectNotFoundException;

@Service
public class HospitalService {

	@Autowired
	private  HospitalRepository repo;

	@Autowired
	private PatientRepository patientRepository;

	@Autowired
	private ProductRepository productRepository;
	

	@Autowired
	private LocationService locationService;

	//@Autowired
	//private LocationService locationService;
	
	public List<Hospital> findAll(){
		return repo.findAll();
	}
	

	public Hospital findById(String hospital_id) {
		Optional<Hospital> obj = repo.findById(hospital_id);
		return obj.orElseThrow(() -> new ObjectNotFoundException("Hospital não encontrado! ID:"+ hospital_id));	
	}
	
	public Hospital insert(Hospital obj) {		
		Location location = locationService.insertLocationByHospital(obj);
		obj.setLocation(location);
		
		return repo.insert(obj);
	}
	
	public void delete(String hospital_id) {
		findById(hospital_id);
		repo.deleteById(hospital_id);
	}
	
	public Hospital update(Hospital obj) {
		Hospital newObj = findById(obj.getId());
		updateData(newObj, obj);
		return repo.save(newObj);
	}
	
	private void updateData(Hospital newObj, Hospital obj) {
		newObj.setName(obj.getName());
		newObj.setAddress(obj.getAddress());
		newObj.setBeds(obj.getBeds());
		newObj.setAvailableBeds(obj.getAvailableBeds());
	}

	public Hospital fromDTO(HospitalDTO objDTO) {
		if(objDTO.getId() == null){
			return new Hospital(objDTO.getId(),objDTO.getName(),objDTO.getAddress(),objDTO.getBeds(),objDTO.getAvailableBeds());
		}
		return findById(objDTO.getId());
	}
	
	public HospitalDTO convertToDTO(Hospital model) {
        return new HospitalDTO(model);
    }
	
	public List<HospitalDTO> convertToDTOs(List<Hospital> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }	

	public Patient checkIn(Hospital hospital, Patient patient){
		if(hospital.temVaga()) {
			patient.setEntryDate(new Date());
			patient.setActive(true);
			patient.setExitDate(null);
			patientRepository.save(patient);

			hospital.addPacient(patient);
			repo.save(hospital);

			return patient;
		}
		throw new HospitalCheioException();
	}

	public Patient checkOut(Hospital hospital, String idPatient){
		Patient patient = hospital.getPatients().stream()
				.filter(p -> p.getId().equals(idPatient))
				.findFirst()
				.orElseThrow(() -> new ObjectNotFoundException("Paciente não encontrado no hospital!"));
		hospital.removePacient(patient);
		repo.save(hospital);
		patient.setActive(false);
		patient.setExitDate(new Date());
		return patientRepository.save(patient);
	}

	public HospitalDTO findHospitalMaisProximoComVagas(Double lat, Double lon, Double raioMaximo) {
		List<HospitalDTO> hospitais = locationService.findHospitalNearLocationBy(lat, lon, raioMaximo);

		return hospitais.stream()
				.filter(h -> h.getAvailableBeds() > 0)
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Nenhum hospital próximo com vagas encontrado!"));
	}

	private Hospital findHospitalProximoComEstoque(String hospitalId, Product produto) {

		List<HospitalDTO> hospitaisDTO = locationService.findHospitalNearHospitalBy(hospitalId, null);


		List<Hospital> hospitais = hospitaisDTO.stream()
				.map(h -> fromDTO(h))
				.collect(Collectors.toList());

		return hospitais.stream()
				.filter(h -> h.getProducts().contains(produto))
				.findFirst()
				.orElseThrow(() -> new ResourceNotFoundException("Nenhum hospital próximo com este produto encontrado!"));
	}

	public String transfereProduto(Hospital hospital, String idProduto, Integer quantidade) {
		//produto existe?
		Product product = productRepository.findById(idProduto)
				.orElseThrow(()-> new ObjectNotFoundException("Produto não cadastrado em nenhum hospital!"));
		//encontra hospital mais prox que contenha o produto
		Hospital hospitalOrigem = findHospitalProximoComEstoque(hospital.getId(), product);
		product = hospitalOrigem.getProducts().stream()
				.filter(p -> p.getId().equals(idProduto))
				.findFirst().get();
		//verifica se tem quandidade suficiente para transferir
		if(product.getQuantity() > quantidade + 4){
			//add novo produto no hospital
			Product novoProduto = new Product();
			novoProduto.setName(product.getName());
			novoProduto.setDescription(product.getDescription());
			novoProduto.setProductType(product.getProductType());
			novoProduto.setQuantity(quantidade);
			productRepository.save(novoProduto);
			hospital.setProduct(novoProduto);
			//diminui quantidade do hospital origem
			product.diminuiQuantidade(quantidade);
			productRepository.save(product);
			return "transferencia realizada!";
		}
		return "transferencia não pode ser feita!";
	}
	

}