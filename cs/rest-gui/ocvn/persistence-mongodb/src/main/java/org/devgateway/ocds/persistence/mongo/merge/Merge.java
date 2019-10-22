package org.devgateway.ocds.persistence.mongo.merge;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Applied JSON merge strategy for OCDS fields. This is defined in OCDS schema
 * as extra field Metadata, with property names: mergeStrategy and mergeOptions.
 * <p>
 * More details on the official Python tool that performs the JSON merge
 * <p>
 * <a href="https://github.com/open-contracting/jsonmerge">jsonmerge</a>
 * <p>
 * <a href="http://standard.open-contracting.org/latest/en/schema/merging/">OCDS Merging</a>
 * <p>
 *
 * @see MergeStrategy
 *
 * @author mpostelnicu
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Merge {

    /**
     * Optional merge options.
     * One example is to specify the id reference in case the selected strategy
     * is {@link MergeStrategy#arrayMergeById}. Example: mergeOptions={"idref","id"}
     * jsonmerge says this is the default behavior, to assume the idref is "id",
     * so we have implement this as the default
     *
     * @return
     */
    String[] mergeOptions() default "";


    /**
     * The applied merge strategy
     * @see MergeStrategy
     *
     * @return
     */
    MergeStrategy value();
}
