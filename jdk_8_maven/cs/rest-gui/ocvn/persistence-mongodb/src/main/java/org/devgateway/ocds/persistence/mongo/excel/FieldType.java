package org.devgateway.ocds.persistence.mongo.excel;

/**
 * @author idobre
 * @since 6/8/16
 */

import com.google.common.collect.ImmutableSet;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Identify the type of a Field that will be used in the writing strategy
 */
public final class FieldType {
    public static final int BASIC_FIELD = 1;

    public static final int OCDS_OBJECT_FIELD = 2;

    public static final int OCDS_OBJECT_SEPARETE_SHEET_FIELD = 3;

    /**
     * Utility classes should not have a public or default constructor.
     */
    private FieldType() {

    }

    /**
     * This is not a complete list of primitive types in Java or their wrappers!
     * Is used to quickly identify if a field is a 'simply' object that can be printed in a Cell
     */
    public static final ImmutableSet<Class<?>> BASICTYPES = new ImmutableSet.Builder<Class<?>>()
            .add(String.class)
            .add(BigDecimal.class)
            .add(Date.class)
            .add(Boolean.class)
            .add(Integer.class)
            .add(Long.class)
            .add(GeoJsonPoint.class)
            .add(boolean.class)
            .add(int.class)
            .add(long.class)
            .add(Double.class)
            .add(double.class)
            .build();
}
