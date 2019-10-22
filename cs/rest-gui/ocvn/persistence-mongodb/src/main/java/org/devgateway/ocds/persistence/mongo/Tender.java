package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExportSepareteSheet;
import org.devgateway.ocds.persistence.mongo.merge.Merge;
import org.devgateway.ocds.persistence.mongo.merge.MergeStrategy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonValue;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;


/**
 * Tender
 * <p>
 * Data regarding tender process - publicly inviting prospective contractors to submit bids for evaluation
 * and selecting a winner or winners.
 *
 * http://standard.open-contracting.org/latest/en/schema/reference/#tender
 *
 */
@JsonPropertyOrder({
        "id",
        "title",
        "description",
        "status",
        "items",
        "minValue",
        "value",
        "procurementMethod",
        "procurementMethodRationale",
        "awardCriteria",
        "awardCriteriaDetails",
        "submissionMethod",
        "submissionMethodDetails",
        "tenderPeriod",
        "enquiryPeriod",
        "hasEnquiries",
        "eligibilityCriteria",
        "awardPeriod",
        "numberOfTenderers",
        "tenderers",
        "procuringEntity",
        "documents",
        "milestones",
        "amendment"
})
public class Tender implements Identifiable {

    /**
     * Tender ID
     * <p>
     * An identifier for this tender process. This may be the same as the ocid, or may be drawn from
     * an internally held identifier for this tender.
     * (Required)
     *
     */
    @ExcelExport
    @JsonProperty("id")
    @Merge(MergeStrategy.ocdsVersion)
    private String id;

    /**
     * Tender title
     *
     */
    @JsonProperty("title")
    @Merge(MergeStrategy.ocdsVersion)
    private String title;

    /**
     * Tender description
     *
     */
    @JsonProperty("description")
    @Merge(MergeStrategy.ocdsVersion)
    private String description;

    /**
     * Tender Status
     * <p>
     * The current status of the tender based on the
     * [tenderStatus codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#tender-status)
     *
     */
    @ExcelExport
    @JsonProperty("status")
    @Merge(MergeStrategy.ocdsVersion)
    private Status status;

