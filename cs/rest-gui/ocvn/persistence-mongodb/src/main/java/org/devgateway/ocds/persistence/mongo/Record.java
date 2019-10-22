
package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({
        "ocid",
        "releases",
        "compiledRelease"
})
@Document
public class Record implements Identifiable {

    /**
     * Open Contracting ID
     * <p>
     * A unique identifier that identifies the unique Open Contracting Process.
     * For more information see:
     * http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/
     * definitions/#contracting-process (Required)
     *
     */
    @JsonProperty("ocid")
    @Id
    private String ocid;
    /**
     * Linked releases
     * <p>
     * A list of objects that identify the releases associated with this Open
     * Contracting ID. The releases MUST be sorted into date order in the array,
     * from oldest (at position 0) to newest (last). (Required)
     *
     */
    @JsonProperty("releases")
    private List<Release> releases = new ArrayList<Release>();

    /**
     * this NOT in the OCDS standard, but the standard uses "oneOf" which is
     * poorly supported presently
     *
     * @see https://github.com/joelittlejohn/jsonschema2pojo/wiki/Proposal-for-
     *      allOf,-anyOf-and-oneOf
     */

    private List<ReleaseReference> releaseReferences = new ArrayList<ReleaseReference>();

    /**
     * Schema for an Open Contracting Release
     * <p>
     *
     *
     */
    @JsonProperty("compiledRelease")
    @DBRef
    private Release compiledRelease;

    /**
     * Open Contracting ID
     * <p>
     * A unique identifier that identifies the unique Open Contracting Process.
     * For more information see:
     * http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/
     * definitions/#contracting-process (Required)
     *
     * @return The ocid
     */
    @JsonProperty("ocid")
    public String getOcid() {
        return ocid;
    }

    /**
     * Open Contracting ID
     * <p>
     * A unique identifier that identifies the unique Open Contracting Process.
     * For more information see:
     * http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/
     * definitions/#contracting-process (Required)
     *
     * @param ocid
     *            The ocid
     */
    @JsonProperty("ocid")
    public void setOcid(final String ocid) {
        this.ocid = ocid;
    }


    @JsonProperty("releaseReferences")
    public List<ReleaseReference> getReleaseReferences() {
        return releaseReferences;
    }

    @JsonProperty("releaseReferences")
    public void setReleaseReferences(final List<ReleaseReference> releaseReferences) {
        this.releaseReferences = releaseReferences;
    }

    /**
     * Linked releases
     * <p>
     * A list of objects that identify the releases associated with this Open
     * Contracting ID. The releases MUST be sorted into date order in the array,
     * from oldest (at position 0) to newest (last). (Required)
     *
     * @return The releases
     */
    @JsonProperty("releases")
    public List<Release> getReleases() {
        return releases;
    }

    /**
     * Linked releases
     * <p>
     * A list of objects that identify the releases associated with this Open
     * Contracting ID. The releases MUST be sorted into date order in the array,
     * from oldest (at position 0) to newest (last). (Required)
     *
     * @param releases
     *            The releases
     */
    @JsonProperty("releases")
    public void setReleases(final List<Release> releases) {
        this.releases = releases;
    }

    /**
     * Schema for an Open Contracting Release
     * <p>
     *
     *
     * @return The compiledRelease
     */
    @JsonProperty("compiledRelease")
    public Release getCompiledRelease() {
        return compiledRelease;
    }

    /**
     * Schema for an Open Contracting Release
     * <p>
     *
     *
     * @param compiledRelease
     *            The compiledRelease
     */
    @JsonProperty("compiledRelease")
    public void setCompiledRelease(final Release compiledRelease) {
        this.compiledRelease = compiledRelease;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(ocid).
                append(releases).
                append(compiledRelease).toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Record)) {
            return false;
        }
        Record rhs = ((Record) other);
        return new EqualsBuilder().
                append(ocid, rhs.ocid).
                append(releases, rhs.releases).
                append(compiledRelease, rhs.compiledRelease).isEquals();
    }

    @Override
    public Serializable getIdProperty() {
        return ocid;
    }

}
