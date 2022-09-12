package org.cbioportal.genome_nexus.model;

import java.util.List;

public class CuriousCases {
    private String genomicLocation;
    private String comment;
    private List<Integer> pubmedIds;
    private String hugoGeneSymbol;

    public String getComment() {
        return comment;
    }

    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    public String getGenomicLocation() {
        return genomicLocation;
    }

    public List<Integer> getPubmedIds() {
        return pubmedIds;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setGenomicLocation(String genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    public void setPubmedIds(List<Integer> pubmedIds) {
        this.pubmedIds = pubmedIds;
    }
}
