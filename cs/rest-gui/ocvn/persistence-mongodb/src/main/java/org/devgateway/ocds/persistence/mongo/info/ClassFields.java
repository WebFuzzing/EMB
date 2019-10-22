package org.devgateway.ocds.persistence.mongo.info;

import java.lang.reflect.Field;
import java.util.Iterator;

/**
 * Returns all the fields of a Class
 *
 * @author idobre
 * @since 6/3/16
 */
public interface ClassFields {
    Iterator<Field> getFields();
}
