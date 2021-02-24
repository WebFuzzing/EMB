package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExportSepareteSheet;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Contract
 * <p>
 * Information regarding the signed contract between the buyer and supplier(s).
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#contract
 *
 */
@JsonPropertyOrder({
        "id",
        "awardID",
        "title",
        "description",
        "status",
        "period",
        "value",
        "items",
        "dateSigned",
        "documents",
        "amendment",
        "implementation"
})
public class Contract implements Identifiable {

    /**
     * Contract ID
     * <p>
     * The identifier for this contract. It must be unique and cannot change within its Open Contracting Process
     * (defined by a single ocid). See the
     *  [identifier guidance](http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/)
     * for further details.
     * (Required)
     *
     */
    @ExcelExport
    @JsonProperty("id")
    @Merge(MergeStrategy.overwrite)
    private String id;

    /**
     * Award ID
     * <p>
     * The award.id against which this contract is being issued.
     * (Required)
     *
     */
    @ExcelExport
    @JsonProperty("awardID")
    @Merge(MergeStrategy.ocdsVersion)
    private String awardID;

    /**
     * Contract title
     *
     */
    @ExcelExport
    @JsonProperty("title")
    @Merge(MergeStrategy.ocdsVersion)
    private String title;

    /**
     * Contract description
     *
     */
    @ExcelExport
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    /**
     * Contract Status
     * <p>
     * The current status of the contract. Drawn from the
     * [contractStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#contract-status)
     *
     */
    @ExcelExport
    @JsonProperty("status")
    @Merge(MergeStrategy.ocdsVersion)
    private Status status;

    /**
     * Period
     * <p>
     *
     *
     */
    @ExcelExport
    @JsonProperty("period")
    private Period period;

    @ExcelExport
    @JsonProperty("value")
    private Amount value;

    /**
     * Items Contracted
     * <p>
     * The goods, services, and any intangible outcomes in this contract.
     * Note: If the items are the same as the award do not repeat.
     *
     */
    @ExcelExport
    @ExcelExportSepareteSheet
    @JsonProperty("items")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Item> items = new LinkedHashSet<Item>();

    /**
     * The date the contract was signed. In the case of multiple signatures, the date of the last signature.
     *
     */
    @ExcelExport
    @JsonProperty("dateSigned")
    @Merge(MergeStrategy.ocdsVersion)
    private Date dateSigned;

    /**
     * All documents and attachments related to the contract, including any notices.
     *
     */
    @JsonProperty("documents")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Document> documents = new LinkedHashSet<Document>();

    /**
     * Amendment information
     * <p>
     *
     *
     */
    @JsonProperty("amendment")
    protected Amendment amendment;

    /**
     * Implementation
     * <p>
     * Information during the performance / implementation stage of the contract.
     *
     */
    @ExcelExport
    @JsonProperty("implementation")
    private Implementation implementation;

    /**
     * Contract ID
     * <p>
     * The identifier for this contract. It must be unique and cannot change within its Open Contracting Process
     * (defined by a single ocid). See the
     *  [identifier guidance](http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/)
     * for further details.
     * (Required)
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * Contract ID
     * <p>
     * The identifier for this contract. It must be unique and cannot change within its Open Contracting Process
     * (defined by a single ocid). See the
     *  [identifier guidance](http://ocds.open-contracting.org/standard/r/1__0__0/en/key_concepts/identifiers/)
     * for further details.
     * (Required)
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Award ID
     * <p>
     * The award.id against which this contract is being issued.
     * (Required)
     *
     * @return
     *     The awardID
     */
    @JsonProperty("awardID")
    public String getAwardID() {
        return awardID;
    }

    /**
     * Award ID
     * <p>
     * The award.id against which this contract is being issued.
     * (Required)
     *
     * @param awardID
     *     The awardID
     */
    @JsonProperty("awardID")
    public void setAwardID(final String awardID) {
        this.awardID = awardID;
    }

    /**
     * Contract title
     *
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * Contract title
     *
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Contract description
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Contract description
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Contract Status
     * <p>
     * The current status of the contract. Drawn from the
     * [contractStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#contract-status)
     *
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    /**
     * Contract Status
     * <p>
     * The current status of the contract. Drawn from the
     * [contractStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists/#contract-status)
     *
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * Period
     * <p>
     *
     *
     * @return
     *     The period
     */
    @JsonProperty("period")
    public Period getPeriod() {
        return period;
    }

