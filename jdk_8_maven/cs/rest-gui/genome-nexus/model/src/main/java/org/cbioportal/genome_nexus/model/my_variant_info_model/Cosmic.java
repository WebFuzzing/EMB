package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Cosmic
{
    @Field(value = "_license")
    private String license;

    @Field(value = "alt")
    private String alt;

    @Field(value = "chrom")
    private String chrom;

    @Field(value = "cosmic_id")
    private String cosmicId;

    @Field(value = "hg19")
    private Hg19 hg19;

    @Field(value = "mut_freq")
    private Double mutFreq;

    @Field(value = "mut_nt")
    private String mutNt;

    @Field(value = "ref")
    private String ref;

    @Field(value = "tumor_site")
    private String tumorSite;


    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    public String getAlt()
    {
        return alt;
    }

    public void setAlt(String alt)
    {
        this.alt = alt;
    }
    public String getChrom()
    {
        return chrom;
    }

    public void setChrom(String chrom)
    {
        this.chrom = chrom;
    }
    public String getCosmicId()
    {
        return cosmicId;
    }

    public void setCosmicId(String cosmicId)
    {
        this.cosmicId = cosmicId;
    }
    public Hg19 getHg19()
    {
        return hg19;
    }

    public void setHg19(Hg19 hg19)
    {
        this.hg19 = hg19;
    }
    public Double getMutFreq()
    {
        return mutFreq;
    }

    public void setMutFreq(Double mutFreq)
    {
        this.mutFreq = mutFreq;
    }
    public String getMutNt()
    {
        return mutNt;
    }

    public void setMutNt(String mutNt)
    {
        this.mutNt = mutNt;
    }
    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }
    public String getTumorSite()
    {
        return tumorSite;
    }

    public void setTumorSite(String tumorSite)
    {
        this.tumorSite = tumorSite;
    }

}