    /**
     * Items to be procured
     * <p>
     * The goods and services to be purchased, broken into line items wherever possible.
     * Items should not be duplicated, but a quantity of 2 specified instead.
     *
     */
    @ExcelExport
    @ExcelExportSepareteSheet
    @JsonProperty("items")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.arrayMergeById)
    private Set<Item> items = new LinkedHashSet<Item>();

    @JsonProperty("minValue")
    private Amount minValue;

    @ExcelExport
    @JsonProperty("value")
    private Amount value;

    /**
     * Specify tendering method against the
     * [method codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#method)
     * as per [GPA definitions](http://www.wto.org/english/docs_e/legal_e/rev-gpr-94_01_e.htm) of
     * Open, Selective, Limited
     *
     */
    @JsonProperty("procurementMethod")
    @Merge(MergeStrategy.ocdsVersion)
    private ProcurementMethod procurementMethod;

    /**
     * Rationale of procurement method, especially in the case of Limited tendering.
     *
     */
    @JsonProperty("procurementMethodRationale")
    @Merge(MergeStrategy.ocdsVersion)
    private String procurementMethodRationale;

    /**
     * Specify the award criteria for the procurement, using the
     * [award criteria codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#award-criteria)
     *
     */
    @JsonProperty("awardCriteria")
    @Merge(MergeStrategy.ocdsVersion)
    private String awardCriteria;

    /**
     * Any detailed or further information on the award or selection criteria.
     *
     */
    @JsonProperty("awardCriteriaDetails")
    @Merge(MergeStrategy.ocdsVersion)
    private String awardCriteriaDetails;

    /**
     * Specify the method by which bids must be submitted, in person, written, or electronic auction.
     * Using the
     * [submission method codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#submission-method)
     *
     */
    @JsonProperty("submissionMethod")
    @Merge(MergeStrategy.ocdsVersion)
    private Set<SubmissionMethod> submissionMethod = new TreeSet<SubmissionMethod>();

    /**
     * Any detailed or further information on the submission method. This may include the address,
     * e-mail address or online service to which bids should be submitted,
     * and any special requirements to be followed for submissions.
     *
     */
    @JsonProperty("submissionMethodDetails")
    @Merge(MergeStrategy.ocdsVersion)
    private String submissionMethodDetails;

    /**
     * Period
     * <p>
     *
     *
     */
    @ExcelExport
    @JsonProperty("tenderPeriod")
    private Period tenderPeriod;

    /**
     * Period
     * <p>
     *
     *
     */
    @JsonProperty("enquiryPeriod")
    private Period enquiryPeriod;

    /**
     * A Yes/No field to indicate whether enquiries were part of tender process.
     *
     */
    @JsonProperty("hasEnquiries")
    @Merge(MergeStrategy.ocdsVersion)
    private Boolean hasEnquiries;

    /**
     * A description of any eligibility criteria for potential suppliers.
     *
     */
    @JsonProperty("eligibilityCriteria")
    @Merge(MergeStrategy.ocdsVersion)
    private String eligibilityCriteria;

    /**
     * Period
     * <p>
     *
     *
     */
    @JsonProperty("awardPeriod")
    private Period awardPeriod;

    @ExcelExport
    @JsonProperty("numberOfTenderers")
    @Merge(MergeStrategy.ocdsVersion)
    private Integer numberOfTenderers;

    /**
     * All entities who submit a tender.
     *
     */
    @ExcelExport
    @JsonProperty("tenderers")
    @JsonDeserialize(as = java.util.LinkedHashSet.class)
    @Merge(MergeStrategy.ocdsVersion)
    private Set<Organization> tenderers = new LinkedHashSet<Organization>();

    /**
     * Organization
     * <p>
     * An organization.
     *
     */
    @ExcelExport
    @JsonProperty("procuringEntity")
    private Organization procuringEntity;

    /**
     * All documents and attachments related to the tender, including any notices. See the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type)
     * for details of potential documents to include.
     *
     */
    @JsonProperty("documents")
    @Merge(MergeStrategy.arrayMergeById)
    private List<Document> documents = new ArrayList<Document>();

    /**
     * A list of milestones associated with the tender.
     *
     */
    @JsonProperty("milestones")
    @Merge(MergeStrategy.arrayMergeById)
    private List<Milestone> milestones = new ArrayList<Milestone>();

    /**
     * Amendment information
     * <p>
     *
     *
     */
    @JsonProperty("amendment")
    private Amendment amendment;

    /**
     * Tender ID
     * <p>
     * An identifier for this tender process. This may be the same as the ocid, or may be drawn from an
     * internally held identifier for this tender.
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
     * Tender ID
     * <p>
     * An identifier for this tender process. This may be the same as the ocid, or may be drawn from an
     * internally held identifier for this tender.
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
     * Tender title
     *
     * @return
     *     The title
     */
    @JsonProperty("title")
    public String getTitle() {
        return title;
    }

    /**
     * Tender title
     *
     * @param title
     *     The title
     */
    @JsonProperty("title")
    public void setTitle(final String title) {
        this.title = title;
    }

    /**
     * Tender description
     *
     * @return
     *     The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     * Tender description
     *
     * @param description
     *     The description
     */
    @JsonProperty("description")
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * Tender Status
     * <p>
     * The current status of the tender based on the
     * [tenderStatus codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#tender-status)
     *
     * @return
     *     The status
     */
    @JsonProperty("status")
    public Status getStatus() {
        return status;
    }

    /**
     * Tender Status
     * <p>
     * The current status of the tender based on the
     * [tenderStatus codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#tender-status)
     *
     * @param status
     *     The status
     */
    @JsonProperty("status")
    public void setStatus(final Status status) {
        this.status = status;
    }

    /**
     * Items to be procured
     * <p>
     * The goods and services to be purchased, broken into line items wherever possible.
     * Items should not be duplicated, but a quantity of 2 specified instead.
     *
     * @return
     *     The items
     */
    @JsonProperty("items")
    public Set<Item> getItems() {
        return items;
    }

    /**
     * Items to be procured
     * <p>
     * The goods and services to be purchased, broken into line items wherever possible.
     * Items should not be duplicated, but a quantity of 2 specified instead.
     *
     * @param items
     *     The items
     */
    @JsonProperty("items")
    public void setItems(final Set<Item> items) {
        this.items = items;
    }

    /**
     *
     * @return
     *     The minValue
     */
    @JsonProperty("minValue")
    public Amount getMinValue() {
        return minValue;
    }

    /**
     *
     * @param minValue
     *     The minValue
     */
    @JsonProperty("minValue")
    public void setMinValue(final Amount minValue) {
        this.minValue = minValue;
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
     * Specify tendering method against the
     * [method codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#method) as per
     * [GPA definitions](http://www.wto.org/english/docs_e/legal_e/rev-gpr-94_01_e.htm) of Open, Selective, Limited
     *
     * @return
     *     The procurementMethod
     */
    @JsonProperty("procurementMethod")
    public ProcurementMethod getProcurementMethod() {
        return procurementMethod;
    }

    /**
     * Specify tendering method against the
     * [method codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#method) as per
     * [GPA definitions](http://www.wto.org/english/docs_e/legal_e/rev-gpr-94_01_e.htm) of Open, Selective, Limited
     *
     * @param procurementMethod
     *     The procurementMethod
     */
    @JsonProperty("procurementMethod")
    public void setProcurementMethod(final ProcurementMethod procurementMethod) {
        this.procurementMethod = procurementMethod;
    }

    /**
     * Rationale of procurement method, especially in the case of Limited tendering.
     *
     * @return
     *     The procurementMethodRationale
     */
    @JsonProperty("procurementMethodRationale")
    public String getProcurementMethodRationale() {
        return procurementMethodRationale;
    }

    /**
     * Rationale of procurement method, especially in the case of Limited tendering.
     *
     * @param procurementMethodRationale
     *     The procurementMethodRationale
     */
    @JsonProperty("procurementMethodRationale")
    public void setProcurementMethodRationale(final String procurementMethodRationale) {
        this.procurementMethodRationale = procurementMethodRationale;
    }

    /**
     * Specify the award criteria for the procurement, using the
     * [award criteria codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#award-criteria)
     *
     * @return
     *     The awardCriteria
     */
    @JsonProperty("awardCriteria")
    public String getAwardCriteria() {
        return awardCriteria;
    }

    /**
     * Specify the award criteria for the procurement, using the
     * [award criteria codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#award-criteria)
     *
     * @param awardCriteria
     *     The awardCriteria
     */
    @JsonProperty("awardCriteria")
    public void setAwardCriteria(final String awardCriteria) {
        this.awardCriteria = awardCriteria;
    }

    /**
     * Any detailed or further information on the award or selection criteria.
     *
     * @return
     *     The awardCriteriaDetails
     */
    @JsonProperty("awardCriteriaDetails")
    public String getAwardCriteriaDetails() {
        return awardCriteriaDetails;
    }

    /**
     * Any detailed or further information on the award or selection criteria.
     *
     * @param awardCriteriaDetails
     *     The awardCriteriaDetails
     */
    @JsonProperty("awardCriteriaDetails")
    public void setAwardCriteriaDetails(final String awardCriteriaDetails) {
        this.awardCriteriaDetails = awardCriteriaDetails;
    }

    /**
     * Specify the method by which bids must be submitted, in person, written, or electronic auction.
     * Using the
     * [submission method codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#submission-method)
     *
     * @return
     *     The submissionMethod
     */
    @JsonProperty("submissionMethod")
    public Set<SubmissionMethod> getSubmissionMethod() {
        return submissionMethod;
    }

    /**
     * Specify the method by which bids must be submitted, in person, written, or electronic auction.
     * Using the
     * [submission method codelist]
     *  (http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#submission-method)
     *
     * @param submissionMethod
     *     The submissionMethod
     */
    @JsonProperty("submissionMethod")
    public void setSubmissionMethod(final Set<SubmissionMethod> submissionMethod) {
        this.submissionMethod = submissionMethod;
    }

    /**
     * Any detailed or further information on the submission method. This may include the address,
     * e-mail address or online service to which bids should be submitted,
     * and any special requirements to be followed for submissions.
     *
     * @return
     *     The submissionMethodDetails
     */
    @JsonProperty("submissionMethodDetails")
    public String getSubmissionMethodDetails() {
        return submissionMethodDetails;
    }

    /**
     * Any detailed or further information on the submission method. This may include the address,
     * e-mail address or online service to which bids should be submitted,
     * and any special requirements to be followed for submissions.
     *
     * @param submissionMethodDetails
     *     The submissionMethodDetails
     */
    @JsonProperty("submissionMethodDetails")
    public void setSubmissionMethodDetails(final String submissionMethodDetails) {
        this.submissionMethodDetails = submissionMethodDetails;
    }

    /**
     * Period
     * <p>
     *
     *
     * @return
     *     The tenderPeriod
     */
    @JsonProperty("tenderPeriod")
    public Period getTenderPeriod() {
        return tenderPeriod;
    }

    /**
     * Period
     * <p>
     *
     *
     * @param tenderPeriod
     *     The tenderPeriod
     */
    @JsonProperty("tenderPeriod")
    public void setTenderPeriod(final Period tenderPeriod) {
        this.tenderPeriod = tenderPeriod;
    }

    /**
     * Period
     * <p>
     *
     *
     * @return
     *     The enquiryPeriod
     */
    @JsonProperty("enquiryPeriod")
    public Period getEnquiryPeriod() {
        return enquiryPeriod;
    }

    /**
     * Period
     * <p>
     *
     *
     * @param enquiryPeriod
     *     The enquiryPeriod
     */
    @JsonProperty("enquiryPeriod")
    public void setEnquiryPeriod(final Period enquiryPeriod) {
        this.enquiryPeriod = enquiryPeriod;
    }

    /**
     * A Yes/No field to indicate whether enquiries were part of tender process.
     *
     * @return
     *     The hasEnquiries
     */
    @JsonProperty("hasEnquiries")
    public Boolean getHasEnquiries() {
        return hasEnquiries;
    }

    /**
     * A Yes/No field to indicate whether enquiries were part of tender process.
     *
     * @param hasEnquiries
     *     The hasEnquiries
     */
    @JsonProperty("hasEnquiries")
    public void setHasEnquiries(final Boolean hasEnquiries) {
        this.hasEnquiries = hasEnquiries;
    }

    /**
     * A description of any eligibility criteria for potential suppliers.
     *
     * @return
     *     The eligibilityCriteria
     */
    @JsonProperty("eligibilityCriteria")
    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    /**
     * A description of any eligibility criteria for potential suppliers.
     *
     * @param eligibilityCriteria
     *     The eligibilityCriteria
     */
    @JsonProperty("eligibilityCriteria")
    public void setEligibilityCriteria(final String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    /**
     * Period
     * <p>
     *
     *
     * @return
     *     The awardPeriod
     */
    @JsonProperty("awardPeriod")
    public Period getAwardPeriod() {
        return awardPeriod;
    }

    /**
     * Period
     * <p>
     *
     *
     * @param awardPeriod
     *     The awardPeriod
     */
    @JsonProperty("awardPeriod")
    public void setAwardPeriod(final Period awardPeriod) {
        this.awardPeriod = awardPeriod;
    }

    /**
     *
     * @return
     *     The numberOfTenderers
     */
    @JsonProperty("numberOfTenderers")
    public Integer getNumberOfTenderers() {
        return numberOfTenderers;
    }

    /**
     *
     * @param numberOfTenderers
     *     The numberOfTenderers
     */
    @JsonProperty("numberOfTenderers")
    public void setNumberOfTenderers(final Integer numberOfTenderers) {
        this.numberOfTenderers = numberOfTenderers;
    }

    /**
     * All entities who submit a tender.
     *
     * @return
     *     The tenderers
     */
    @JsonProperty("tenderers")
    public Set<Organization> getTenderers() {
        return tenderers;
    }

    /**
     * All entities who submit a tender.
     *
     * @param tenderers
     *     The tenderers
     */
    @JsonProperty("tenderers")
    public void setTenderers(final Set<Organization> tenderers) {
        this.tenderers = tenderers;
    }

    /**
     * Organization
     * <p>
     * An organization.
     *
     * @return
     *     The procuringEntity
     */
    @JsonProperty("procuringEntity")
    public Organization getProcuringEntity() {
        return procuringEntity;
    }

    /**
     * Organization
     * <p>
     * An organization.
     *
     * @param procuringEntity
     *     The procuringEntity
     */
    @JsonProperty("procuringEntity")
    public void setProcuringEntity(final Organization procuringEntity) {
        this.procuringEntity = procuringEntity;
    }

    /**
     * All documents and attachments related to the tender, including any notices. See the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type)
     * for details of potential documents to include.
     *
     * @return
     *     The documents
     */
    @JsonProperty("documents")
    public List<Document> getDocuments() {
        return documents;
    }

    /**
     * All documents and attachments related to the tender, including any notices. See the
     * [documentType codelist](http://ocds.open-contracting.org/standard/r/1__0__0/en/schema/codelists#document-type)
     * for details of potential documents to include.
     *
     * @param documents
     *     The documents
     */
    @JsonProperty("documents")
    public void setDocuments(final List<Document> documents) {
        this.documents = documents;
    }

    /**
     * A list of milestones associated with the tender.
     *
     * @return
     *     The milestones
     */
    @JsonProperty("milestones")
    public List<Milestone> getMilestones() {
        return milestones;
    }

    /**
     * A list of milestones associated with the tender.
     *
     * @param milestones
     *     The milestones
     */
    @JsonProperty("milestones")
    public void setMilestones(final List<Milestone> milestones) {
        this.milestones = milestones;
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
                append(status).
                append(items).
                append(minValue).
                append(value).
                append(procurementMethod).
                append(procurementMethodRationale).
                append(awardCriteria).
                append(awardCriteriaDetails).
                append(submissionMethod).
                append(submissionMethodDetails).
                append(tenderPeriod).
                append(enquiryPeriod).
                append(hasEnquiries).
                append(eligibilityCriteria).
                append(awardPeriod).
                append(numberOfTenderers).
                append(tenderers).
                append(procuringEntity).
                append(documents).
                append(milestones).append(amendment).
                toHashCode();
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof Tender)) {
            return false;
        }
        Tender rhs = ((Tender) other);
        return new EqualsBuilder().
                append(id, rhs.id).
                append(title, rhs.title).
                append(description, rhs.description).
                append(status, rhs.status).
                append(items, rhs.items).
                append(minValue, rhs.minValue).
                append(value, rhs.value).
                append(procurementMethod, rhs.procurementMethod).
                append(procurementMethodRationale, rhs.procurementMethodRationale).
                append(awardCriteria, rhs.awardCriteria).
                append(awardCriteriaDetails, rhs.awardCriteriaDetails).
                append(submissionMethod, rhs.submissionMethod).
                append(submissionMethodDetails, rhs.submissionMethodDetails).
                append(tenderPeriod, rhs.tenderPeriod).
                append(enquiryPeriod, rhs.enquiryPeriod).
                append(hasEnquiries, rhs.hasEnquiries).
                append(eligibilityCriteria, rhs.eligibilityCriteria).
                append(awardPeriod, rhs.awardPeriod).
                append(numberOfTenderers, rhs.numberOfTenderers).
                append(tenderers, rhs.tenderers).
                append(procuringEntity, rhs.procuringEntity).
                append(documents, rhs.documents).
                append(milestones, rhs.milestones).
                append(amendment, rhs.amendment).
                isEquals();
    }

    public enum ProcurementMethod {
        open("open"),

        selective("selective"),

        limited("limited");

        private final String value;

        private static final Map<String, ProcurementMethod> CONSTANTS = new HashMap<String, ProcurementMethod>();

        static {
            for (ProcurementMethod c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        ProcurementMethod(final String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static ProcurementMethod fromValue(final String value) {
            ProcurementMethod constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }



    public enum SubmissionMethod {
        electronicAuction("electronicAuction"),

        electronicSubmission("electronicSubmission"),

        written("written"),

        inPerson("inPerson");

        private final String value;

        private static final Map<String, SubmissionMethod> CONSTANTS = new HashMap<String, SubmissionMethod>();

        static {
            for (SubmissionMethod c: values()) {
                CONSTANTS.put(c.value, c);
            }
        }

        SubmissionMethod(final String value) {
            this.value = value;
        }

        @JsonValue
        @Override
        public String toString() {
            return this.value;
        }

        @JsonCreator
        public static SubmissionMethod fromValue(final String value) {
            SubmissionMethod constant = CONSTANTS.get(value);
            if (constant == null) {
                throw new IllegalArgumentException(value);
            } else {
                return constant;
            }
        }

    }

    public enum Status {
        planned("planned"),

        active("active"),

        cancelled("cancelled"),

        unsuccessful("unsuccessful"),

        complete("complete");

        private final String value;

        private static final Map<String, Status> CONSTANTS = new HashMap<String, Status>();

        static {
            for (Status c: values()) {
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
