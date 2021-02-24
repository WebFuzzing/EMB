package org.devgateway.ocds.persistence.mongo.excel.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Class that indicates if an OCDS object should be exported in a separate Excel Sheet
 * @author idobre
 * @since 6/7/16
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelExportSepareteSheet {
    String value() default "";
}