    /**
     * Period
     * <p>
     *
     *
     * @param period
     *     The period
     */
    @JsonProperty("period")
    public void setPeriod(final Period period) {
        this.period = period;
    }

    /**
     *
     * @return
     *     The value
     */
    @JsonProperty("value")
    public Amount getValue() {
        return value;
    }

    /**
     *
     * @param value
     *     The value
     */
    @JsonProperty("value")
    public void setValue(final Amount value) {
        this.value = value;
    }

    /**
     * Items Contracted
     * <p>
     * The goods, services, and any intangible outcomes in this contract.
     * Note: If the items are the same as the award do not repeat.
     *
     * @return
     *     The items
     */
    @JsonProperty("items")
    public Set<Item> getItems() {
        return items;
    }

    /**
     * Items Contracted
     * <p>
     * The goods, services, and any intangible outcomes in this contract.
     * Note: If the items are the same as the award do not repeat.
     *
     * @param items
     *     The items
     */
    @JsonProperty("items")
    public void setItems(final Set<Item> items) {
        this.items = items;
    }

    /**
     * The date the contract was signed. In the case of multiple signatures, the date of the last signature.
     *
     * @return
     *     The dateSigned
     */
    @JsonProperty("dateSigned")
    public Date getDateSigned() {
        return dateSigned;
    }

    /**
     * The date the contract was signed. In the case of multiple signatures, the date of the last signature.
     *
     * @param dateSigned
     *     The dateSigned
     */
    @JsonProperty("dateSigned")
    public void setDateSigned(final Date dateSigned) {
        this.dateSigned = dateSigned;
    }

    /**
     * All documents and attachments related to the contract, including any notices.
     *
     * @return
     *     The documents
     */
    @JsonProperty("documents")
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * All documents and attachments related to the contract, including any notices.
     *
     * @param documents
     *     The documents
     */
    @JsonProperty("documents")
    public void setDocuments(final Set<Document> documents) {
        this.documents = documents;
    }

    /**
     * Amendment information
     * <p>
     *
     *
     * @return
     *     The amendment
     */
    @JsonProperty("amendment")
    public Amendment getAmendment() {
        return amendment;
    }

    /**
     * Amendment information
     * <p>
     *
     *
     * @param amendment
     *     The amendment
     */
    @JsonProperty("amendment")
    public void setAmendment(final Amendment amendment) {
        this.amendment = amendment;
    }

    /**
     * Implementation
     * <p>
     * Information during the performance / implementation stage of the contract.
     *
     * @return
     *     The implementation
     */
    @JsonProperty("implementation")
    public Implementation getImplementation() {
        return implementation;
    }

    /**
     * Implementation
     * <p>
     * Information during the performance / implementation stage of the contract.
     *
     * @param implementation
     *     The implementation
     */
    @JsonProperty("implementation")
    public void setImplementation(final Implementation implementation) {
        this.implementation = implementation;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(id).
                append(awardID).
                append(title).
                append(description).
                append(status).
                append(period).
                append(value).
                append(items).
                append(dateSigned).
                append(documents).
                append(amendment).
                append(implementation).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Contract)) {
            return false;
        }
        Contract rhs = ((Contract) other);
        return new EqualsBuilder().
                append(id, rhs.id).
                append(awardID, rhs.awardID).
                append(title, rhs.title).
                append(description, rhs.description).
                append(status, rhs.status).
                append(period, rhs.period).
                append(value, rhs.value).
                append(items, rhs.items).
                append(dateSigned, rhs.dateSigned).
                append(documents, rhs.documents).
                append(amendment, rhs.amendment).
                append(implementation, rhs.implementation).
                isEquals();
    }

    public enum Status {
        pending("pending"),

        active("active"),

        cancelled("cancelled"),

        terminated("terminated");

        private final String value;

        private static final Map<String, Status> CONSTANTS = new HashMap<String, Status>();

        static {
            for (Contract.Status c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        Status(final String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static Status fromValue(final String value) {
            Status constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }
    }

    @Override
    public Serializable getIdProperty() {
        return id;
    }
}
