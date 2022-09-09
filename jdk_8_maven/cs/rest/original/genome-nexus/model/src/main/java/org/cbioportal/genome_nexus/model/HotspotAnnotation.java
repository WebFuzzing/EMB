package org.cbioportal.genome_nexus.model;

import java.util.ArrayList;
import java.util.List;

public class HotspotAnnotation {

    private String license;
    private List<List<Hotspot>> annotation;

    public HotspotAnnotation()
    {
        this.license = "https://opendatacommons.org/licenses/odbl/1.0/";
        this.annotation = new ArrayList<>();
    }

    public void setLicense(String license) { this.license = license; }

    public String getLicense() { return this.license; }

    public List<List<Hotspot>> getAnnotation() { return this.annotation; }

    public void setAnnotation(List<List<Hotspot>> annotation) { this.annotation = annotation; }

}
