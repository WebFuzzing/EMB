
package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;


/**
 * Description of the unit which the good comes in e.g. hours, kilograms.
 * Made up of a unit name, and the value of a single unit.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#unit
 */
@JsonPropertyOrder({
        "name",
        "value"
})
public class Unit {

    /**
     * Name of the unit
     *
     */
    @JsonProperty("name")
    @Merge(MergeStrategy.ocdsVersion)
    private String name;

    @JsonProperty("value")
    private Amount value;

    /**
     * Name of the unit
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * Name of the unit
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(final String name) {
        this.name = name;
    }

    /**
     *
     * @return
     *     The value
     */
    @JsonProperty("value")
    public Amount getValue() {
        return value;
    }

    /**
     *
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(final Amount value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(name).
                append(value).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Unit)) {
            return false;
        }
        Unit rhs = ((Unit) other);
        return new EqualsBuilder().
                append(name, rhs.name).
                append(value, rhs.value).
                isEquals();
    }

}
