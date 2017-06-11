/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Planning;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

import java.util.Date;

/**
 * @author mpostelnicu Extension of {@link Planning} to allow extra Vietnam-specific
 *         fields
 */
public class VNPlanning extends Planning {
    private Date bidPlanProjectDateIssue;

    private String bidPlanProjectCompanyIssue;

    private Integer bidPlanProjectFund;

    @ExcelExport
    private Date bidPlanProjectDateApprove;

    @ExcelExport
    private String bidNo;

    public Date getBidPlanProjectDateIssue() {
        return bidPlanProjectDateIssue;
    }

    public void setBidPlanProjectDateIssue(final Date bidPlanProjectDateIssue) {
        this.bidPlanProjectDateIssue = bidPlanProjectDateIssue;
    }

    public String getBidPlanProjectCompanyIssue() {
        return bidPlanProjectCompanyIssue;
    }

    public void setBidPlanProjectCompanyIssue(final String bidPlanProjectCompanyIssue) {
        this.bidPlanProjectCompanyIssue = bidPlanProjectCompanyIssue;
    }



    public Integer getBidPlanProjectFund() {
        return bidPlanProjectFund;
    }

    public void setBidPlanProjectFund(final Integer bidPlanProjectFund) {
        this.bidPlanProjectFund = bidPlanProjectFund;
    }

    public Date getBidPlanProjectDateApprove() {
        return bidPlanProjectDateApprove;
    }

    public void setBidPlanProjectDateApprove(final Date bidPlanProjectDateApprove) {
        this.bidPlanProjectDateApprove = bidPlanProjectDateApprove;
    }

    public String getBidNo() {
        return bidNo;
    }

    public void setBidNo(final String bidNo) {
        this.bidNo = bidNo;
    }
}
