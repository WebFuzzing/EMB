/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Tender;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

import java.util.Date;

/**
 * @author mpostelnicu Eerxtension of {@link Tender} to allow extra Vietnam-specific
 *         fields
 */
public class VNTender extends Tender {
    @ExcelExport
    private Integer bidMethod;

    @ExcelExport
    private ContrMethod contrMethod;

    private String approveState;

    private String cancelYN;

    private String modYn;

    @ExcelExport
    private String procurementMethodDetails;

    @ExcelExport
    private String publicationMethod;

    private String cancellationRationale;

    @ExcelExport
    private Date bidOpenDt;

    public String getCancellationRationale() {
        return cancellationRationale;
    }

    public void setCancellationRationale(String cancellationRationale) {
        this.cancellationRationale = cancellationRationale;
    }

    public String getPublicationMethod() {
        return publicationMethod;
    }

    public void setPublicationMethod(String publicationMethod) {
        this.publicationMethod = publicationMethod;
    }

    public ContrMethod getContrMethod() {
        return contrMethod;
    }

    public void setContrMethod(ContrMethod contrMethod) {
        this.contrMethod = contrMethod;
    }

    public String getProcurementMethodDetails() {
        return procurementMethodDetails;
    }

    public void setProcurementMethodDetails(String procurementMethodDetails) {
        this.procurementMethodDetails = procurementMethodDetails;
    }

    public Integer getBidMethod() {
        return bidMethod;
    }

    public void setBidMethod(final Integer bidMethod) {
        this.bidMethod = bidMethod;
    }

    public String getApproveState() {
        return approveState;
    }

    public void setApproveState(final String approveState) {
        this.approveState = approveState;
    }

    public String getCancelYN() {
        return cancelYN;
    }

    public void setCancelYN(final String cancelYN) {
        this.cancelYN = cancelYN;
    }

    public String getModYn() {
        return modYn;
    }

    public void setModYn(final String modYn) {
        this.modYn = modYn;
    }

    public Date getBidOpenDt() {
        return bidOpenDt;
    }

    public void setBidOpenDt(final Date bidOpenDt) {
        this.bidOpenDt = bidOpenDt;
    }

}
