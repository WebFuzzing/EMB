package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJson;

/**
 * Location
 * <p>
 * The location where activity related to this tender, contract or license will be delivered, or will take place.
 * A location can be described by either a geometry (point location, line or polygon), or a gazetteer entry, or both.
 *
 */
@JsonPropertyOrder({
        "description",
        "geometry",
        "gazetteer",
        "uri"
})
public abstract class Location<T extends GeoJson<?>> implements Identifiable {
    @Id
    private String id;

    /**
     * A name or description of this location. This might include the name(s) of the location(s),
     * or might provide a human readable description of the location to be covered.
     * This description may be used in a user-interface.
     *
     */
    @ExcelExport
    @JsonProperty("description")
    private String description;

    @JsonProperty("gazetteer")
    private Gazetteer gazetteer = new Gazetteer();

    /**
     * A URI to a further description of the activity location. This may be a human readable document with information
     * on the location, or a machine-readable description of the location.
     *
     */
    @JsonProperty("uri")
    private String uri;

    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     * A name or description of this location. This might include the name(s) of the location(s),
     * or might provide a human readable description of the location to be covered.
     * This description may be used in a user-interface.
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * A name or description of this location. This might include the name(s) of the location(s),
     * or might provide a human readable description of the location to be covered.
     * This description may be used in a user-interface.
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The gazetteer
     */
    @JsonProperty("gazetteer")
    public Gazetteer getGazetteer() {
        return gazetteer;
    }

    /**
     *
     * @param gazetteer
     *     The gazetteer
     */
    @JsonProperty("gazetteer")
    public void setGazetteer(final Gazetteer gazetteer) {
        this.gazetteer = gazetteer;
    }

    /**
     * A URI to a further description of the activity location. This may be a human readable document with information
     * on the location, or a machine-readable description of the location.
     *
     * @return
     *     The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * A URI to a further description of the activity location. This may be a human readable document with information
     * on the location, or a machine-readable description of the location.
     *
     * @param uri
     *     The uri
     */
    @JsonProperty("uri")
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * We follow the [GeoJSON standard](http://geojson.org/) to express basic location information,
     * using latitude and longitude values in the
     * [WGS84](https://en.wikipedia.org/wiki/World_Geodetic_System) (EPSG:4326) projection.
     * A point location can be identified by geocoding a delivery address. For concession licenses,
     * or other contracts covering a polygon location which is not contained in a known gazetteer, polygon and
     * multi-polygon can be used.
     *
     * @return
     *     The geometry
     */
    public abstract T getGeometry();

    /**
     * We follow the [GeoJSON standard](http://geojson.org/) to express basic location information,
     * using latitude and longitude values in the
     * [WGS84](https://en.wikipedia.org/wiki/World_Geodetic_System) (EPSG:4326) projection.
     * A point location can be identified by geocoding a delivery address. For concession licenses,
     * or other contracts covering a polygon location which is not contained in a known gazetteer, polygon and
     * multi-polygon can be used.
     *
     * @param geometry
     *     The geometry
     */
    public abstract void setGeometry(T geometry);

    public abstract String getGazetteerPrefix();

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(description).
                append(gazetteer).
                append(uri).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Location)) {
            return false;
        }
        Location rhs = ((Location) other);
        return new EqualsBuilder().
                append(description, rhs.description).
                append(gazetteer, rhs.gazetteer).
                append(uri, rhs.uri).
                isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return id;
    }
}
