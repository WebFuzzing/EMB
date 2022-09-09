package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class HrdScore {
    @Field("lst")
    private Double lst;

    @Field("ntelomeric_ai")
    private Double ntelomericAi;

    @Field("fraction_loh")
    private Double fractionLoh;

    public Double getLst() {
        return lst;
    }

    public Double getFractionLoh() {
        return fractionLoh;
    }

    public void setFractionLoh(Double fractionLoh) {
        this.fractionLoh = fractionLoh;
    }

    public Double getNtelomericAi() {
        return ntelomericAi;
    }

    public void setNtelomericAi(Double ntelomericAi) {
        this.ntelomericAi = ntelomericAi;
    }

    public void setLst(Double lst) {
        this.lst = lst;
    }

}
