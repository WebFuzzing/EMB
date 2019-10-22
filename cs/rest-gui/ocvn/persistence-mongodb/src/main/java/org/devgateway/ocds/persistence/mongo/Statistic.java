package org.devgateway.ocds.persistence.mongo;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Bid Statistic
 * <p>
 * For reporting aggregate statistics about the bids related to a tender.
 * Where lots are in use, statistics may optionally be broken down by lot.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
        "id",
        "measure",
        "date",
        "value",
        "notes",
        "relatedLot"
})
public class Statistic {

    /**
     * ID
     * <p>
     * An internal identifier for this statistical item.
     * (Required)
     */
    @JsonProperty("id")
    @JsonPropertyDescription("An internal identifier for this statistical item.")
    private String id;
    /**
     * Measure
     * <p>
     * An item from the bidStatistics codelist for the statisic reported in value.
     * (Required)
     */
    @JsonProperty("measure")
    @JsonPropertyDescription("An item from the bidStatistics codelist for the statisic reported in value.")
    private String measure;
    /**
     * Date
     * <p>
     * The date when this statistic was last updated. This is often the closing date of the tender process.
     * This field can be left blank unless either (a) the same statistic is provided from multiple points in time,
     * or (b) there is a specific local requirement for the data when statistics were calculated to be provided.
     */
    @JsonProperty("date")
    @JsonPropertyDescription("The date when this statistic was last updated. This is often the closing date of the "
            + "tender process. This field can be left blank unless either (a) the same statistic is provided from "
            + "multiple points in time, or (b) there is a specific local requirement for the data when statistics "
            + "were calculated to be provided.")
    private Date date;
    /**
     * Value
     * <p>
     * The value for the measure in question. Total counts should be provided as an integer. Percentages should be
     * presented as a proportion of 1 (e.g. 10% = 0.1)
     * (Required)
     */
    @JsonProperty("value")
    @JsonPropertyDescription("The value for the measure in question. Total counts should be provided as an integer."
            + " Percentages should be presented as a proportion of 1 (e.g. 10% = 0.1)")
    private Double value;
    /**
     * Notes
     * <p>
     * Any notes required to understand or interpret the given statistic.
     */
    @JsonProperty("notes")
    @JsonPropertyDescription("Any notes required to understand or interpret the given statistic.")
    private String notes;
    /**
     * Related Lot
     * <p>
     * Where lots are in use, if this statistic relates to bids on a particular lot, provide the lot identifier here.
     * If left blank, the statistic will be interpreted as applying to the whole tender.
     */
    @JsonProperty("relatedLot")
    @JsonPropertyDescription("Where lots are in use, if this statistic relates to bids on a particular lot, provide"
            + " the lot identifier here. If left blank, the statistic will be interpreted as applying"
            + " to the whole tender.")
    private String relatedLot;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * ID
     * <p>
     * An internal identifier for this statistical item.
     * (Required)
     */
    @JsonProperty("id")
    public String getId() {
        return id;
    }

    /**
     * ID
     * <p>
     * An internal identifier for this statistical item.
     * (Required)
     */
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Measure
     * <p>
     * An item from the bidStatistics codelist for the statisic reported in value.
     * (Required)
     */
    @JsonProperty("measure")
    public String getMeasure() {
        return measure;
    }

    /**
     * Measure
     * <p>
     * An item from the bidStatistics codelist for the statisic reported in value.
     * (Required)
     */
    @JsonProperty("measure")
    public void setMeasure(String measure) {
        this.measure = measure;
    }

    /**
     * Date
     * <p>
     * The date when this statistic was last updated. This is often the closing date of the tender process.
     * This field can be left blank unless either (a) the same statistic is provided from multiple points in time,
     * or (b) there is a specific local requirement for the data when statistics were calculated to be provided.
     */
    @JsonProperty("date")
    public Date getDate() {
        return date;
    }

    /**
     * Date
     * <p>
     * The date when this statistic was last updated. This is often the closing date of the tender process.
     * This field can be left blank unless either (a) the same statistic is provided from multiple points in time,
     * or (b) there is a specific local requirement for the data when statistics were calculated to be provided.
     */
    @JsonProperty("date")
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Value
     * <p>
     * The value for the measure in question. Total counts should be provided as an integer. Percentages should be
     * presented as a proportion of 1 (e.g. 10% = 0.1)
     * (Required)
     */
    @JsonProperty("value")
    public Double getValue() {
        return value;
    }

    /**
     * Value
     * <p>
     * The value for the measure in question. Total counts should be provided as an integer. Percentages should be
     * presented as a proportion of 1 (e.g. 10% = 0.1)
     * (Required)
     */
    @JsonProperty("value")
    public void setValue(Double value) {
        this.value = value;
    }

    /**
     * Notes
     * <p>
     * Any notes required to understand or interpret the given statistic.
     */
    @JsonProperty("notes")
    public String getNotes() {
        return notes;
    }

    /**
     * Notes
     * <p>
     * Any notes required to understand or interpret the given statistic.
     */
    @JsonProperty("notes")
    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Related Lot
     * <p>
     * Where lots are in use, if this statistic relates to bids on a particular lot, provide the lot identifier here.
     * If left blank, the statistic will be interpreted as applying to the whole tender.
     */
    @JsonProperty("relatedLot")
    public String getRelatedLot() {
        return relatedLot;
    }

    /**
     * Related Lot
     * <p>
     * Where lots are in use, if this statistic relates to bids on a particular lot, provide the lot identifier here.
     * If left blank, the statistic will be interpreted as applying to the whole tender.
     */
    @JsonProperty("relatedLot")
    public void setRelatedLot(String relatedLot) {
        this.relatedLot = relatedLot;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

    public void setAdditionalProperties(Map<String, Object> additionalProperties) {
        this.additionalProperties = additionalProperties;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).append(measure).append(date).append(value).append(notes).
                append(relatedLot).append(additionalProperties).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Statistic)) {
            return false;
        }
        Statistic rhs = ((Statistic) other);
        return new EqualsBuilder().append(id, rhs.id).append(measure, rhs.measure).append(date, rhs.date).
                append(value, rhs.value).append(notes, rhs.notes).append(relatedLot, rhs.relatedLot).
                append(additionalProperties, rhs.additionalProperties).isEquals();
    }

}
