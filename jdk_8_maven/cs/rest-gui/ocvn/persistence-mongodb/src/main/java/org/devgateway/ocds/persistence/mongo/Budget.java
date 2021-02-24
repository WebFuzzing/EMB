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
 * Budget Information
 * <p>
 * This section contain information about the budget line, and associated projects,
 * through which this contracting process is funded.
 * It draws upon data model of the
 *  [Budget Data Package](https://github.com/openspending/budget-data-package/blob/master/specification.md),
 * and should be used to cross-reference to more detailed information held using a Budget Data Package, or,
 * where no linked Budget Data Package is available, to provide enough information to allow a user to manually or
 * automatically cross-reference with another published source of budget and project information.
 *
 *  http://standard.open-contracting.org/latest/en/schema/reference/#budget
 *
 */
@JsonPropertyOrder({
        "source",
        "id",
        "description",
        "amount",
        "project",
        "projectID",
        "uri"
})
public class Budget {

    /**
     * Data Source
     * <p>
     * Used to point either to a corresponding Budget Data Package, or to a machine or human-readable source where
     * users can find further information on the budget line item identifiers, or project identifiers, provided here.
     *
     */
    @JsonProperty("source")
    @Merge(MergeStrategy.ocdsVersion)
    private String source;

    /**
     * An identifier for the budget line item which provides funds for this contracting process.
     * This identifier should be possible to cross-reference against the provided data source.
     *
     */
    @JsonProperty("id")
    @Merge(MergeStrategy.ocdsVersion)
    private String id;

    /**
     * Budget Source
     * <p>
     * A short free text description of the budget source. May be used to provide the title of the budget line,
     * or the programme used to fund this project.
     *
     */
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    @ExcelExport
    @JsonProperty("amount")
    private Amount amount;

    /**
     * Project Title
     * <p>
     * The name of the project that through which this contracting process is funded (if applicable).
     * Some organizations maintain a registry of projects, and the data should use the name by which the project
     * is known in that registry. No translation option is offered for this string, as translated values
     * can be provided in third-party data, linked from the data source above.
     *
     */
    @ExcelExport
    @JsonProperty("project")
    @Merge(MergeStrategy.ocdsVersion)
    private String project;

    /**
     * Project Identifier
     * <p>
     * An external identifier for the project that this contracting process forms part of,
     * or is funded via (if applicable). Some organizations maintain a registry of projects,
     * and the data should use the identifier from the relevant registry of projects.
     *
     */
    @ExcelExport
    @JsonProperty("projectID")
    @Merge(MergeStrategy.ocdsVersion)
    private String projectID;

    /**
     * Linked budget information
     * <p>
     * A URI pointing directly to a machine-readable record about
     * the related budget or projects for this contracting process.
     *
     */
    @JsonProperty("uri")
    @Merge(MergeStrategy.ocdsVersion)
    private String uri;

    /**
     * Data Source
     * <p>
     * Used to point either to a corresponding Budget Data Package, or to a machine or human-readable source where
     * users can find further information on the budget line item identifiers, or project identifiers, provided here.
     *
     * @return
     *     The source
     */
    @JsonProperty("source")
    public String getSource() {
        return source;
    }

    /**
     * Data Source
     * <p>
     * Used to point either to a corresponding Budget Data Package, or to a machine or human-readable source where
     * users can find further information on the budget line item identifiers, or project identifiers, provided here.
     *
     * @param source
     *     The source
     */
    @JsonProperty("source")
    public void setSource(final String source) {
        this.source = source;
    }

    /**
     * An identifier for the budget line item which provides funds for this contracting process.
     * This identifier should be possible to cross-reference against the provided data source.
     *
     * @return
     *     The id
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * An identifier for the budget line item which provides funds for this contracting process.
     * This identifier should be possible to cross-reference against the provided data source.
     *
     * @param id
     *     The id
     */
    @JsonProperty("id")
    public void setId(final String id) {
        this.id = id;
    }

    /**
     * Budget Source
     * <p>
     * A short free text description of the budget source. May be used to provide the title of the budget line,
     * or the programme used to fund this project.
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Budget Source
     * <p>
     * A short free text description of the budget source. May be used to provide the title of the budget line,
     * or the programme used to fund this project.
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     *
     * @return
     *     The amount
     */
    @JsonProperty("amount")
    public Amount getAmount() {
        return amount;
    }

    /**
     *
     * @param amount
     *     The amount
     */
    @JsonProperty("amount")
    public void setAmount(final Amount amount) {
        this.amount = amount;
    }

    /**
     * Project Title
     * <p>
     * The name of the project that through which this contracting process is funded (if applicable).
     * Some organizations maintain a registry of projects, and the data should use the name by which the project
     * is known in that registry. No translation option is offered for this string,
     * as translated values can be provided in third-party data, linked from the data source above.
     *
     * @return
     *     The project
     */
    @JsonProperty("project")
    public String getProject() {
        return project;
    }

    /**
     * Project Title
     * <p>
     * The name of the project that through which this contracting process is funded (if applicable).
     * Some organizations maintain a registry of projects, and the data should use the name by which the project
     * is known in that registry. No translation option is offered for this string,
     * as translated values can be provided in third-party data, linked from the data source above.
     *
     * @param project
     *     The project
     */
    @JsonProperty("project")
    public void setProject(final String project) {
        this.project = project;
    }

    /**
     * Project Identifier
     * <p>
     * An external identifier for the project that this contracting process forms part of,
     * or is funded via (if applicable). Some organizations maintain a registry of projects,
     * and the data should use the identifier from the relevant registry of projects.
     *
     * @return
     *     The projectID
     */
    @JsonProperty("projectID")
    public String getProjectID() {
        return projectID;
    }

    /**
     * Project Identifier
     * <p>
     * An external identifier for the project that this contracting process forms part of,
     * or is funded via (if applicable). Some organizations maintain a registry of projects,
     * and the data should use the identifier from the relevant registry of projects.
     *
     * @param projectID
     *     The projectID
     */
    @JsonProperty("projectID")
    public void setProjectID(final String projectID) {
        this.projectID = projectID;
    }

    /**
     * Linked budget information
     * <p>
     * A URI pointing directly to a machine-readable record about the related budget or
     * projects for this contracting process.
     *
     * @return
     *     The uri
     */
    @JsonProperty("uri")
    public String getUri() {
        return uri;
    }

    /**
     * Linked budget information
     * <p>
     * A URI pointing directly to a machine-readable record about the related budget or
     * projects for this contracting process.
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
                append(source).
                append(id).
                append(description).
                append(amount).
                append(project).
                append(projectID).
                append(uri).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Budget)) {
            return false;
        }
        Budget rhs = ((Budget) other);
        return new EqualsBuilder().
                append(source, rhs.source).
                append(id, rhs.id).
                append(description, rhs.description).
                append(amount, rhs.amount).
                append(project, rhs.project).
                append(projectID, rhs.projectID).
                append(uri, rhs.uri)
                .isEquals();
    }

}
