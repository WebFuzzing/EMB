package org.devgateway.ocds.persistence.mongo.spring.json2object;

import java.io.IOException;

/**
 * @author idobre
 * @since 5/31/16
 */
public interface JsonToObject<T> {

    /**
     * Transform a JSON String to a T object
     *
     * @return T object
     */
    T toObject() throws IOException;
}
