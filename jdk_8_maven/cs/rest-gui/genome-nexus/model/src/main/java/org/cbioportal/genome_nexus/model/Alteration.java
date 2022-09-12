package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Alteration
{
    @Field(value = "hugoSymbol")
    private String hugoSymbol;

    @Field(value = "entrezGeneId")
    private Integer entrezGeneId;

    @Field(value = "alteration")
    private String alteration;

    @Field(value = "consequence")
    private String consequence;

    @Field(value = "proteinStart")
    private Integer proteinStart;

    @Field(value = "proteinEnd")
    private Integer proteinEnd;

    @Field(value = "tumorType")
    private String tumorType;

    public Alteration(String hugoSymbol, Integer entrezGeneId, String alteration,
                        String consequence, Integer proteinStart, Integer proteinEnd, String tumorType)
    {
        this.hugoSymbol = hugoSymbol;
        this.entrezGeneId = entrezGeneId;
        this.alteration = alteration;
        this.consequence = consequence;
        this.proteinStart = proteinStart;
        this.proteinEnd = proteinEnd;
        this.tumorType = tumorType;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public Integer getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(Integer entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getAlteration() {
        return alteration;
    }

    public void setAlteration(String alteration) {
        this.alteration = alteration;
    }

    public String getConsequence() {
        return consequence;
    }

    public void setConsequence(String consequence) {
        this.consequence = consequence;
    }

    public Integer getProteinStart() {
        return proteinStart;
    }

    public void setProteinStart(Integer proteinStart) {
        this.proteinStart = proteinStart;
    }

    public Integer getProteinEnd() {
        return proteinEnd;
    }

    public void setProteinEnd(Integer proteinEnd) {
        this.proteinEnd = proteinEnd;
    }

    public String getTumorType() {
        return tumorType;
    }

    public void setTumorType(String tumorType) {
        this.tumorType = tumorType;
    }
}
