package org.cbioportal.genome_nexus.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class GenomicLocation
{
    private String chromosome;
    private Integer start;
    private Integer end;
    private String referenceAllele;
    private String variantAllele;
    @JsonIgnore
    private String originalInput;

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getEnd() {
        return end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getReferenceAllele() {
        return referenceAllele;
    }

    public void setReferenceAllele(String referenceAllele) {
        this.referenceAllele = referenceAllele;
    }

    public String getVariantAllele() {
        return variantAllele;
    }

    public void setVariantAllele(String variantAllele) {
        this.variantAllele = variantAllele;
    }

    public String getOriginalInput() {
        return originalInput;
    }

    public void setOriginalInput(String originalInput) {
        this.originalInput = originalInput;
    }

    public String toString() {
        return this.getChromosome() + "," + this.getStart() + "," + this.getEnd() + "," + this.getReferenceAllele() + "," + this.getVariantAllele();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof GenomicLocation)) {
            return false;
        }
        GenomicLocation gl = (GenomicLocation)obj;
        // compare start
        if (this.start == null && gl.start != null) {
            return false;
        }
        if (!this.start.equals(gl.start)) {
            return false;
        }
        // compare end
        if (this.end == null && gl.end != null) {
            return false;
        }
        if (!this.end.equals(gl.end)) {
            return false;
        }
        // compare chromosome
        if (this.chromosome == null && gl.chromosome != null) {
            return false;
        }
        if (!this.chromosome.equals(gl.chromosome)) {
            return false;
        }
        // compare referenceAllele
        if (this.referenceAllele == null && gl.referenceAllele != null) {
            return false;
        }
        if (!this.referenceAllele.equals(gl.referenceAllele)) {
            return false;
        }
        // compare variantAllele
        if (this.variantAllele == null && gl.variantAllele != null) {
            return false;
        }
        if (!this.variantAllele.equals(gl.variantAllele)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (this == null) {
            return 0;
        }
        int hash = 7;
        hash =  31 * hash + (start == null ? 0 : start.hashCode());
        hash =  31 * hash + (end == null ? 0 : end.hashCode());
        hash =  31 * hash + (chromosome == null ? 0 : chromosome.hashCode());
        hash =  31 * hash + (referenceAllele == null ? 0 : referenceAllele.hashCode());
        hash =  31 * hash + (variantAllele == null ? 0 : variantAllele.hashCode());
        return hash;
    }

}
