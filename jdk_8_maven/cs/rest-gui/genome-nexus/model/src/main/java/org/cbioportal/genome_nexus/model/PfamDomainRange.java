package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class PfamDomainRange
{
    @Field(value="pfam_domain_id")
    private String pfamDomainId;

    @Field(value="pfam_domain_start")
    private Integer pfamDomainStart;

    @Field(value="pfam_domain_end")
    private Integer pfamDomainEnd;

    public String getPfamDomainId() {
        return this.pfamDomainId;
    }

    public void setPfamDomainId(String pfamDomainId) {
        this.pfamDomainId = pfamDomainId;
    }

    public Integer getPfamDomainStart() {
        return this.pfamDomainStart;
    }

    public void setPfamDomainStart(Integer pfamDomainStart) {
        this.pfamDomainStart = pfamDomainStart;
    }

    public Integer getPfamDomainEnd() {
        return this.pfamDomainEnd;
    }

    public void setPfamDomainEnd(Integer pfamDomainEnd) {
        this.pfamDomainStart = pfamDomainEnd;
    }
}