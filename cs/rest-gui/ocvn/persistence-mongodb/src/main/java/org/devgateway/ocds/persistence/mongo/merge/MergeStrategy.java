package org.devgateway.ocds.persistence.mongo.merge;

/**
 *
 * Defined merge strategies in OCDS
 * <p>
 * <a href="https://github.com/open-contracting/jsonmerge">jsonmerge</a>
 * <p>
 * <a href="http://standard.open-contracting.org/latest/en/schema/merging/">OCDS Merging</a>
 * <p>
 *
 * @see Merge
 *
 * @author mpostelnicu
 *
 */
public enum MergeStrategy {
    /**
     * Overwrite with the value in base with value in head. Works with any type.
     */
    overwrite,

    /**
     * Append arrays. Works only with arrays.
     */
    @Deprecated
    append,

    /**
     * Merge arrays, identifying items to be merged by an ID field. Resulting
     * arrays have items from both base and head arrays. Any items that have
     * identical an ID are merged based on the strategy specified further down
     * in the hierarchy.
     * <p>
     * By default, array items are expected to be objects and ID of the item is
     * obtained from the id property of the object.
     * <p>
     * You can specify an arbitrary JSON pointer to point to the ID of the item
     * using the idRef merge option. When resolving the pointer, document root
     * is placed at the root of the array item (e.g. by default, idRef is '/id')
     * <p>
     * Array items in head for which the ID cannot be identified (e.g. idRef
     * pointer is invalid) are ignored.
     * <p>
     * You can specify an additional item ID to be ignored using the ignoreId
     * merge option.
     * <p>
     * OCDS: The arrayMergeById applies to the following lists of objects within
     * the release:
     * <p>
     * awards contracts items documents transactions milestones Each of these
     * objects has a required id field on it. When the merge is being performed,
     * the item with the corresponding id is looked up for the before and after
     * versions of the release and the fields are then matched accordingly.
     * <p>
     * If a given entry is omitted (e.g. there is no information about a
     * particular contract in a subsequent release), then the previous values
     * carry forward.
     * <p>
     * To remove an entry it would have to have its field values set to null, as
     * per the guidance on emptying fields and values.
     */
    arrayMergeById,

    /**
     * Merge objects. Resulting objects have properties from both base and head.
     * Any properties that are present both in base and head are merged based on
     * the strategy specified further down in the hierarchy (e.g. in properties,
     * patternProperties or additionalProperties schema keywords).
     */
    @Deprecated
    objectMerge,

    /**
     * Changes the type of the value to an array. New values are appended to the
     * array in the form of an object with a value property. This way all values
     * seen during the merge are preserved.
     * <p>
     * You can limit the length of the list using the limit option in the
     * mergeOptions keyword.
     * <p>
     * By default, if a head document contains the same value as the base,
     * document, no new version will be appended. You can change this by setting
     * ignoreDups option to false.
     */
    @Deprecated
    version,

    /**
     * Most fields have the mergeStrategy ocdsVersion. The ocdsVersion strategy
     * has two modes of operation:
     * <p>
     * <ul>
     * <li>when making a compiled record, the field is overridden with the
     * latest</li>
     * <li>value when making a versioned record, the field history is
     * documented.</li>
     * </ul>
     * <p>
     * The ocdsVersion strategy also applies to the following lists:
     * <p>
     * <ul>
     * <li>Award.suppliers</li>
     * <li>Organization.additionalIdentifiers</li>
     * <li>Item.additionalClassifications</li>
     * <li>Amendment.changes</li>
     * </ul>
     * <p>
     * In this instance the entire list is treated as one single value and any
     * change to any field will result in the whole list being updated and
     * documented as changed.
     * <p>
     *
     * To keep the versioning as clean as possible, the list of objects should
     * always be given in the same order in each release, so as not to
     * mistakenly mark a change when actually only order has shifted.
     * <p>
     * This merging strategy has the advantage of not requiring unique
     * identifiers on every object, but has the downside of requiring every
     * release to publish the whole block of data, not just an incremental
     * change.
     */
    ocdsVersion,

    /**
     * There are a number of fields marked with the strategy ocdsOmit.
     * <p>
     * This strategy returns nothing on merge, because to update the field
     * wouldn't make sense.
     * <p>
     * For example, the field for tag should not be updated to the latest
     * version, it should be updated to compiled for it to make sense.
     */
    ocdsOmit
}