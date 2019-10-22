
package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * http://standard.open-contracting.org/latest/en/schema/reference/#changes
 */
@JsonPropertyOrder({
        "property",
        "former_value"
})
public class Change {

    /**
     * The property name that has been changed relative to the place the amendment is.
     * For example if the contract value has changed, then the property under changes within
     * the contract.amendment would be value.amount.
     *
     */
    @JsonProperty("property")
    private String property;

    /**
     * The previous value of the changed property, in whatever type the property is.
     *
     */
    @JsonProperty("former_value")
    private String formerValue;

    /**
     * The property name that has been changed relative to the place the amendment is.
     * For example if the contract value has changed, then the property under changes within
     * the contract.amendment would be value.amount.
     *
     * @return
     *     The property
     */
    @JsonProperty("property")
    public String getProperty() {
        return property;
    }

    /**
     * The property name that has been changed relative to the place the amendment is.
     * For example if the contract value has changed, then the property under changes within
     * the contract.amendment would be value.amount.
     *
     * @param property
     *     The property
     */
    @JsonProperty("property")
    public void setProperty(final String property) {
        this.property = property;
    }

    /**
     * The previous value of the changed property, in whatever type the property is.
     *
     * @return
     *     The formerValue
     */
    @JsonProperty("former_value")
    public String getFormerValue() {
        return formerValue;
    }

    /**
     * The previous value of the changed property, in whatever type the property is.
     *
     * @param formerValue
     *     The former_value
     */
    @JsonProperty("former_value")
    public void setFormerValue(final String formerValue) {
        this.formerValue = formerValue;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(property).
                append(formerValue).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Change)) {
            return false;
        }
        Change rhs = ((Change) other);
        return new EqualsBuilder().
                append(property, rhs.property).
                append(formerValue, rhs.formerValue).
                isEquals();
    }

}
