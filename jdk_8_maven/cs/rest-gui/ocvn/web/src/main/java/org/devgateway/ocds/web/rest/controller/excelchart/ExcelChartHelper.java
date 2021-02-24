package org.devgateway.ocds.web.rest.controller.excelchart;

import com.mongodb.DBObject;
import org.apache.log4j.Logger;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author idobre
 * @since 8/18/16
 */
@Service
@CacheConfig(keyGenerator = "genericExcelChartKeyGenerator", cacheNames = "excelChartHelper")
public class ExcelChartHelper {
    private static Logger logger = Logger.getLogger(ExcelChartHelper.class);

    /**
     * Collects categories from a List of DBObjects.
     *
     * @param catKey   - key that represents the categories
     * @param lists - multiple DBObject lists from who we will extract categories
     * @return
     */
    @Cacheable
    public List<?> getCategoriesFromDBObject(final String catKey, final List<DBObject>... lists) {
        final List<Object> categoriesWithDuplicates = new ArrayList<>();
        for (List<DBObject> list : lists) {
            list.parallelStream()
                    .filter(item -> item.toMap().get(catKey) != null)
                    .forEach(item -> categoriesWithDuplicates.add(item.toMap().get(catKey)));
        }

        // sort and keep only the unique categories
        // keep in mind that we can have different number of categories from each source
        // (example different number of years)
        return categoriesWithDuplicates
                .parallelStream()
                .sorted()
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * Collects values for each category from a List of DBObjects.
     * If the category doesn't exist then we add the null value (we will have an empty cell in excel file).
     */
    @Cacheable
    public List<Number> getValuesFromDBObject(final List<DBObject> list, final List<?> categories,
                                              final String catKey, final String valKey) {
        final List<Number> values = new ArrayList<>();

        categories.forEach(cat -> {
            // check if the category 'cat' is present in the list of DBObjects and extract the value
            Optional<DBObject> result = list.parallelStream().filter(
                    val -> val.toMap().get(catKey) != null && val.toMap().get(catKey).equals(cat)
            ).findFirst();
            if (result.isPresent()) {
                values.add((Number) result.get().toMap().get(valKey));
            } else {
                values.add(null);
            }
        });

        return values;
    }
}
