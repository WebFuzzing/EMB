package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Mutdb
{

    @Field(value = "rsid")
    private String rsid;

    @Field(value = "ref")
    private String ref;

    @Field(value = "alt")
    private String alt;

    @Field(value = "uniprot_id")
    private String uniprotId;

    @Field(value = "mutpred_score")
    private Double mutpredScore;

    @Field(value = "cosmic_id")
    private String cosmicId;

    @Field(value = "chrom")
    private String chrom;

    @Field(value = "hg19")
    private Hg19 hg19;


    public String getRsid()
    {
        return rsid;
    }

    public void setRsid(String rsid)
    {
        this.rsid = rsid;
    }
    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }
    public String getAlt()
    {
        return alt;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }
    public String getUniprotId()
    {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId)
    {
        this.uniprotId = uniprotId;
    }
    public Double getMutpredScore()
    {
        return mutpredScore;
    }

    public void setMutpredScore(Double mutpredScore)
    {
        this.mutpredScore = mutpredScore;
    }
    public String getCosmicId()
    {
        return cosmicId;
    }

    public void setCosmicId(String cosmicId)
    {
        this.cosmicId = cosmicId;
    }
    public String getChrom()
    {
        return chrom;
    }

    public void setChrom(String chrom)
    {
        this.chrom = chrom;
    }
    public Hg19 getHg19()
    {
        return hg19;
    }

    public void setHg19(Hg19 hg19)
    {
        this.hg19 = hg19;
    }

}