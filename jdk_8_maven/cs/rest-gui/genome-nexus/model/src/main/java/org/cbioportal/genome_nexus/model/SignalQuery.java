package org.cbioportal.genome_nexus.model;

public class SignalQuery
{
    private SignalQueryType queryType;
    private SignalMatchType matchType;
    private String hugoSymbol;
    private String alteration;
    private String region;
    private String variant;
    private String description;

    public SignalQueryType getQueryType() {
        return queryType;
    }

    public void setQueryType(SignalQueryType queryType) {
        this.queryType = queryType;
    }

    public SignalMatchType getMatchType() {
        return matchType;
    }

    public void setMatchType(SignalMatchType matchType) {
        this.matchType = matchType;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public String getAlteration() {
        return alteration;
    }

    public void setAlteration(String alteration) {
        this.alteration = alteration;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
