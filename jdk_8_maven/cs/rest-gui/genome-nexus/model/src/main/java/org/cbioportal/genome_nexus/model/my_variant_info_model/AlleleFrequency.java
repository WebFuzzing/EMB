package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleFrequency
{

    @Field(value = "af")
    private Double alleleFrequency;

    @Field(value = "af_afr")
    private Double alleleFrequencyAfrican;

    @Field(value = "af_amr")
    private Double alleleFrequencyLatino;

    @Field(value = "af_asj")
    private Double alleleFrequencyAshkenaziJewish;

    @Field(value = "af_eas")
    private Double alleleFrequencyEastAsian;

    @Field(value = "af_fin")
    private Double alleleFrequencyEuropeanFinnish;

    @Field(value = "af_nfe")
    private Double alleleFrequencyEuropeanNonFinnish;

    @Field(value = "af_oth")
    private Double alleleFrequencyOther;

    @Field(value = "af_sas")
    private Double alleleFrequencySouthAsian;

    public Double getAlleleFrequency()
    {
        return alleleFrequency;
    }

    public void setAlleleFrequency(Double alleleFrequency)
    {
        this.alleleFrequency = alleleFrequency;
    }

    public Double getAlleleFrequencyAfrican()
    {
        return alleleFrequencyAfrican;
    }

    public void setAlleleFrequencyAfrican(Double alleleFrequencyAfrican)
    {
        this.alleleFrequencyAfrican = alleleFrequencyAfrican;
    }

    public Double getAlleleFrequencyLatino()
    {
        return alleleFrequencyLatino;
    }

    public void setAlleleFrequencyLatino(Double alleleFrequencyLatino)
    {
        this.alleleFrequencyLatino = alleleFrequencyLatino;
    }

    public Double getAlleleFrequencyAshkenaziJewish()
    {
        return alleleFrequencyAshkenaziJewish;
    }

    public void setAlleleFrequencyAshkenaziJewish(Double alleleFrequencyAshkenaziJewish)
    {
        this.alleleFrequencyAshkenaziJewish = alleleFrequencyAshkenaziJewish;
    }

    public Double getAlleleFrequencyEastAsian()
    {
        return alleleFrequencyEastAsian;
    }

    public void setAlleleFrequencyEastAsian(Double alleleFrequencyEastAsian)
    {
        this.alleleFrequencyEastAsian = alleleFrequencyEastAsian;
    }

    public Double getAlleleFrequencyEuropeanFinnish()
    {
        return alleleFrequencyEuropeanFinnish;
    }

    public void setAlleleFrequencyEuropeanFinnish(Double alleleFrequencyEuropeanFinnish)
    {
        this.alleleFrequencyEuropeanFinnish = alleleFrequencyEuropeanFinnish;
    }

    public Double getAlleleFrequencyEuropeanNonFinnish()
    {
        return alleleFrequencyEuropeanNonFinnish;
    }

    public void setAlleleFrequencyEuropeanNonFinnish(Double alleleFrequencyEuropeanNonFinnish)
    {
        this.alleleFrequencyEuropeanNonFinnish = alleleFrequencyEuropeanNonFinnish;
    }

    public Double getAlleleFrequencyOther()
    {
        return alleleFrequencyOther;
    }

    public void setAlleleFrequencyOther(Double alleleFrequencyOther)
    {
        this.alleleFrequencyOther = alleleFrequencyOther;
    }

    public Double getAlleleFrequencySouthAsian()
    {
        return alleleFrequencySouthAsian;
    }

    public void setAlleleFrequencySouthAsian(Double alleleFrequencySouthAsian)
    {
        this.alleleFrequencySouthAsian = alleleFrequencySouthAsian;
    }

}