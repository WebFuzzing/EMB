package org.cbioportal.genome_nexus.model;

public class ClinvarAnnotation {
    private Clinvar annotation;

    public ClinvarAnnotation(Clinvar annotation) {
        this.annotation = annotation;
    }

    public Clinvar getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Clinvar annotation) {
        this.annotation = annotation;
    }
}
