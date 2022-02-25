package br.com.codenation.hospital.dto;

import java.io.Serializable;
import br.com.codenation.hospital.domain.Hospital;

public class HospitalDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private String id;
	private String name;
	private String address;
	private int beds;
	private int availableBeds;
	private String longitude;
	private String latitude;
	
	public HospitalDTO() {
		
	}
	
	public HospitalDTO(Hospital obj) {
		this.id = obj.getId();
		this.name = obj.getName();
		this.address = obj.getAddress();
		this.beds = obj.getBeds();
		this.availableBeds = obj.getAvailableBeds();
		if(obj.getLocation() != null) {
			this.longitude = String.valueOf(obj.getLocation().getPosition().getX());
			this.latitude = String.valueOf(obj.getLocation().getPosition().getY());
		}
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getBeds() {
		return beds;
	}

	public void setBeds(int beds) {
		this.beds = beds;
	}

	public int getAvailableBeds() {
		return availableBeds;
	}

	public void setAvailableBeds(int availableBeds) {
		this.availableBeds = availableBeds;
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
}