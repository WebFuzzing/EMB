package org.devgateway.ocds.persistence.mongo.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation that indicates if a field should be exported to Excel Export
 *
 * @author idobre
 * @since 6/7/16
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExport {
    String value() default "";
}

