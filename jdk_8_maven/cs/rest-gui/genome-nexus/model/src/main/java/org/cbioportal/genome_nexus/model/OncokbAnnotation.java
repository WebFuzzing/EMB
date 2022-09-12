package org.cbioportal.genome_nexus.model;

import java.util.ArrayList;
import java.util.List;
import org.oncokb.client.IndicatorQueryResp;

import org.springframework.data.mongodb.core.mapping.Field;

public class OncokbAnnotation
{
    @Field(value = "license")
    private String license;

    @Field(value = "annotation")
    private IndicatorQueryResp annotation;

    public OncokbAnnotation()
    {
        this.license = "https://www.oncokb.org/terms";
        this.annotation = new IndicatorQueryResp();
    }

    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    public IndicatorQueryResp getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(IndicatorQueryResp annotation)
    {
        this.annotation = annotation;
    }
}
