package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Organization
 * <p>
 * An organization.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#organization
 *
 */
@JsonPropertyOrder({
        "identifier",
        "additionalIdentifiers",
        "name",
        "address",
        "contactPoint",
        "roles"
})
@Document
public class Organization implements Identifiable {
    @Id
    private String id;

    @JsonProperty("roles")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<OrganizationType> roles = new LinkedHashSet<OrganizationType>();

    @ExcelExport
    @JsonProperty("identifier")
    private Identifier identifier;
    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     */
    @JsonProperty("additionalIdentifiers")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.ocdsVersion)
    private Set<Identifier> additionalIdentifiers = new LinkedHashSet<Identifier>();

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("name")
    private String name;

    /**
     * An address. This may be the legally registered address of the organization, or may be a correspondence address
     * for this particular contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("address")
    private Address address;

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("contactPoint")
    private ContactPoint contactPoint;

    public String getId() {
        return id;
    }

    /**
     *
     * @return
     *     The identifier
     */
    @JsonProperty("identifier")
    public Identifier getIdentifier() {
        return identifier;
    }

    public void setId(final String id) {
        this.id = id;
    }

    /**
     *
     * @param identifier
     *     The identifier
     */
    @JsonProperty("identifier")
    public void setIdentifier(final Identifier identifier) {
        this.identifier = identifier;
    }

    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     * @return
     *     The additionalIdentifiers
     */
    @JsonProperty("additionalIdentifiers")
    public Set<Identifier> getAdditionalIdentifiers() {
        return additionalIdentifiers;
    }

    /**
     * A list of additional / supplemental identifiers for the organization, using the
     * [organization identifier guidance]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/#organization-identifiers).
     *  This could be used to provide an internally used identifier for
     *  this organization in addition to the primary legal entity identifier.
     *
     * @param additionalIdentifiers
     *     The additionalIdentifiers
     */
    @JsonProperty("additionalIdentifiers")
    public void setAdditionalIdentifiers(final Set<Identifier> additionalIdentifiers) {
        this.additionalIdentifiers = additionalIdentifiers;
    }

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The common name of the organization. The ID property provides an space for the formal legal name,
     * and so this may either repeat that value, or could provide the common name by which this organization is known.
     * This field could also include details of the department or sub-unit involved in this contracting process.
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * An address. This may be the legally registered address of the organization,
     * or may be a correspondence address for this particular contracting process.
     *
     * @return
     *     The address
     */
    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    /**
     * An address. This may be the legally registered address of the organization,
     * or may be a correspondence address for this particular contracting process.
     *
     * @param address
     *     The address
     */
    @JsonProperty("address")
    public void setAddress(final Address address) {
        this.address = address;
    }

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     * @return
     *     The contactPoint
     */
    @JsonProperty("contactPoint")
    public ContactPoint getContactPoint() {
        return contactPoint;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(identifier).
                append(additionalIdentifiers).
                append(name).
                append(address).
                append(contactPoint).
                toHashCode();
    }

    /**
     * An person, contact point or department to contact in relation to this contracting process.
     *
     * @param contactPoint
     *     The contactPoint
     */
    @JsonProperty("contactPoint")
    public void setContactPoint(final ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Organization)) {
            return false;
        }
        Organization rhs = ((Organization) other);
        return new EqualsBuilder().
                append(identifier, rhs.identifier).
                append(additionalIdentifiers, rhs.additionalIdentifiers).
                append(name, rhs.name).
                append(address, rhs.address).
                append(contactPoint, rhs.contactPoint).
                isEquals();
    }


    public enum OrganizationType {
        procuringEntity("procuringEntity"),

        buyer("buyer"),

        supplier("supplier");

        private final String value;

        private static final Map<String, OrganizationType> CONSTANTS = new HashMap<String, OrganizationType>();

        static {
            for (OrganizationType c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        OrganizationType(final String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static OrganizationType fromValue(final String value) {
            OrganizationType constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }


    public Set<OrganizationType> getRoles() {
        return roles;
    }

    public void setRoles(final Set<OrganizationType> roles) {
        this.roles = roles;
    }

    @Override
    public Serializable getIdProperty() {
        return id;
    }



}

