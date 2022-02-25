package br.com.codenation.hospital.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.codenation.hospital.domain.Hospital;
import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.dto.ProductDTO;
import br.com.codenation.hospital.repository.ProductRepository;
import br.com.codenation.hospital.services.exception.ObjectNotFoundException;

@Service
public class ProductService {
	
	@Autowired
	private  ProductRepository productRepository;
	
	@Autowired
	private  HospitalService hospitalService;
	
	public List<ProductDTO> findAll(){
		return convertToDTOs(productRepository.findAll());
	}
	
	public ProductDTO findById(ObjectId id) {
		return convertToDTO(productRepository.findBy_id(id));
    }
	
	public ProductDTO findById(String id) {
		return convertToDTO(findProductById(id));
    }
	
	private Product findProductById(String id) {
        Optional<Product> result = productRepository.findById(id);
		return result.orElseThrow(() -> new ObjectNotFoundException("Product não encontrado! ID: "+ id));
    }
	
	public List<ProductDTO> findByHospitalId(String hospitalId) {
		Hospital hospital = hospitalService.findById(hospitalId);
	    List<Product> products = hospital.getProducts();
		return convertToDTOs(products);
    }
	
	public List<ProductDTO> findByName(String name) {
		List<Product> products = productRepository.findByNameLikeIgnoreCase(name);
		return convertToDTOs(products);
    }

	public ProductDTO insert(String hospitalId, ProductDTO productDTO) {
	    Product product = fromDTO(productDTO);
	    product = productRepository.save(product);
		Hospital hospital = hospitalService.findById(hospitalId);
	    hospital.setProduct(product);
		return convertToDTO(product);
	}
	
	public void delete(String hospitalId, String productId) {
		Product removeProduct = findProductById(productId);
		Hospital hospital = hospitalService.findById(hospitalId);
	    hospital.getProducts().remove(removeProduct);

		productRepository.deleteById(productId);
	}
	
	public ProductDTO update(String hospitalId, ProductDTO product) {
		Product updateProduct = findProductById(product.getId());
		updateProduct.setName(product.getName());
		updateProduct.setDescription(product.getDescription());
		updateProduct.setQuantity(product.getQuantity());
		updateProduct.setProductType(product.getProductType());
		return convertToDTO(productRepository.save(updateProduct));
	}
	
	public Product fromDTO(ProductDTO productDTO) {
		return new Product(productDTO.getProductName(), productDTO.getDescription(), productDTO.getQuantity(), productDTO.getProductType());
	}
	
	private ProductDTO convertToDTO(Product model) {
		ProductDTO dto = new ProductDTO();
        dto.setId(model.getId());
        dto.setName(model.getName());
        dto.setDescription(model.getDescription());
        dto.setQuantity(model.getQuantity());
        dto.setProductType(model.getProductType());
        return dto;
    }
	
	private List<ProductDTO> convertToDTOs(List<Product> models) {
        return models.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
}