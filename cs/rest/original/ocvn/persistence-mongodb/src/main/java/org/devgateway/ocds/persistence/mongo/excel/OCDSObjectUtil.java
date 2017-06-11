package org.devgateway.ocds.persistence.mongo.excel;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.Budget;
import org.devgateway.ocds.persistence.mongo.Item;
import org.devgateway.ocds.persistence.mongo.Location;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.Planning;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExportSepareteSheet;
import org.devgateway.ocds.persistence.mongo.info.ClassFields;
import org.devgateway.ocds.persistence.mongo.info.ClassFieldsDefault;
import org.devgateway.ocds.persistence.mongo.info.ClassFieldsExcelExport;
import org.devgateway.ocvn.persistence.mongo.dao.VNAward;
import org.devgateway.ocvn.persistence.mongo.dao.VNBudget;
import org.devgateway.ocvn.persistence.mongo.dao.VNItem;
import org.devgateway.ocvn.persistence.mongo.dao.VNLocation;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.devgateway.ocvn.persistence.mongo.dao.VNTender;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author idobre
 * @since 6/16/16
 */
public final class OCDSObjectUtil {
    private static final Logger LOGGER = Logger.getLogger(OCDSObjectUtil.class);

    private static Map<Field, Class> fieldsClassCache;

    private static Map<Class, List<Field>> classFieldsCache;

    static {
        fieldsClassCache = new HashMap<>();

        classFieldsCache = new HashMap<>();
    }

    /**
     * Utility classes should not have a public or default constructor.
     */
    private OCDSObjectUtil() {

    }

    /**
     * Return the {@link FieldType} of a Field
     * This is used to determine the writing strategy for this particular field
     *
     * @param field
     * @return
     */
    public static int getFieldType(final Field field) {
        final Class fieldClass = getFieldClass(field);

        if (FieldType.BASICTYPES.contains(fieldClass)) {
            return FieldType.BASIC_FIELD;
        }

        // check if this is an OCDS Object (an Object defined in our application)
        final String classPackage = fieldClass.getName().substring(0, fieldClass.getName().lastIndexOf('.'));
        if (classPackage.contains("org.devgateway")) {
            if (fieldClass.isEnum()) {
                return FieldType.BASIC_FIELD;
            }

            if (field.getAnnotation(ExcelExportSepareteSheet.class) != null) {
                return FieldType.OCDS_OBJECT_SEPARETE_SHEET_FIELD;
            }

            return FieldType.OCDS_OBJECT_FIELD;
        }

        LOGGER.error("We didn't get the field type for: '" + field.getName()
                + "', returning: " + FieldType.BASIC_FIELD);
        return FieldType.BASIC_FIELD;
    }

    /**
     * Return the Class of a field
     *
     * @param field
     * @return
     */
    public static Class getFieldClass(final Field field) {
        Class fieldClass = null;

        if (fieldsClassCache.get(field) != null) {
            fieldClass = fieldsClassCache.get(field);
        } else {
            if (field.getType().equals(java.util.Set.class) || field.getType().equals(java.util.List.class)) {
                final ParameterizedType genericListType = (ParameterizedType) field.getGenericType();
                try {
                    fieldClass = Class.forName(genericListType.getActualTypeArguments()[0].getTypeName());
                } catch (ClassNotFoundException e) {
                    LOGGER.error(e);
                }
            } else {
                fieldClass = field.getType();
            }

            // check if the class was extended and return thew new type
            if (INHERITEDOCDSOBJECTS.get(fieldClass) != null) {
                fieldClass = INHERITEDOCDSOBJECTS.get(fieldClass);
            }

            fieldsClassCache.put(field, fieldClass);
        }

        return fieldClass;
    }

    /**
     * Returns an Iterator with the Fields of a Class.
     * The fields are filtered with the {@link ClassFieldsExcelExport} class.
     *
     * @param clazz
     * @return
     */
    public static Iterator<Field> getFields(final Class clazz) {
        final Iterator<Field> fields;

        if (classFieldsCache.get(clazz) != null) {
            List<Field> fieldsList = classFieldsCache.get(clazz);
            fields = fieldsList.iterator();
        } else {
            final ClassFields classFields = new ClassFieldsExcelExport(
                    new ClassFieldsDefault(clazz, true)
            );

            fields = classFields.getFields();
            classFieldsCache.put(clazz, Lists.newArrayList(classFields.getFields()));
        }

        return fields;
    }

    /**
     * Try to get the ocid for a {@link Release}.
     *
     * @param object
     * @return ocid
     */
    public static String getOCDSObjectID(final Object object) {
        if (object == null || !(object instanceof Release)) {
            return null;
        }

        String objectId = null;
        try {
            Method ocidMethod = PropertyUtils.getReadMethod(new PropertyDescriptor("ocid", object.getClass()));
            if (ocidMethod != null) {
                objectId = (String) ocidMethod.invoke(object);
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {

        }

        return objectId;
    }

    /**
     * Use this Map if we have a particular implementation of the OCDS with many of the Objects extended
     * Example:
     *      .put(Award.class, VNAward.class)
     *      .put(Budget.class, VNBudget.class)
     */
    public static final ImmutableMap<Class, Class> INHERITEDOCDSOBJECTS = new ImmutableMap.Builder<Class, Class>()
            .put(Award.class, VNAward.class)
            .put(Budget.class, VNBudget.class)
            .put(Item.class, VNItem.class)
            .put(Location.class, VNLocation.class)
            .put(Organization.class, Organization.class)
            .put(Planning.class, VNPlanning.class)
            .put(Tender.class, VNTender.class)
            .build();
}
