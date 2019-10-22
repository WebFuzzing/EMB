/**
 *
 */
package org.devgateway.ocds.persistence.mongo.spring;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.devgateway.ocds.persistence.mongo.Identifiable;
import org.devgateway.ocds.persistence.mongo.Record;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.Tag;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;
import org.devgateway.ocds.persistence.mongo.repository.main.RecordRepository;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.collect.Sets;

/**
 * @author mpostelnicu
 *
 */
@Service
public class ReleaseCompilerService {

    protected static final Logger logger = LoggerFactory.getLogger(ReleaseCompilerService.class);

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private RecordRepository recordRepository;

    // @Autowired
    // private OcdsSchemaValidatorService ocdsSchemaValidatorService;
    //
    // @Autowired
    // private ObjectMapper jacksonObjectMapper;

    @Autowired
    protected Reflections reflections;

    private Set<Field> fieldsAnnotatedWithMerge;

    @PostConstruct
    protected void init() {
        fieldsAnnotatedWithMerge = Sets.newConcurrentHashSet(reflections.getFieldsAnnotatedWith(Merge.class));
    }

    /**
     * @param left
     * @param right
     * @return
     * @see {@link MergeStrategy#overwrite}
     */
    protected Object mergeFieldStrategyOverwrite(final Object left, final Object right) {
        return right;
    }

    /**
     * @param left
     * @param right
     * @return
     * @see {@link MergeStrategy#ocdsOmit}
     */
    protected Object mergeFieldStrategyOcdsOmit(final Object left, final Object right) {
        return null;
    }

    /**
     *
     * @param left
     * @param right
     * @return
     * @see {@link MergeStrategy#ocdsVersion}
     */
    protected Object mergeFieldStrategyOcdsVersion(final Object left, final Object right) {
        return right;
    }

    protected Identifiable getIdentifiableById(final Serializable id, final Collection<Identifiable> col) {
        for (Identifiable identifiable : col) {
            if (identifiable.getIdProperty().equals(id)) {
                return identifiable;
            }
        }
        return null;
    }

    /**
     * @param leftCollection
     * @param rightCollection
     * @return
     * @see {@link MergeStrategy#arrayMergeById}
     */
    @SuppressWarnings("unchecked")
    protected <S extends Collection<Identifiable>> S mergeFieldStrategyArrayMergeById(final S leftCollection,
                                                                                      final S rightCollection) {

        // target collections must be instantiated
        S target = null;
        try {
            target = (S) leftCollection.getClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        // we add all the left
        target.addAll(leftCollection);

        // iterate all right elements
        for (Identifiable rightIdentifiable : rightCollection) {

            // if there is an existing element with the same id, perform merge
            // on the children and replace existing left element
            Identifiable leftIdentifiable = getIdentifiableById(rightIdentifiable.getIdProperty(), leftCollection);
            if (leftIdentifiable != null) {
                target.remove(leftIdentifiable);
                target.add(mergeOcdsBeans(leftIdentifiable, rightIdentifiable));
            } else {
                // otherwise add the new element to the left list
                target.add(rightIdentifiable);
            }
        }
        return target;
    }

    /**
     * Merges the fields of the right bean into a shallow copy of the left bean
     *
     * @param leftBean
     * @param rightBean
     * @return
     */
    @SuppressWarnings("unchecked")
    protected <S> S mergeOcdsBeans(final S leftBean, final S rightBean) {

        // if there is no data to the right, the merge just returns the
        // unmutated left
        if (rightBean == null) {
            return leftBean;
        }

        Class<?> clazz = rightBean.getClass();
        if (leftBean != null && !leftBean.getClass().equals(clazz)) {
            throw new RuntimeException("Attempted the merging of objects of different type!");
        }

        //we perform a shallow copy of the left bean
        S target;
        try {
            target = (S) BeanUtils.cloneBean(leftBean);
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException
                | NoSuchMethodException e1) {
            throw new RuntimeException(e1);
        }

        Arrays.asList(rightBean.getClass().getDeclaredFields()).parallelStream().forEach(field -> {
            try {
                PropertyUtils.setProperty(target, field.getName(), mergeFieldFromOcdsBeans(field, leftBean, rightBean));
            } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

        return target;

    }

    /**
     * Computes the output of an atomic merging operation on a specific field
     *
     * @param field the field to perform the merge on
     * @param leftBean the left bean
     * @param rightBean the right bean
     * @return the merged result
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    protected <S> Object mergeFieldFromOcdsBeans(final Field field, final S leftBean, final S rightBean)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        Object rightFieldValue = PropertyUtils.getProperty(rightBean, field.getName());
        Object leftFieldValue = PropertyUtils.getProperty(leftBean, field.getName());
        if (fieldsAnnotatedWithMerge.contains(field)) {
            MergeStrategy mergeStrategy = field.getDeclaredAnnotation(Merge.class).value();
            switch (mergeStrategy) {
                case overwrite:
                    return mergeFieldStrategyOverwrite(leftFieldValue, rightFieldValue);
                case ocdsOmit:
                    return mergeFieldStrategyOcdsOmit(leftFieldValue, rightFieldValue);

                case ocdsVersion:
                    return mergeFieldStrategyOcdsVersion(leftFieldValue, rightFieldValue);

                case arrayMergeById:
                    return mergeFieldStrategyArrayMergeById((Collection<Identifiable>) leftFieldValue,
                            (Collection<Identifiable>) rightFieldValue);

                default:
                    throw new RuntimeException("Unknown or unimplemented merge strategy!");
            }
        } else {
            // if no merge strategy was defined for the given field,
            // recursively invoke the method on the field value
            return mergeOcdsBeans(leftFieldValue, rightFieldValue);
        }
    }

    protected Release createCompiledRelease(final Record record) {
        // empty records produce null compiled release
        if (record.getReleases().isEmpty()) {
            return null;
        }
        // records with just one release produce a compiled release identical to
        // the one release
        Release left = record.getReleases().get(0);
        if (record.getReleases().size() > 1) {
            // we merge each element of the list to its left partner
            List<Release> subList = record.getReleases().subList(1, record.getReleases().size());
            for (Release right : subList) {
                Release compiled = mergeOcdsBeans(left, right);
                left = compiled;
            }
        }

        // this was purposefully nullified by ocdsOmit
        left.setTag(new ArrayList<Tag>());

        left.getTag().add(Tag.compiled);

        return left;
    }

    public void createSaveCompiledReleaseAndSaveRecord(final Record record) {
        Release compiledRelease = createCompiledRelease(record);
        record.setCompiledRelease(releaseRepository.save(compiledRelease));
        recordRepository.save(record);
    }

}
