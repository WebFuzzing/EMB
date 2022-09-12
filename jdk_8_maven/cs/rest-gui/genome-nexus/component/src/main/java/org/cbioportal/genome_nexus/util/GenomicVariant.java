package org.cbioportal.genome_nexus.util;

public class GenomicVariant {

    public enum Type {
        SUBSTITUTION,
        INSERTION,
        DELETION,
        INDEL
        // hgvs DUPLICATION is not supported in this model
    }

    static enum RefType {
        GENOMIC,
        MITOCHONDRIAL // for future development (not yet implemented)
    }

    private String chromosome;
    private RefType refType;
    private Integer start;
    private Integer end;
    private Type type;
    private String ref;
    private String alt;

    public GenomicVariant() {
        this.chromosome = null;
        this.refType = null;
        this.start = null;
        this.end = null;
        this.type = null;
        this.ref = null;
        this.alt = null;
    }

    public GenomicVariant (String chromosome, RefType refType, Integer start, Integer end, Type type, String ref, String alt) {
        this.chromosome = chromosome;
        this.refType = refType;
        this.start = start;
        this.end = end;
        this.type = type;
        this.ref = ref;
        this.alt = alt;
    }

    public String getChromosome() {
        return this.chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public RefType getRefType() {
        return this.refType;
    }

    public void setRefType(RefType refType) {
        this.refType = refType;
    }

    public Integer getStart() {
        return this.start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Type getType() {
        return this.type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Integer getEnd() {
        return this.end;
    }

    public void setEnd(Integer end) {
        this.end = end;
    }

    public String getRef() {
        return this.ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getAlt() {
        return this.alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }
}
