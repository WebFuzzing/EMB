package br.com.codenation.hospital.resource;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.codenation.hospital.constant.Constant;
import br.com.codenation.hospital.dto.ProductDTO;
import br.com.codenation.hospital.services.ProductService;

@CrossOrigin("http://localhost:4200") // permissão para o Angular
@RestController
@RequestMapping(path = Constant.V1Path)
public class ProductResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductResource.class);

	@Autowired
	private ProductService service;

	@GetMapping(path = "estoque/{produto_id}")
	public ResponseEntity<ProductDTO> findProductBy(@PathVariable String hospital_id, @PathVariable String produto_id) {
		try {
			ProductDTO productDTO = service.findById(produto_id);
			return Optional.ofNullable(productDTO).map(productReponse -> ResponseEntity.ok().body(productReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("findProductBy - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(path = "estoque")
	public ResponseEntity<List<ProductDTO>> findAllProductBy(@PathVariable String hospital_id) {
		try {
			List<ProductDTO> productList = service.findByHospitalId(hospital_id);
			return Optional.ofNullable(productList).map(productReponse -> ResponseEntity.ok().body(productReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("findAllProductBy - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@PostMapping(path = "estoque")
	public ResponseEntity<ProductDTO> insert(@PathVariable String hospital_id, @RequestBody ProductDTO productDTO) {
		try {
			ProductDTO newProductDTO = service.insert(hospital_id, productDTO);
			return Optional.ofNullable(newProductDTO).map(productReponse -> ResponseEntity.ok().body(productReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("insert - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@DeleteMapping(path = "estoque/{produto_id}")
	public ResponseEntity<String> delete(@PathVariable String hospital_id, @PathVariable String produto_id) {
		try {
			ProductDTO deleteProductDTO = service.findById(produto_id);
			if (deleteProductDTO != null) {
				service.delete(hospital_id, deleteProductDTO.getId());
			}
			return Optional.ofNullable(deleteProductDTO)
					.map(productReponse -> ResponseEntity.ok().body("Produto apagado id: " + produto_id))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("delete - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}

	@PutMapping(path = "estoque/{produto_id}")
	public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO, @PathVariable String hospital_id,
			@PathVariable String produto_id) {
		try {
			productDTO.setId(produto_id);
			ProductDTO updateProductDTO = service.update(hospital_id, productDTO);
			return Optional.ofNullable(updateProductDTO)
					.map(hospitalReponse -> ResponseEntity.ok().body(hospitalReponse))
					.orElseGet(() -> ResponseEntity.notFound().build());
		} catch (Exception ex) {
			LOGGER.error("update - Handling error with message: {}", ex.getMessage());
			return ResponseEntity.badRequest().build();
		}
	}
}