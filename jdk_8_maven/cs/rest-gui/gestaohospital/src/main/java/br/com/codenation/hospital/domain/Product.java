package br.com.codenation.hospital.domain;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="product_collection")
public class Product implements Serializable{
	private static final long serialVersionUID = 1L;
	

	@Id
	private ObjectId _id;
	private String name;
	private String description;
	private int quantity;
	private ProductType productType;

	public Product() {
	
	}	
	
	public Product(ObjectId _id, String name, String description, int quantity, ProductType productType) {
		this._id = _id;
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.productType = productType;
	}
	
	public Product(String name, String description, int quantity, ProductType productType) {
		this.name = name;
		this.description = description;
		this.quantity = quantity;
		this.productType = productType;
	}

	public String getId() {
		return _id.toHexString();
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
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

	public void diminuiQuantidade(int quantity){
		this.quantity-=quantity;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((_id == null) ? 0 : _id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + quantity;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Product other = (Product) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (_id == null) {
			if (other._id != null)
				return false;
		} else if (!_id.equals(other._id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (quantity != other.quantity)
			return false;
		return true;
	}
}