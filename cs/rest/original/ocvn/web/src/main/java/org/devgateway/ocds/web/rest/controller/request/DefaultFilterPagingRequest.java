/**
 *
 */
package org.devgateway.ocds.web.rest.controller.request;

import java.math.BigDecimal;
import cz.jirutka.validator.collection.constraints.EachPattern;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

/**
 * @author mpostelnicu Filtering bean applied to all endpoints
 */
public class DefaultFilterPagingRequest extends GenericPagingRequest {

    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    @ApiModelProperty(value = "This corresponds to the tender.items.classification._id")
    private List<String> bidTypeId;

    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    @ApiModelProperty(
            value = "This corresponds the negated bidTypeId filter, matches elements that are NOT in the list of Ids")
    private List<String> notBidTypeId;

    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    @ApiModelProperty(value = "This is the id of the organization/procuring entity. "
            + "Corresponds to the OCDS Organization.identifier")
    private List<String> procuringEntityId;

    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    @ApiModelProperty(value = "This corresponds the negated procuringEntityId filter,"
            + " matches elements that are NOT in the list of Ids")
    private List<String> notProcuringEntityId;

    // @EachPattern(regexp = "^[\\p{L}0-9]*$")
    @ApiModelProperty(value = "This is the id of the organization/supplier entity. "
            + "Corresponds to the OCDS Organization.identifier")
    private List<String> supplierId;

    @ApiModelProperty(value = "This will filter after tender.procurementMethodDetails."
            + "Valid examples are Đấu thầu rộng rãi, Đấu thầu hạn chế, etc...")
    private List<String> bidSelectionMethod;

    @ApiModelProperty(value = "This corresponds the negated bidSelectionMethod filter,"
            + " matches elements that are NOT in the list of Ids")
    private List<String> notBidSelectionMethod;

    @ApiModelProperty(value = "This will filter after tender.contrMethod.id, Values range from 1 to 5.")
    @EachPattern(regexp = "^[a-zA-Z0-9]*$")
    private List<String> contrMethod;

    @ApiModelProperty(value = "This will filter after planning.budget.projectLocation._id")
    private List<String> planningLoc;

    @ApiModelProperty(value = "This will filter after tender.items.deliveryLocation._id")
    private List<String> tenderLoc;

    @ApiModelProperty(value = "This will filter after tender.value.amount and will specify a minimum"
            + "Use /api/tenderValueInterval to get the minimum allowed.")
    private BigDecimal minTenderValue;

    @ApiModelProperty(value = "This will filter after tender.value.amount and will specify a maximum."
            + "Use /api/tenderValueInterval to get the maximum allowed.")
    private BigDecimal maxTenderValue;

    @ApiModelProperty(value = "This will filter after awards.value.amount and will specify a minimum"
            + "Use /api/awardValueInterval to get the minimum allowed.")
    private BigDecimal minAwardValue;

    @ApiModelProperty(value = "This will filter after awards.value.amount and will specify a maximum."
            + "Use /api/awardValueInterval to get the maximum allowed.")
    private BigDecimal maxAwardValue;

    @ApiModelProperty(value = "This will search after the City Id of the procuring entity."
            + "The field is organization.address.postalCode")
    private List<String> procuringEntityCityId;

    @ApiModelProperty(value = "Filters after tender.submissionMethod='electronicSubmission', also known as"
            + " eBids")
    private Boolean electronicSubmission;

    @ApiModelProperty(value = "This will search after the DepartmentId of the procuring entity."
            + "The field is organization.department._id")
    private List<Integer> procuringEntityDepartmentId;

    @ApiModelProperty(value = "This will search after the DepartmentId of the procuring entity."
            + "The field is organization.group._id")
    private List<Integer> procuringEntityGroupId;


    public DefaultFilterPagingRequest() {
        super();
    }

    public List<String> getBidTypeId() {
        return bidTypeId;
    }

    public void setBidTypeId(final List<String> bidTypeId) {
        this.bidTypeId = bidTypeId;
    }

    public List<String> getProcuringEntityId() {
        return procuringEntityId;
    }

    public void setProcuringEntityId(final List<String> procuringEntityId) {
        this.procuringEntityId = procuringEntityId;
    }

    public List<String> getBidSelectionMethod() {
        return bidSelectionMethod;
    }

    public void setBidSelectionMethod(final List<String> bidSelectionMethod) {
        this.bidSelectionMethod = bidSelectionMethod;
    }

    public List<String> getContrMethod() {
        return contrMethod;
    }

    public void setContrMethod(List<String> contrMethod) {
        this.contrMethod = contrMethod;
    }

    public List<String> getTenderLoc() {
        return tenderLoc;
    }

    public void setTenderLoc(final List<String> tenderDeliveryLocationGazetteerIdentifier) {
        this.tenderLoc = tenderDeliveryLocationGazetteerIdentifier;
    }

    public BigDecimal getMinTenderValue() {
        return minTenderValue;
    }

    public void setMinTenderValue(final BigDecimal minTenderValueAmount) {
        this.minTenderValue = minTenderValueAmount;
    }

    public BigDecimal getMaxTenderValue() {
        return maxTenderValue;
    }

    public void setMaxTenderValue(final BigDecimal maxTenderValueAmount) {
        this.maxTenderValue = maxTenderValueAmount;
    }

    public BigDecimal getMinAwardValue() {
        return minAwardValue;
    }

    public void setMinAwardValue(final BigDecimal minAwardValue) {
        this.minAwardValue = minAwardValue;
    }

    public BigDecimal getMaxAwardValue() {
        return maxAwardValue;
    }

    public void setMaxAwardValue(final BigDecimal maxAwardValue) {
        this.maxAwardValue = maxAwardValue;
    }

    public List<String> getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(final List<String> supplierId) {
        this.supplierId = supplierId;
    }

    public List<String> getNotBidTypeId() {
        return notBidTypeId;
    }

    public void setNotBidTypeId(List<String> notBidTypeId) {
        this.notBidTypeId = notBidTypeId;
    }

    public List<String> getNotProcuringEntityId() {
        return notProcuringEntityId;
    }

    public void setNotProcuringEntityId(List<String> notProcuringEntityId) {
        this.notProcuringEntityId = notProcuringEntityId;
    }

    public List<String> getProcuringEntityCityId() {
        return procuringEntityCityId;
    }
    public Boolean getElectronicSubmission() {
        return electronicSubmission;
    }

    public void setElectronicSubmission(Boolean electronicSubmission) {
        this.electronicSubmission = electronicSubmission;
    }

    public void setProcuringEntityCityId(List<String> procuringEntityCityId) {
        this.procuringEntityCityId = procuringEntityCityId;
    }

    public List<Integer> getProcuringEntityDepartmentId() {
        return procuringEntityDepartmentId;
    }

    public void setProcuringEntityDepartmentId(List<Integer> procuringEntityDepartmentId) {
        this.procuringEntityDepartmentId = procuringEntityDepartmentId;
    }

    public List<Integer> getProcuringEntityGroupId() {
        return procuringEntityGroupId;
    }

    public void setProcuringEntityGroupId(List<Integer> procuringEntityGroupId) {
        this.procuringEntityGroupId = procuringEntityGroupId;
    }

    public List<String> getNotBidSelectionMethod() {
        return notBidSelectionMethod;
    }

    public void setNotBidSelectionMethod(List<String> notBidSelectionMethod) {
        this.notBidSelectionMethod = notBidSelectionMethod;
    }

    public List<String> getPlanningLoc() {
        return planningLoc;
    }

    public void setPlanningLoc(List<String> planningLoc) {
        this.planningLoc = planningLoc;
    }
}