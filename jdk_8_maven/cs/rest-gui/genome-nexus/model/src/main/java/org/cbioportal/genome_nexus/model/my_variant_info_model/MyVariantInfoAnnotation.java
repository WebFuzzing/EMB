package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class MyVariantInfoAnnotation
{

    @Field(value = "license")
    private String license;

    @Field(value = "annotation")
    private MyVariantInfo annotation;


    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    public MyVariantInfo getAnnotation()
    {
        return annotation;
    }

    public void setAnnotation(MyVariantInfo annotation)
    {
        this.annotation = annotation;
    }

}