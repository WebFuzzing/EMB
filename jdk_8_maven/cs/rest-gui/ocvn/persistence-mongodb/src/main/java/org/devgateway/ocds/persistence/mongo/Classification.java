package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * Classification OCDS Entity http://standard.open-contracting.org/latest/en/schema/reference/#classification
 */
@JsonPropertyOrder({
        "scheme",
        "id",
        "description",
        "uri"
})
@Document
public class Classification implements Identifiable {
    /**
     * An classification should be drawn from an existing scheme or list of codes.
     * This field is used to indicate the scheme/codelist from which the classification is drawn.
     * For line item classifications, this value should represent an known
     * [Item Classification Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#item-classification-scheme)
     *  wherever possible.
     *
     */
    @JsonProperty("scheme")
    @Merge(MergeStrategy.ocdsVersion)
    private String scheme;

    /**
     * The classification code drawn from the selected scheme.
     *
     */
    @ExcelExport
    @JsonProperty("id")
    @Id
    @Merge(MergeStrategy.ocdsVersion)
    private String id;

    /**
     * A textual description or title for the code.
     *
     */
    @ExcelExport
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    /**
     * A URI to identify the code. In the event individual URIs are not available for items in the identifier scheme
     * this value should be left blank.
     *
     */
    @JsonProperty("uri")
    @Merge(MergeStrategy.ocdsVersion)
    private String uri;

    /**
     * An classification should be drawn from an existing scheme or list of codes.
     * This field is used to indicate the scheme/codelist from which the classification is drawn.
     * For line item classifications, this value should represent an known
     * [Item Classification Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#item-classification-scheme)
     *  wherever possible.
     *
     * @return
     *     The scheme
     */
    @JsonProperty("scheme")
    public String getScheme() {
        return scheme;
    }

    /**
     * An classification should be drawn from an existing scheme or list of codes.
     * This field is used to indicate the scheme/codelist from which the classification is drawn.
     * For line item classifications, this value should represent an known
     * [Item Classification Scheme]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#item-classification-scheme)
     *  wherever possible.
     *
     * @param scheme
     *     The scheme
     */
    @JsonProperty("scheme")
    public void setScheme(final String scheme) {
        this.scheme = scheme;
    }

    /**
     * The classification code drawn from the selected scheme.
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * The classification code drawn from the selected scheme.
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * A textual description or title for the code.
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * A textual description or title for the code.
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * A URI to identify the code. In the event individual URIs are not available for items in the identifier scheme
     * this value should be left blank.
     *
     * @return
     *     The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * A URI to identify the code. In the event individual URIs are not available for items in the identifier scheme
     * this value should be left blank.
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
                append(description).
                append(uri).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Classification)) {
            return false;
        }
        Classification rhs = ((Classification) other);
        return new EqualsBuilder().
                append(scheme, rhs.scheme).
                append(id, rhs.id).
                append(description, rhs.description).
                append(uri, rhs.uri).isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return id;
    }
}
