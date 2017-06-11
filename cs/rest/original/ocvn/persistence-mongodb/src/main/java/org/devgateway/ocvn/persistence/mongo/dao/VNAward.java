/**
 *
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import java.util.Date;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Award;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

/**
 * @author mpostelnicu
 * Extension of {@link Award} to allow extra Vietnam-specific fields
 */
public class VNAward extends Award {
    private String ineligibleYN;

    private String ineligibleRson;

    private Integer bidOpenRank;
    
    private Date alternateDate;
    
    private Date publishedDate;

    @ExcelExport
    private Integer bidType;

    @ExcelExport
    private Integer bidSuccMethod;

    private Amount bidPrice;

    private String contractTime;

    public String getIneligibleYN() {
        return ineligibleYN;
    }

    public void setIneligibleYN(String inelibigleYN) {
        this.ineligibleYN = inelibigleYN;
    }

    public Integer getBidOpenRank() {
        return bidOpenRank;
    }

    public void setBidOpenRank(Integer bidOpenRank) {
        this.bidOpenRank = bidOpenRank;
    }

    public Integer getBidType() {
        return bidType;
    }

    public void setBidType(Integer bidType) {
        this.bidType = bidType;
    }

    public Integer getBidSuccMethod() {
        return bidSuccMethod;
    }

    public void setBidSuccMethod(Integer bidSuccMethod) {
        this.bidSuccMethod = bidSuccMethod;
    }

    public Amount getBidPrice() {
        return bidPrice;
    }

    public void setBidPrice(Amount bidPrice) {
        this.bidPrice = bidPrice;
    }

    public String getContractTime() {
        return contractTime;
    }

    public void setContractTime(String contractTime) {
        this.contractTime = contractTime;
    }

    public String getIneligibleRson() {
        return ineligibleRson;
    }

    public void setIneligibleRson(String ineligibleRson) {
        this.ineligibleRson = ineligibleRson;
    }

    public Date getAlternateDate() {
        return alternateDate;
    }

    public void setAlternateDate(Date alternateDate) {
        this.alternateDate = alternateDate;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

}
