package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Ann
{

    @Field(value = "distance_to_feature")
    private String distanceToFeature;

    @Field(value = "effect")
    private String effect;

    @Field(value = "feature_id")
    private String featureId;

    @Field(value = "feature_type")
    private String featureType;

    @Field(value = "gene_id")
    private String geneId;

    @Field(value = "gene_name")
    private String geneName;

    @Field(value = "hgvs_coding")
    private String hgvsCoding;

    @Field(value = "putative_impact")
    private String putativeImpact;

    @Field(value = "transcript_biotype")
    private String transcriptBiotype;


    public String getDistanceToFeature()
    {
        return distanceToFeature;
    }

    public void setDistanceToFeature(String distanceToFeature)
    {
        this.distanceToFeature = distanceToFeature;
    }
    public String getEffect()
    {
        return effect;
    }

    public void setEffect(String effect)
    {
        this.effect = effect;
    }
    public String getFeatureId()
    {
        return featureId;
    }

    public void setFeatureId(String featureId)
    {
        this.featureId = featureId;
    }
    public String getFeatureType()
    {
        return featureType;
    }

    public void setFeatureType(String featureType)
    {
        this.featureType = featureType;
    }
    public String getGeneId()
    {
        return geneId;
    }

    public void setGeneId(String geneId)
    {
        this.geneId = geneId;
    }
    public String getGeneName()
    {
        return geneName;
    }

    public void setGeneName(String geneName)
    {
        this.geneName = geneName;
    }
    public String getHgvsCoding()
    {
        return hgvsCoding;
    }

    public void setHgvsCoding(String hgvsCoding)
    {
        this.hgvsCoding = hgvsCoding;
    }
    public String getPutativeImpact()
    {
        return putativeImpact;
    }

    public void setPutativeImpact(String putativeImpact)
    {
        this.putativeImpact = putativeImpact;
    }
    public String getTranscriptBiotype()
    {
        return transcriptBiotype;
    }

    public void setTranscriptBiotype(String transcriptBiotype)
    {
        this.transcriptBiotype = transcriptBiotype;
    }

}