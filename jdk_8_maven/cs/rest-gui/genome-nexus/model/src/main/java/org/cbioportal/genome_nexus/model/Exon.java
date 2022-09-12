package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Exon
{
    @Field(value="id")
    private String exonId;

    @Field(value="start")
    private Integer exonStart;

    @Field(value="end")
    private Integer exonEnd;

    @Field(value="rank")
    private Integer rank;

    @Field(value="strand")
    private Integer strand;

    @Field(value="version")
    private Integer version;

    public String getExonId() {
        return this.exonId;
    }

    public void setExonId(String exonId) {
        this.exonId = exonId;
    }

    public Integer getExonStart() {
        return this.exonStart;
    }

    public void setExonStart(Integer exonStart) {
        this.exonStart = exonStart;
    }

    public Integer getExonEnd() {
        return this.exonEnd;
    }

    public void setExonEnd(Integer exonEnd) {
        this.exonEnd = exonEnd;
    }

    public Integer getRank() {
        return this.rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getStrand() {
        return this.strand;
    }

    public void setStrand(Integer strand) {
        this.strand = strand;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}
