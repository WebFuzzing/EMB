package org.devgateway.ocds.persistence.mongo.info;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author idobre
 * @since 6/3/16
 */
public final class ClassFieldsDefault implements ClassFields {
    private final Class clazz;

    private Field[] declaredFields;

    private Boolean getInheritedFields = false;

    public ClassFieldsDefault(final Class clazz) {
        this.clazz = clazz;
    }

    public ClassFieldsDefault(final Class clazz, final Boolean getInheritedFields) {
        this(clazz);
        this.getInheritedFields = getInheritedFields;
    }

    @Override
    public Iterator<Field> getFields() {
        // cache declared fields of a class
        if (declaredFields == null) {
            if (getInheritedFields) {
                declaredFields = getAllFields(clazz).toArray(new Field[getAllFields(clazz).size()]);
            } else {
                declaredFields = clazz.getDeclaredFields();
            }
        }

        // filter some of the fields including this$0 used in inner classes
        Iterator<Field> fields = Arrays.stream(declaredFields)
                .filter(field -> !field.getName().equals("serialVersionUID"))
                .filter(field -> !field.getName().equals("this$0"))
                .iterator();

        return fields;
    }

    /**
     * Function used to get also the inherited fields.
     *
     * @param clazz
     * @return
     */
    private List<Field> getAllFields(final Class clazz) {
        final List<Field> fields = new ArrayList<>();
        final Class superClazz = clazz.getSuperclass();

        if (superClazz != null) {
            fields.addAll(getAllFields(superClazz));
        }

        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));

        return fields;
    }
}
