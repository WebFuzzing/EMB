package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;


/**
 * Milestone OCDS entity http://standard.open-contracting.org/latest/en/schema/reference/#milestone
 */
@JsonPropertyOrder({
        "id",
        "title",
        "description",
        "dueDate",
        "dateModified",
        "status",
        "documents"
})
public class Milestone implements Identifiable {

    /**
     * A local identifier for this milestone, unique within this block. This field is used to keep track of
     * multiple revisions of a milestone through the compilation from release to record mechanism.
     * (Required)
     *
     */
    @JsonProperty("id")
    @Merge(MergeStrategy.overwrite)
    private String id;

    /**
     * Milestone title
     *
     */
    @JsonProperty("title")
    @Merge(MergeStrategy.ocdsVersion)
    private String title;

    /**
     * A description of the milestone.
     *
     */
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    /**
     * The date the milestone is due.
     *
     */
    @JsonProperty("dueDate")
    @Merge(MergeStrategy.ocdsVersion)
    private Date dueDate;

    /**
     * The date the milestone was last reviewed or modified and the status was altered or confirmed to still be correct.
     *
     */
    @JsonProperty("dateModified")
    @Merge(MergeStrategy.ocdsVersion)
    private Date dateModified;

    /**
     * The status that was realized on the date provided in dateModified, drawn from the
     * [milestoneStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#milestone-status).
     *
     */
    @JsonProperty("status")
    @Merge(MergeStrategy.ocdsVersion)
    private Milestone.Status status;

    /**
     * List of documents associated with this milestone.
     *
     */
    @JsonProperty("documents")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Document> documents = new LinkedHashSet<Document>();

    /**
     * A local identifier for this milestone, unique within this block. This field is used to keep track of
     * multiple revisions of a milestone through the compilation from release to record mechanism.
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
     * A local identifier for this milestone, unique within this block. This field is used to keep track of
     * multiple revisions of a milestone through the compilation from release to record mechanism.
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
     * Milestone title
     *
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * Milestone title
     *
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * A description of the milestone.
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * A description of the milestone.
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * The date the milestone is due.
     *
     * @return
     *     The dueDate
     */
    @JsonProperty("dueDate")
    public Date getDueDate() {
        return dueDate;
    }

    /**
     * The date the milestone is due.
     *
     * @param dueDate
     *     The dueDate
     */
    @JsonProperty("dueDate")
    public void setDueDate(final Date dueDate) {
        this.dueDate = dueDate;
    }

    /**
     * The date the milestone was last reviewed or modified and the status was altered or confirmed to still be correct.
     *
     * @return
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public Date getDateModified() {
        return dateModified;
    }

    /**
     * The date the milestone was last reviewed or modified and the status was altered or confirmed to still be correct.
     *
     * @param dateModified
     *     The dateModified
     */
    @JsonProperty("dateModified")
    public void setDateModified(final Date dateModified) {
        this.dateModified = dateModified;
    }

    /**
     * The status that was realized on the date provided in dateModified, drawn from the
     * [milestoneStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#milestone-status).
     *
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Milestone.Status getStatus() {
        return status;
    }

    /**
     * The status that was realized on the date provided in dateModified, drawn from the
     * [milestoneStatus codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#milestone-status).
     *
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(final Milestone.Status status) {
        this.status = status;
    }

    /**
     * List of documents associated with this milestone.
     *
     * @return
     *     The documents
     */
    @JsonProperty("documents")
    public Set<Document> getDocuments() {
        return documents;
    }

    /**
     * List of documents associated with this milestone.
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
                append(id).
                append(title).
                append(description).
                append(dueDate).
                append(dateModified).
                append(status).
                append(documents).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Milestone)) {
            return false;
        }
        Milestone rhs = ((Milestone) other);
        return new EqualsBuilder().
                append(id, rhs.id).
                append(title, rhs.title).
                append(description, rhs.description).
                append(dueDate, rhs.dueDate).
                append(dateModified, rhs.dateModified).
                append(status, rhs.status).
                append(documents, rhs.documents).
                isEquals();
    }

    public enum Status {
        met("met"),

        notMet("notMet"),

        partiallyMet("partiallyMet");

        private final String value;

        private static final Map<String, Milestone.Status> CONSTANTS = new HashMap<String, Milestone.Status>();

        static {
            for (Milestone.Status c: values()) {
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
        public static Milestone.Status fromValue(final String value) {
            Milestone.Status constant = CONSTANTS.get(value);
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
