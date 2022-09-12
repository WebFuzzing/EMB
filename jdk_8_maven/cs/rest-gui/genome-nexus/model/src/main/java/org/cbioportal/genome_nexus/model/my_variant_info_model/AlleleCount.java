package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleCount
{

    @Field(value = "ac")
    private Integer alleleCount;

    @Field(value = "ac_afr")
    private Integer alleleCountAfrican;

    @Field(value = "ac_amr")
    private Integer alleleCountLatino;

    @Field(value = "ac_asj")
    private Integer alleleCountAshkenaziJewish;

    @Field(value = "ac_eas")
    private Integer alleleCountEastAsian;

    @Field(value = "ac_fin")
    private Integer alleleCountEuropeanFinnish;

    @Field(value = "ac_nfe")
    private Integer alleleCountEuropeanNonFinnish;

    @Field(value = "ac_oth")
    private Integer alleleCountOther;

    @Field(value = "ac_sas")
    private Integer alleleCountSouthAsian;

    public Integer getAlleleCount()
    {
        return alleleCount;
    }

    public void setAlleleCount(Integer alleleCount)
    {
        this.alleleCount = alleleCount;
    }

    public Integer getAlleleCountAfrican()
    {
        return alleleCountAfrican;
    }

    public void setAlleleCountAfrican(Integer alleleCountAfrican)
    {
        this.alleleCountAfrican = alleleCountAfrican;
    }

    public Integer getAlleleCountLatino()
    {
        return alleleCountLatino;
    }

    public void setAlleleCountLatino(Integer alleleCountLatino)
    {
        this.alleleCountLatino = alleleCountLatino;
    }

    public Integer getAlleleCountAshkenaziJewish()
    {
        return alleleCountAshkenaziJewish;
    }

    public void setAlleleCountAshkenaziJewish(Integer alleleCountAshkenaziJewish)
    {
        this.alleleCountAshkenaziJewish = alleleCountAshkenaziJewish;
    }

    public Integer getAlleleCountEastAsian()
    {
        return alleleCountEastAsian;
    }

    public void setAlleleCountEastAsian(Integer alleleCountEastAsian)
    {
        this.alleleCountEastAsian = alleleCountEastAsian;
    }

    public Integer getAlleleCountEuropeanFinnish()
    {
        return alleleCountEuropeanFinnish;
    }

    public void setAlleleCountEuropeanFinnishn(Integer alleleCountEuropeanFinnish)
    {
        this.alleleCountEuropeanFinnish = alleleCountEuropeanFinnish;
    }

    public Integer getAlleleCountEuropeanNonFinnish()
    {
        return alleleCountEuropeanNonFinnish;
    }

    public void setAlleleCountEuropeanNonFinnish(Integer alleleCountEuropeanNonFinnish)
    {
        this.alleleCountEuropeanNonFinnish = alleleCountEuropeanNonFinnish;
    }

    public Integer getAlleleCountOther()
    {
        return alleleCountOther;
    }

    public void setAlleleCountOther(Integer alleleCountOther)
    {
        this.alleleCountOther = alleleCountOther;
    }

    public Integer getAlleleCountSouthAsian()
    {
        return alleleCountSouthAsian;
    }

    public void setAlleleCountSouthAsian(Integer alleleCountSouthAsian)
    {
        this.alleleCountSouthAsian = alleleCountSouthAsian;
    }

}