package org.devgateway.ocds.persistence.mongo;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * An address. This may be the legally registered address of the organization,
 * or may be a correspondence address for this particular contracting process.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#address
 *
 */
@JsonPropertyOrder({
        "streetAddress",
        "locality",
        "region",
        "postalCode",
        "countryName"
})
public class Address {

    /**
     * The street address. For example, 1600 Amphitheatre Pkwy.
     *
     */
    @ExcelExport
    @JsonProperty("streetAddress")
    @Merge(MergeStrategy.ocdsVersion)
    private String streetAddress;

    /**
     * The locality. For example, Mountain View.
     *
     */
    @JsonProperty("locality")
    @Merge(MergeStrategy.ocdsVersion)
    private String locality;

    /**
     * The region. For example, CA.
     *
     */
    @JsonProperty("region")
    @Merge(MergeStrategy.ocdsVersion)
    private String region;

    /**
     * The postal code. For example, 94043.
     *
     */
    @JsonProperty("postalCode")
    @Merge(MergeStrategy.ocdsVersion)
    private String postalCode;

    /**
     * The country name. For example, United States.
     *
     */
    @JsonProperty("countryName")
    @Merge(MergeStrategy.ocdsVersion)
    private String countryName;

    /**
     * The street address. For example, 1600 Amphitheatre Pkwy.
     *
     * @return
     *     The streetAddress
     */
    @JsonProperty("streetAddress")
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * The street address. For example, 1600 Amphitheatre Pkwy.
     *
     * @param streetAddress
     *     The streetAddress
     */
    @JsonProperty("streetAddress")
    public void setStreetAddress(final String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * The locality. For example, Mountain View.
     *
     * @return
     *     The locality
     */
    @JsonProperty("locality")
    public String getLocality() {
        return locality;
    }

    /**
     * The locality. For example, Mountain View.
     *
     * @param locality
     *     The locality
     */
    @JsonProperty("locality")
    public void setLocality(final String locality) {
        this.locality = locality;
    }

    /**
     * The region. For example, CA.
     *
     * @return
     *     The region
     */
    @JsonProperty("region")
    public String getRegion() {
        return region;
    }

    /**
     * The region. For example, CA.
     *
     * @param region
     *     The region
     */
    @JsonProperty("region")
    public void setRegion(final String region) {
        this.region = region;
    }

    /**
     * The postal code. For example, 94043.
     *
     * @return
     *     The postalCode
     */
    @JsonProperty("postalCode")
    public String getPostalCode() {
        return postalCode;
    }

    /**
     * The postal code. For example, 94043.
     *
     * @param postalCode
     *     The postalCode
     */
    @JsonProperty("postalCode")
    public void setPostalCode(final String postalCode) {
        this.postalCode = postalCode;
    }

    /**
     * The country name. For example, United States.
     *
     * @return
     *     The countryName
     */
    @JsonProperty("countryName")
    public String getCountryName() {
        return countryName;
    }

    /**
     * The country name. For example, United States.
     *
     * @param countryName
     *     The countryName
     */
    @JsonProperty("countryName")
    public void setCountryName(final String countryName) {
        this.countryName = countryName;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(streetAddress).
                append(locality).
                append(region).
                append(postalCode).
                append(countryName).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Address)) {
            return false;
        }
        Address rhs = ((Address) other);
        return new EqualsBuilder().
                append(streetAddress, rhs.streetAddress).
                append(locality, rhs.locality).
                append(region, rhs.region).
                append(postalCode, rhs.postalCode).
                append(countryName, rhs.countryName).
                isEquals();
    }

}

