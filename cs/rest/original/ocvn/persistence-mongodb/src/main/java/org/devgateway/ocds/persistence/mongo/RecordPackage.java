
package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Schema for an Open Contracting Record package
 * <p>
 * The record package contains a list of records along with some publishing meta data.
 * The records pull together all the releases under a single Open Contracting ID and compile them
 * into the latest version of the information along with the history of any data changes.
 *
 */
@JsonPropertyOrder({
        "uri",
        "publisher",
        "license",
        "publicationPolicy",
        "publishedDate",
        "packages",
        "records"
})
@Document
public class RecordPackage implements Identifiable {


    /**
     * Package Identifier
     * <p>
     * The String of this package that identifies it uniquely in the world.
     * (Required)
     *
     */
    @JsonProperty("uri")
    @Id
    private String uri;

    /**
     * Information to uniquely identify the publisher of this package.
     * (Required)
     *
     */
    @JsonProperty("publisher")
    private Publisher publisher;

    /**
     * A link to the license that applies to the data in this datapackage. [Open
     * Definition Conformant](http://opendefinition.org/licenses/) licenses are
     * strongly recommended. The canonical String of the license should be used.
     * Documents linked from this file may be under other license conditions.
     *
     */
    @JsonProperty("license")
    private String license;

    /**
     * A link to a document describing the publishers publication policy.
     *
     */
    @JsonProperty("publicationPolicy")
    private String publicationPolicy;

    /**
     * The date that this package was published. (Required)
     *
     */
    @JsonProperty("publishedDate")
    private Date publishedDate;

    /**
     * A list of Strings of all the release packages that were used to create
     * this record package. (Required)
     *
     */
    @JsonProperty("packages")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    private Set<String> packages = new LinkedHashSet<String>();

    /**
     * The records for this data package. (Required)
     *
     */
    @JsonProperty("records")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @DBRef
    private Set<Record> records = new LinkedHashSet<Record>();

    /**
     * Package Identifier
     * <p>
     * The String of this package that identifies it uniquely in the world.
     * (Required)
     *
     * @return The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * Package Identifier
     * <p>
     * The String of this package that identifies it uniquely in the world.
     * (Required)
     *
     * @param uri
     *            The uri
     */
    @JsonProperty("uri")
    public void setUri(final String uri) {
        this.uri = uri;
    }

    /**
     * Information to uniquely identify the publisher of this package.
     * (Required)
     *
     * @return The publisher
     */
    @JsonProperty("publisher")
    public Publisher getPublisher() {
        return publisher;
    }

    /**
     * Information to uniquely identify the publisher of this package.
     * (Required)
     *
     * @param publisher
     *            The publisher
     */
    @JsonProperty("publisher")
    public void setPublisher(final Publisher publisher) {
        this.publisher = publisher;
    }

    /**
     * A link to the license that applies to the data in this datapackage. [Open
     * Definition Conformant](http://opendefinition.org/licenses/) licenses are
     * strongly recommended. The canonical String of the license should be used.
     * Documents linked from this file may be under other license conditions.
     *
     * @return The license
     */
    @JsonProperty("license")
    public String getLicense() {
        return license;
    }

    /**
     * A link to the license that applies to the data in this datapackage. [Open
     * Definition Conformant](http://opendefinition.org/licenses/) licenses are
     * strongly recommended. The canonical String of the license should be used.
     * Documents linked from this file may be under other license conditions.
     *
     * @param license
     *            The license
     */
    @JsonProperty("license")
    public void setLicense(final String license) {
        this.license = license;
    }

    /**
     * A link to a document describing the publishers publication policy.
     *
     * @return The publicationPolicy
     */
    @JsonProperty("publicationPolicy")
    public String getPublicationPolicy() {
        return publicationPolicy;
    }

    /**
     * A link to a document describing the publishers publication policy.
     *
     * @param publicationPolicy
     *            The publicationPolicy
     */
    @JsonProperty("publicationPolicy")
    public void setPublicationPolicy(final String publicationPolicy) {
        this.publicationPolicy = publicationPolicy;
    }

    /**
     * The date that this package was published. (Required)
     *
     * @return The publishedDate
     */
    @JsonProperty("publishedDate")
    public Date getPublishedDate() {
        return publishedDate;
    }

    /**
     * The date that this package was published. (Required)
     *
     * @param publishedDate
     *            The publishedDate
     */
    @JsonProperty("publishedDate")
    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     * A list of Strings of all the release packages that were used to create
     * this record package. (Required)
     *
     * @return The packages
     */
    @JsonProperty("packages")
    public Set<String> getPackages() {
        return packages;
    }

    /**
     * A list of Strings of all the release packages that were used to create
     * this record package. (Required)
     *
     * @param packages
     *            The packages
     */
    @JsonProperty("packages")
    public void setPackages(final Set<String> packages) {
        this.packages = packages;
    }

    /**
     * The records for this data package. (Required)
     *
     * @return The records
     */
    @JsonProperty("records")
    public Set<Record> getRecords() {
        return records;
    }

    /**
     * The records for this data package. (Required)
     *
     * @param records
     *            The records
     */
    @JsonProperty("records")
    public void setRecords(final Set<Record> records) {
        this.records = records;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(uri).
                append(publisher).
                append(license).
                append(publicationPolicy).
                append(publishedDate).
                append(packages).
                append(records).toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof RecordPackage)) {
            return false;
        }
        RecordPackage rhs = ((RecordPackage) other);
        return new EqualsBuilder().
                append(uri, rhs.uri).
                append(publisher, rhs.publisher).
                append(license, rhs.license).
                append(publicationPolicy, rhs.publicationPolicy).
                append(publishedDate, rhs.publishedDate).
                append(packages, rhs.packages).
                append(records, rhs.records).isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return uri;
    }

}
