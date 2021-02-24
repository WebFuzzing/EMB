package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

@JsonPropertyOrder({
        "scheme",
        "identifiers"
})
public class Gazetteer implements Serializable {

    private static final long serialVersionUID = -7480459687909560318L;

    /**
     * The entry of the selected gazetteer in the gazetteers codelist. The codelist provides details of services,
     * where available, that can resolve a gazetteer entry to provide location names.
     *
     */
    @JsonProperty("scheme")
    private String scheme;

    /**
     * An array of one or more codes drawn from the gazetteer indicated in scheme.
     *
     */
    @JsonProperty("identifiers")
    private Set<String> identifiers = new TreeSet<String>();

    /**
     * The entry of the selected gazetteer in the gazetteers codelist. The codelist provides details of services,
     * where available, that can resolve a gazetteer entry to provide location names.
     *
     * @return
     *     The scheme
     */
    @JsonProperty("scheme")
    public String getScheme() {
        return scheme;
    }

    /**
     * The entry of the selected gazetteer in the gazetteers codelist. The codelist provides details of services,
     * where available, that can resolve a gazetteer entry to provide location names.
     *
     * @param scheme
     *     The scheme
     */
    @JsonProperty("scheme")
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * An array of one or more codes drawn from the gazetteer indicated in scheme.
     *
     * @return
     *     The identifiers
     */
    @JsonProperty("identifiers")
    public Set<String> getIdentifiers() {
        return identifiers;
    }

    /**
     * An array of one or more codes drawn from the gazetteer indicated in scheme.
     *
     * @param identifiers
     *     The identifiers
     */
    @JsonProperty("identifiers")
    public void setIdentifiers(final Set<String> identifiers) {
        this.identifiers = identifiers;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(scheme).
                append(identifiers).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Gazetteer)) {
            return false;
        }
        Gazetteer rhs = ((Gazetteer) other);
        return new EqualsBuilder().
                append(scheme, rhs.scheme).
                append(identifiers, rhs.identifiers).
                isEquals();
    }

}