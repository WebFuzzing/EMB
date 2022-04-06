package br.com.codenation.hospital.dto;

import java.io.Serializable;
import java.util.Objects;

import br.com.codenation.hospital.domain.Location;
import br.com.codenation.hospital.domain.Product;

public class LocationDTO implements Serializable {
	private static final long serialVersionUID = 10L;

	private String id;
	private String name;
	private String referenceId;
	private String category;
	private String longitude;
	private String latitude;

	public LocationDTO(String category, String name, String longitude, String latitude) {
		this.category = category;
		this.name = name;
    	this.longitude = longitude;
    	this.latitude = latitude;
	}
	
	public LocationDTO(Location obj) {
		this.id = obj.getId();
		this.category = obj.getLocationCategory().getDescricao();
		this.referenceId = obj.getReferenceId();
		this.name = obj.getName();
		this.latitude = String.valueOf(obj.getPosition().getY());
		this.longitude = String.valueOf(obj.getPosition().getX());
	}
	
	public String getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || this.getClass() != o.getClass()) return false;
		final LocationDTO that = (LocationDTO) o;
		return 	Objects.equals(this.getId(), that.getId()) && 
				Objects.equals(this.getReferenceId(), that.getReferenceId()) &&
				Objects.equals(this.getName(), that.getName()) &&
				Objects.equals(this.getLongitude(), that.getLongitude()) &&
				Objects.equals(this.getLatitude(), that.getLatitude()) &&
				Objects.equals(this.getCategory(), that.getCategory());
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.getId(), this.getName(), this.getReferenceId(), this.getLongitude(), this.getLatitude(), this.getCategory());
	}
}
