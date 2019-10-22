package org.devgateway.ocvn.persistence.mongo.dao;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 *
 * @author mpostelnicu
 */
@Document(collection = "organization")
public class VNOrganization extends Organization {

    /**
     * This is the group captured from GROUP_NO column
     */
    private OrgGroup group;
    
    /**
     * This is the department of the org, captured from DEPARTMENT_NO column
     */
    private OrgDepartment department;

    public OrgGroup getGroup() {
        return group;
    }

    public void setGroup(OrgGroup group) {
        this.group = group;
    }

    public OrgDepartment getDepartment() {
        return department;
    }

    public void setDepartment(OrgDepartment department) {
        this.department = department;
    }

}
