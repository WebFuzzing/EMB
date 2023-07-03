package org.cbioportal.genome_nexus.model;

import org.springframework.data.mongodb.core.mapping.Field;

public class GeneralPopulationStats {
    @Field("counts")
    private SignalPopulationStats counts;

    @Field("frequencies")
    private SignalPopulationStats frequencies;

    public SignalPopulationStats getCounts() {
        return counts;
    }

    public SignalPopulationStats getFrequencies() {
        return frequencies;
    }

    public void setFrequencies(SignalPopulationStats frequencies) {
        this.frequencies = frequencies;
    }

    public void setCounts(SignalPopulationStats counts) {
        this.counts = counts;
    }
}
