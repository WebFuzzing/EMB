package org.cbioportal.genome_nexus.model;

import java.util.ArrayList;
import java.util.List;

public class PtmAnnotation
{
    private String license;
    private List<List<PostTranslationalModification>> annotation;

    public PtmAnnotation()
    {
        this.license = "https://www.ncbi.nlm.nih.gov/pubmed/26578568";
        this.annotation = new ArrayList<>();
    }

    public List<List<PostTranslationalModification>> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(List<List<PostTranslationalModification>> annotation) {
        this.annotation = annotation;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
