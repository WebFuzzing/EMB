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
 * Schema for an Open Contracting Release Package
 * <p>
 * Note that all releases within a release package must have a unique releaseID
 * within this release package.
 *
 */
@JsonPropertyOrder({
        "uri",
        "publishedDate",
        "releases",
        "publisher",
        "license",
        "publicationPolicy"
})
@Document
public class ReleasePackage implements Identifiable {


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
     * The date that this package was published. Ideally this should be the
     * latest date that there is release information in this package. (Required)
     *
     */
    @JsonProperty("publishedDate")
    private Date publishedDate;

    /**
     *
     * (Required)
     *
     */
    @JsonProperty("releases")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @DBRef
    private Set<Release> releases = new LinkedHashSet<Release>();

    /**
     * Information to uniquely identify the publisher of this package.
     * (Required)
     *
     */
    @JsonProperty("publisher")
    private Publisher publisher;

    /**
     * A link to the license that applies to the data in this datapackage. A
     * Public Domain Dedication or [Open Definition
     * Conformant](http://opendefinition.org/licenses/) license is strongly
     * recommended. The canonical String of the license should be used.
     * Documents linked from this file may be under other license conditions.
     *
     */
    @JsonProperty("license")
    private String license;

    /**
     * A link to a document describing the publishers [publication
     * policy](http://ocds.open-contracting.org/standard/r/1__0__0/en/
     * implementation/publication_patterns/#publication-policy).
     *
     */
    @JsonProperty("publicationPolicy")
    private String publicationPolicy;

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
     * The date that this package was published. Ideally this should be the
     * latest date that there is release information in this package. (Required)
     *
     * @return The publishedDate
     */
    @JsonProperty("publishedDate")
    public Date getPublishedDate() {
        return publishedDate;
    }

    /**
     * The date that this package was published. Ideally this should be the
     * latest date that there is release information in this package. (Required)
     *
     * @param publishedDate
     *            The publishedDate
     */
    @JsonProperty("publishedDate")
    public void setPublishedDate(final Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    /**
     *
     * (Required)
     *
     * @return The releases
     */
    @JsonProperty("releases")
    public Set<Release> getReleases() {
        return releases;
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
     *
     * (Required)
     *
     * @param releases
     *            The releases
     */
    @JsonProperty("releases")
    public void setReleases(final Set<Release> releases) {
        this.releases = releases;
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
     * A link to the license that applies to the data in this datapackage. A
     * Public Domain Dedication or [Open Definition
     * Conformant](http://opendefinition.org/licenses/) license is strongly
     * recommended. The canonical String of the license should be used.
     * Documents linked from this file may be under other license conditions.
     *
     * @return The license
     */
    @JsonProperty("license")
    public String getLicense() {
        return license;
    }

    /**
     * A link to the license that applies to the data in this datapackage. A
     * Public Domain Dedication or [Open Definition
     * Conformant](http://opendefinition.org/licenses/) license is strongly
     * recommended. The canonical String of the license should be used.
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
     * A link to a document describing the publishers [publication
     * policy](http://ocds.open-contracting.org/standard/r/1__0__0/en/
     * implementation/publication_patterns/#publication-policy).
     *
     * @return The publicationPolicy
     */
    @JsonProperty("publicationPolicy")
    public String getPublicationPolicy() {
        return publicationPolicy;
    }

    /**
     * A link to a document describing the publishers [publication
     * policy](http://ocds.open-contracting.org/standard/r/1__0__0/en/
     * implementation/publication_patterns/#publication-policy).
     *
     * @param publicationPolicy
     *            The publicationPolicy
     */
    @JsonProperty("publicationPolicy")
    public void setPublicationPolicy(final String publicationPolicy) {
        this.publicationPolicy = publicationPolicy;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(uri).
                append(publishedDate).
                append(releases).
                append(publisher).
                append(license).
                append(publicationPolicy).toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReleasePackage)) {
            return false;
        }
        ReleasePackage rhs = ((ReleasePackage) other);
        return new EqualsBuilder().
                append(uri, rhs.uri).
                append(publishedDate, rhs.publishedDate).
                append(releases, rhs.releases).
                append(publisher, rhs.publisher).
                append(license, rhs.license).
                append(publicationPolicy, rhs.publicationPolicy).isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return uri;
    }

}
