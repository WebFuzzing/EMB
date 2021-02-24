/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Location;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author mpostelnicu
 *
 */
@Document(collection = "location")
public class VNLocation extends Location<GeoJsonPoint> {
    public static final String GEONAMES_URI_PREFIX = "http://www.geonames.org/";

    public static final String GEONAMES_SCHEME = "GEONAMES";

    @ExcelExport
    private GeoJsonPoint geometry;

    public VNLocation() {
        super();
        this.getGazetteer().setScheme(GEONAMES_SCHEME);
    }

    @Override
    public GeoJsonPoint getGeometry() {
        return geometry;
    }

    @Override
    public void setGeometry(GeoJsonPoint geometry) {
        this.geometry = geometry;
    }

    @Override
    public String getGazetteerPrefix() {
        return GEONAMES_URI_PREFIX;
    }
}
