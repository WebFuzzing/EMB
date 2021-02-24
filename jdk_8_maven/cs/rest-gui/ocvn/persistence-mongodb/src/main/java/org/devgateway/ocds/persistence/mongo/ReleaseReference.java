
package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Information to uniquely identify the release.
 *
 */
@JsonPropertyOrder({
        "url",
        "date",
        "tag"
})
public class ReleaseReference {

    /**
     * The url of the release which contains the url of the package with the releaseID appended using a fragment
     * identifier e.g. http://ocds.open-contracting.org/demos/releases/12345.json#ocds-a2ef3d01-1594121/1
     * (Required)
     *
     */
    @JsonProperty("url")
    private String url;

    /**
     * Release Date
     * <p>
     * The date of the release, should match `date` at the root level of the release.
     * This is used to sort the releases in the list into date order.
     * (Required)
     *
     */
    @JsonProperty("date")
    private Date date;

    /**
     * Release Tag
     * <p>
     * The tag should match the tag in the release. This provides additional context when reviewing a record
     * to see what types of releases are included for this ocid.
     *
     */
    @JsonProperty("tag")
    private List<Tag> tag = new ArrayList<Tag>();

    /**
     * The url of the release which contains the url of the package with the releaseID appended using a fragment
     * identifier e.g. http://ocds.open-contracting.org/demos/releases/12345.json#ocds-a2ef3d01-1594121/1
     * (Required)
     *
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * The url of the release which contains the url of the package with the releaseID appended using a fragment
     * identifier e.g. http://ocds.open-contracting.org/demos/releases/12345.json#ocds-a2ef3d01-1594121/1
     * (Required)
     *
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(final String url) {
        this.url = url;
    }

    /**
     * Release Date
     * <p>
     * The date of the release, should match `date` at the root level of the release.
     * This is used to sort the releases in the list into date order.
     * (Required)
     *
     * @return
     *     The date
     */
    @JsonProperty("date")
    public Date getDate() {
        return date;
    }

    /**
     * Release Date
     * <p>
     * The date of the release, should match `date` at the root level of the release.
     * This is used to sort the releases in the list into date order.
     * (Required)
     *
     * @param date
     *     The date
     */
    @JsonProperty("date")
    public void setDate(final Date date) {
        this.date = date;
    }

    /**
     * Release Tag
     * <p>
     * The tag should match the tag in the release. This provides additional context when reviewing a record
     * to see what types of releases are included for this ocid.
     *
     * @return
     *     The tag
     */
    @JsonProperty("tag")
    public List<Tag> getTag() {
        return tag;
    }

    /**
     * Release Tag
     * <p>
     * The tag should match the tag in the release. This provides additional context when reviewing a record
     * to see what types of releases are included for this ocid.
     *
     * @param tag
     *     The tag
     */
    @JsonProperty("tag")
    public void setTag(final List<Tag> tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(url).
                append(date).
                append(tag).toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ReleaseReference)) {
            return false;
        }
        ReleaseReference rhs = ((ReleaseReference) other);
        return new EqualsBuilder().
                append(url, rhs.url).
                append(date, rhs.date).
                append(tag, rhs.tag).isEquals();
    }

}
