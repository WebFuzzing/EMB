/**
 * 
 */
package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Amount;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.excel.annotation.ExcelExport;

/**
 * @author mpostelnicu
 *
 */
public class VNTendererOrganization extends Organization {
    @ExcelExport
    private Amount bidValue;

    public VNTendererOrganization() {

    }

    public VNTendererOrganization(Organization organization) {
        this.setId(organization.getId());
        this.setAddress(organization.getAddress());
        this.setContactPoint(organization.getContactPoint());
        this.setIdentifier(organization.getIdentifier());
        this.setName(organization.getName());
        this.setRoles(organization.getRoles());
    }

    public Amount getBidValue() {
        return bidValue;
    }

    public void setBidValue(Amount bidValue) {
        this.bidValue = bidValue;
    }

}
