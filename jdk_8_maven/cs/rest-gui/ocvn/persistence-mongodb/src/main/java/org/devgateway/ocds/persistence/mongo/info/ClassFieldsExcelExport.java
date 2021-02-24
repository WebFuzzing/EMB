package org.devgateway.ocds.persistence.mongo.info;

import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Decorator class used to obtain only Excel Exportable fields (the ones annotated with {@link ExcelExport}
 *
 * @author idobre
 * @since 6/7/16
 */
public final class ClassFieldsExcelExport implements ClassFields {
    private final ClassFields original;

    public ClassFieldsExcelExport(final ClassFields classFields) {
        this.original = classFields;
    }

    @Override
    public Iterator<Field> getFields() {
        // cache the stream
        final Iterable<Field> originalFields = () -> this.original.getFields();

        // return only classes that are annotated with @ExcelExport
        final Stream<Field> stream = StreamSupport.stream(originalFields.spliterator(), false)
                .filter(field -> field.getAnnotation(ExcelExport.class) != null);

        return stream.iterator();
    }
}
