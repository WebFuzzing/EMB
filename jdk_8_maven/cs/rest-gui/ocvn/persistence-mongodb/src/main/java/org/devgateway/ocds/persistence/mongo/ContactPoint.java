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
 * An person, contact point or department to contact in relation to this contracting process.
 *
 *  http://standard.open-contracting.org/latest/en/schema/reference/#contactpoint
 */
@JsonPropertyOrder({
        "name",
        "email",
        "telephone",
        "faxNumber",
        "url"
})
public class ContactPoint {

    /**
     * The name of the contact person, department, or contact point,
     * for correspondence relating to this contracting process.
     *
     */
    @ExcelExport
    @JsonProperty("name")
    @Merge(MergeStrategy.ocdsVersion)
    private String name;

    /**
     * The e-mail address of the contact point/person.
     *
     */
    @ExcelExport
    @JsonProperty("email")
    @Merge(MergeStrategy.ocdsVersion)
    private String email;

    /**
     * The telephone number of the contact point/person. This should include the international dialling code.
     *
     */
    @ExcelExport
    @JsonProperty("telephone")
    @Merge(MergeStrategy.ocdsVersion)
    private String telephone;

    /**
     * The fax number of the contact point/person. This should include the international dialling code.
     *
     */
    @ExcelExport
    @JsonProperty("faxNumber")
    @Merge(MergeStrategy.ocdsVersion)
    private String faxNumber;

    /**
     * A web address for the contact point/person.
     *
     */
    @ExcelExport
    @JsonProperty("url")
    @Merge(MergeStrategy.ocdsVersion)
    private String url;

    /**
     * The name of the contact person, department, or contact point,
     * for correspondence relating to this contracting process.
     *
     * @return
     *     The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     * The name of the contact person, department, or contact point,
     * for correspondence relating to this contracting process.
     *
     * @param name
     *     The name
     */
    @JsonProperty("name")
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * The e-mail address of the contact point/person.
     *
     * @return
     *     The email
     */
    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    /**
     * The e-mail address of the contact point/person.
     *
     * @param email
     *     The email
     */
    @JsonProperty("email")
    public void setEmail(final String email) {
        this.email = email;
    }

    /**
     * The telephone number of the contact point/person. This should include the international dialling code.
     *
     * @return
     *     The telephone
     */
    @JsonProperty("telephone")
    public String getTelephone() {
        return telephone;
    }

    /**
     * The telephone number of the contact point/person. This should include the international dialling code.
     *
     * @param telephone
     *     The telephone
     */
    @JsonProperty("telephone")
    public void setTelephone(final String telephone) {
        this.telephone = telephone;
    }

    /**
     * The fax number of the contact point/person. This should include the international dialling code.
     *
     * @return
     *     The faxNumber
     */
    @JsonProperty("faxNumber")
    public String getFaxNumber() {
        return faxNumber;
    }

    /**
     * The fax number of the contact point/person. This should include the international dialling code.
     *
     * @param faxNumber
     *     The faxNumber
     */
    @JsonProperty("faxNumber")
    public void setFaxNumber(final String faxNumber) {
        this.faxNumber = faxNumber;
    }

    /**
     * A web address for the contact point/person.
     *
     * @return
     *     The url
     */
    @JsonProperty("url")
    public String getUrl() {
        return url;
    }

    /**
     * A web address for the contact point/person.
     *
     * @param url
     *     The url
     */
    @JsonProperty("url")
    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(name).
                append(email).
                append(telephone).
                append(faxNumber).
                append(url).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof ContactPoint)) {
            return false;
        }
        ContactPoint rhs = ((ContactPoint) other);
        return new EqualsBuilder().
                append(name, rhs.name).
                append(email, rhs.email).
                append(telephone, rhs.telephone).
                append(faxNumber, rhs.faxNumber).
                append(url, rhs.url).
                isEquals();
    }
}
