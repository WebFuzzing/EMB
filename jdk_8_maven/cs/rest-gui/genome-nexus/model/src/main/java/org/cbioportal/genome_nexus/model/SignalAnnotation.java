package org.cbioportal.genome_nexus.model;


import java.util.Collections;
import java.util.List;

public class SignalAnnotation
{
    private String license;

    private List<SignalMutation> annotation;

    public SignalAnnotation()
    {
        this.license = "https://www.signaldb.org/about";
        this.annotation = Collections.emptyList();
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    public List<SignalMutation> getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(List<SignalMutation> annotation)
    {
        this.annotation = annotation;
    }
}
