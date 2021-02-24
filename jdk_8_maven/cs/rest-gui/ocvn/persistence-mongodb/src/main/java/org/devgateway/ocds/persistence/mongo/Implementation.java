package org.devgateway.ocds.persistence.mongo;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Implementation
 * <p>
 * Information during the performance / implementation stage of the contract.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#implementation
 *
 */
@JsonPropertyOrder({
        "transactions",
        "milestones",
        "documents"
})
public class Implementation {

    /**
     * A list of the spending transactions made against this contract
     *
     */
    @ExcelExport
    @JsonProperty("transactions")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Transaction> transactions = new LinkedHashSet<>();

    /**
     * As milestones are completed, milestone completions should be documented.
     *
     */
    @JsonProperty("milestones")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Milestone> milestones = new LinkedHashSet<Milestone>();

    /**
     * Documents and reports that are part of the implementation phase e.g. audit and evaluation reports.
     *
     */
    @JsonProperty("documents")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Document> documents = new LinkedHashSet<Document>();

    /**
     * A list of the spending transactions made against this contract
     *
     * @return
     *     The transactions
     */
    @JsonProperty("transactions")
    public Set<Transaction> getTransactions() {
        return transactions;
    }

    /**
     * A list of the spending transactions made against this contract
     *
     * @param transactions
     *     The transactions
     */
    @JsonProperty("transactions")
    public void setTransactions(final Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    /**
     * As milestones are completed, milestone completions should be documented.
     *
     * @return
     *     The milestones
     */
    @JsonProperty("milestones")
    public Set<Milestone> getMilestones() {
        return milestones;
    }

    /**
     * As milestones are completed, milestone completions should be documented.
     *
     * @param milestones
     *     The milestones
     */
    @JsonProperty("milestones")
    public void setMilestones(final Set<Milestone> milestones) {
        this.milestones = milestones;
    }

    /**
     * Documents and reports that are part of the implementation phase e.g. audit and evaluation reports.
     *
     * @return
     *     The documents
     */
    @JsonProperty("documents")
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * Documents and reports that are part of the implementation phase e.g. audit and evaluation reports.
     *
     * @param documents
     *     The documents
     */
    @JsonProperty("documents")
    public void setDocuments(final Set<Document> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(transactions).
                append(milestones).
                append(documents).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Implementation)) {
            return false;
        }
        Implementation rhs = ((Implementation) other);
        return new EqualsBuilder().
                append(transactions, rhs.transactions).
                append(milestones, rhs.milestones).
                append(documents, rhs.documents).
                isEquals();
    }

}
