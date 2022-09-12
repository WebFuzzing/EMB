package org.cbioportal.genome_nexus.model.my_variant_info_model;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Field;

public class Dbsnp
{
    @Field(value = "_license")
    private String license;

    @Field(value = "allele_origin")
    private String alleleOrigin;

    @Field(value = "alleles")
    private List<Alleles> alleles;

    @Field(value = "alt")
    private String alt;

    @Field(value = "chrom")
    private String chrom;

    @Field(value = "_class")
    private String _class;

    @Field(value = "dbsnp_build")
    private Integer dbsnpBuild;

    @Field(value = "flags")
    private List<String> flags;

    @Field(value = "hg19")
    private Hg19 hg19;

    @Field(value = "ref")
    private String ref;

    @Field(value = "rsid")
    private String rsid;

    @Field(value = "validated")
    private Boolean validated;

    @Field(value = "var_subtype")
    private String varSubtype;

    @Field(value = "vartype")
    private String vartype;


    public String getLicense()
    {
        return license;
    }

    public void setLicense(String license)
    {
        this.license = license;
    }
    public String getAlleleOrigin()
    {
        return alleleOrigin;
    }

    public void setAlleleOrigin(String alleleOrigin)
    {
        this.alleleOrigin = alleleOrigin;
    }
    public List<Alleles> getAlleles()
    {
        return alleles;
    }

    public void setAlleles(List<Alleles> alleles)
    {
        this.alleles = alleles;
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
    public String get_class()
    {
        return _class;
    }

    public void set_class(String _class)
    {
        this._class = _class;
    }
    public Integer getDbsnpBuild()
    {
        return dbsnpBuild;
    }

    public void setDbsnpBuild(Integer dbsnpBuild)
    {
        this.dbsnpBuild = dbsnpBuild;
    }
    public List<String> getFlags()
    {
        return flags;
    }

    public void setFlags(List<String> flags)
    {
        this.flags = flags;
    }

    public Hg19 getHg19()
    {
        return hg19;
    }

    public void setHg19(Hg19 hg19)
    {
        this.hg19 = hg19;
    }
    public String getRef()
    {
        return ref;
    }

    public void setRef(String ref)
    {
        this.ref = ref;
    }
    public String getRsid()
    {
        return rsid;
    }

    public void setRsid(String rsid)
    {
        this.rsid = rsid;
    }
    public Boolean getValidated()
    {
        return validated;
    }

    public void setValidated(Boolean validated)
    {
        this.validated = validated;
    }
    public String getVarSubtype()
    {
        return varSubtype;
    }

    public void setVarSubtype(String varSubtype)
    {
        this.varSubtype = varSubtype;
    }
    public String getVartype()
    {
        return vartype;
    }

    public void setVartype(String vartype)
    {
        this.vartype = vartype;
    }

}
