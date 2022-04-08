package br.com.codenation.hospital.domain;

public class LocationBuilder {
    private Double latitude;
    private Double longitude;
    private String name;
    private String referenceId;
    private LocationCategory category;

    public LocationBuilder setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public LocationBuilder setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public LocationBuilder setName(String name) {
        this.name = name;
        return this;
    }
    
    public LocationBuilder setLocationCategory(LocationCategory category) {
        this.category = category;
        return this;
    }
    
    public LocationBuilder setReferenceId(String referenceId) {
        this.referenceId = referenceId;
        return this;
    }

    public Location build() {
        return new Location(referenceId, category, name, latitude, longitude);
    }
}