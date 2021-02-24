package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author idobre
 * @since 9/13/16
 *
 * Default implementation of the abstract class {@link Location}
 */
@JsonPropertyOrder({
        "geometry"
})
@Document(collection = "location")
public class DefaultLocation extends Location<GeoJsonPoint> {
    public static final String GEONAMES_URI_PREFIX = "http://www.geonames.org/";

    public static final String GEONAMES_SCHEME = "GEONAMES";

    @ExcelExport
    @JsonProperty("geometry")
    private GeoJsonPoint geometry;

    public DefaultLocation() {
        super();
        this.getGazetteer().setScheme(GEONAMES_SCHEME);
    }

    @Override
    public GeoJsonPoint getGeometry() {
        return (GeoJsonPoint) geometry;
    }

    @Override
    public void setGeometry(final GeoJsonPoint geometry) {
        this.geometry = geometry;
    }

    @Override
    public String getGazetteerPrefix() {
        return GEONAMES_URI_PREFIX;
    }
}
