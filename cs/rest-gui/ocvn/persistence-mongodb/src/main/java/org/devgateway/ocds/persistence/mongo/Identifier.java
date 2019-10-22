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
 *  Identifier OCDS entity http://standard.open-contracting.org/latest/en/schema/reference/#identifier
 */
@JsonPropertyOrder({
        "scheme",
        "id",
        "legalName",
        "uri"
})
public class Identifier {

    /**
     * Organization identifiers be drawn from an existing identification scheme.
     * This field is used to indicate the scheme or codelist in which the identifier will be found.
     * This value should be drawn from the
     * [Organization Identifier Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#organization-identifier-scheme).
     *
     */
    @JsonProperty("scheme")
    @Merge(MergeStrategy.ocdsVersion)
    private String scheme;

    /**
     * The identifier of the organization in the selected scheme.
     *
     */
    @JsonProperty("id")
    @Merge(MergeStrategy.ocdsVersion)
    private String id;

    /**
     * The legally registered name of the organization.
     *
     */
    @JsonProperty("legalName")
    @Merge(MergeStrategy.ocdsVersion)
    private String legalName;

    /**
     * A URI to identify the organization, such as those provided by
     * [Open Corporates](http://www.opencorporates.com) or some other relevant URI provider.
     * This is not for listing the website of the organization: t
     * hat can be done through the url field of the Organization contact point.
     *
     */
    @JsonProperty("uri")
    @Merge(MergeStrategy.ocdsVersion)
    private String uri;

    /**
     * Organization identifiers be drawn from an existing identification scheme.
     * This field is used to indicate the scheme or codelist in which the identifier will be found.
     * This value should be drawn from the
     * [Organization Identifier Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#organization-identifier-scheme).
     *
     * @return
     *     The scheme
     */
    @JsonProperty("scheme")
    public String getScheme() {
        return scheme;
    }

    /**
     * Organization identifiers be drawn from an existing identification scheme.
     * This field is used to indicate the scheme or codelist in which the identifier will be found.
     * This value should be drawn from the
     * [Organization Identifier Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#organization-identifier-scheme).
     *
     * @param scheme
     *     The scheme
     */
    @JsonProperty("scheme")
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * The identifier of the organization in the selected scheme.
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * The identifier of the organization in the selected scheme.
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * The legally registered name of the organization.
     *
     * @return
     *     The legalName
     */
    @JsonProperty("legalName")
    public String getLegalName() {
        return legalName;
    }

    /**
     * The legally registered name of the organization.
     *
     * @param legalName
     *     The legalName
     */
    @JsonProperty("legalName")
    public void setLegalName(final String legalName) {
        this.legalName = legalName;
    }

    /**
     * A URI to identify the organization, such as those provided by
     * [Open Corporates](http://www.opencorporates.com) or some other relevant URI provider.
     * This is not for listing the website of the organization:
     * that can be done through the url field of the Organization contact point.
     *
     * @return
     *     The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * A URI to identify the organization, such as those provided by
     * [Open Corporates](http://www.opencorporates.com) or some other relevant URI provider.
     * This is not for listing the website of the organization:
     * that can be done through the url field of the Organization contact point.
     *
     * @param uri
     *     The uri
     */
    @JsonProperty("uri")
    public void setUri(final String uri) {
        this.uri = uri;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(scheme).
                append(id).
                append(legalName).
                append(uri).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Identifier)) {
            return false;
        }
        Identifier rhs = ((Identifier) other);
        return new EqualsBuilder().
                append(scheme, rhs.scheme).
                append(id, rhs.id).
                append(legalName, rhs.legalName).
                append(uri, rhs.uri).
                isEquals();
    }

}
