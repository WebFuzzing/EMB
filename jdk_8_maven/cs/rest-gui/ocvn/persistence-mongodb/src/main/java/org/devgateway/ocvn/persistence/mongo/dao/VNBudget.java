/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import java.util.ArrayList;
import java.util.List;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Budget;
import org.devgateway.ocds.persistence.mongo.Classification;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

/**
 * @author mpostelnicu
 *
 */
public class VNBudget extends Budget {
    private String bidPlanProjectStyle;

    private String bidPlanProjectType;

    private Classification projectClassification = new Classification();
        
    private Amount projectAmount;

    @ExcelExport
    private List<VNLocation> projectLocation = new ArrayList<>();

    public Classification getProjectClassification() {
        return projectClassification;
    }

    public void setProjectClassification(Classification projectClassification) {
        this.projectClassification = projectClassification;
    }

    public List<VNLocation> getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(List<VNLocation> projectLocation) {
        this.projectLocation = projectLocation;
    }

    public String getBidPlanProjectStyle() {
        return bidPlanProjectStyle;
    }

    public void setBidPlanProjectStyle(String bidPlanProjectStyle) {
        this.bidPlanProjectStyle = bidPlanProjectStyle;
    }

    public String getBidPlanProjectType() {
        return bidPlanProjectType;
    }

    public void setBidPlanProjectType(String bidPlanProjectType) {
        this.bidPlanProjectType = bidPlanProjectType;
    }

    public Amount getProjectAmount() {
        return projectAmount;
    }

    public void setProjectAmount(Amount projectAmount) {
        this.projectAmount = projectAmount;
    }

}
