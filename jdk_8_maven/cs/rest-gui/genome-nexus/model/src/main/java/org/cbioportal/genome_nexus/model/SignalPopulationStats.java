package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class SignalPopulationStats {
    @Field("impact")
    private Double impact;

    @Field("eur")
    private Double eur;

    @Field("afr")
    private Double afr;

    @Field("asn")
    private Double asn;

    @Field("asj")
    private Double asj;

    @Field("oth")
    private Double oth;

    public Double getImpact() {
        return impact;
    }

    public Double getOth() {
        return oth;
    }

    public void setOth(Double oth) {
        this.oth = oth;
    }

    public Double getAsj() {
        return asj;
    }

    public void setAsj(Double asj) {
        this.asj = asj;
    }

    public Double getAsn() {
        return asn;
    }

    public void setAsn(Double asn) {
        this.asn = asn;
    }

    public Double getAfr() {
        return afr;
    }

    public void setAfr(Double afr) {
        this.afr = afr;
    }

    public Double getEur() {
        return eur;
    }

    public void setEur(Double eur) {
        this.eur = eur;
    }

    public void setImpact(Double impact) {
        this.impact = impact;
    }
}
