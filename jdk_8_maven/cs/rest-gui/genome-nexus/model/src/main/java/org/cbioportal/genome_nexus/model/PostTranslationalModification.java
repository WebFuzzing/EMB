package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection="ptm.experimental")
public class PostTranslationalModification
{
    @Field(value = "uniprot_entry")
    private String uniprotEntry;

    @Field(value = "uniprot_accession")
    private String uniprotAccession;

    @Field(value = "ensembl_transcript_ids")
    private List<String> ensemblTranscriptIds;

    @Field(value = "position")
    private Integer position;

    @Field(value = "type")
    private String type;

    @Field(value = "pubmed_ids")
    private List<String> pubmedIds;

    @Field(value = "sequence")
    private String sequence;

    public String getUniprotEntry() {
        return uniprotEntry;
    }

    public void setUniprotEntry(String uniprotEntry) {
        this.uniprotEntry = uniprotEntry;
    }

    public String getUniprotAccession() {
        return uniprotAccession;
    }

    public void setUniprotAccession(String uniprotAccession) {
        this.uniprotAccession = uniprotAccession;
    }

    public List<String> getEnsemblTranscriptIds() {
        return ensemblTranscriptIds;
    }

    public void setEnsemblTranscriptIds(List<String> ensemblTranscriptIds) {
        this.ensemblTranscriptIds = ensemblTranscriptIds;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getPubmedIds() {
        return pubmedIds;
    }

    public void setPubmedIds(List<String> pubmedIds) {
        this.pubmedIds = pubmedIds;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }
}
