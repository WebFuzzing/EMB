package br.com.codenation.hospital.dto;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import br.com.codenation.hospital.domain.Product;
import br.com.codenation.hospital.domain.ProductType;

public class ProductDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	@NotEmpty
	private String name;
	
	
	private String description;
	
	@NotEmpty
	private int quantity;
	
	@NotEmpty
	private ProductType productType;
	
	public ProductDTO() {
		
	}
	
	public ProductDTO(Product obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.quantity = obj.getQuantity();
		this.productType = obj.getProductType();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getProductName() {
		return name;
	}

	public void setProductName(String productName) {
		this.name = productName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public ProductType getProductType() {
		return productType;
	}

	public void setProductType(ProductType productType) {
		this.productType = productType;
	}
}