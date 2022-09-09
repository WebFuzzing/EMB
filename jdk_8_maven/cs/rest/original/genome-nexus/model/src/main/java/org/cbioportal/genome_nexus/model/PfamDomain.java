package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Selcuk Onur Sumer
 */
@Document(collection="pfam.domain")
public class PfamDomain
{
    @Field(value = "pfamA_acc")
    private String pfamAccession;

    @Field(value = "pfamA_id")
    private String name;

    @Field(value = "description")
    private String description;

    public String getPfamAccession() {
        return pfamAccession;
    }

    public void setPfamAccession(String pfamAccession) {
        this.pfamAccession = pfamAccession;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
