package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class AlleleNumber
{

    @Field(value = "an")
    private Integer alleleNumber;

    @Field(value = "an_afr")
    private Integer alleleNumberAfrican;

    @Field(value = "an_amr")
    private Integer alleleNumberLatino;

    @Field(value = "an_asj")
    private Integer alleleNumberAshkenaziJewish;

    @Field(value = "an_eas")
    private Integer alleleNumberEastAsian;

    @Field(value = "an_fin")
    private Integer alleleNumberEuropeanFinnish;

    @Field(value = "an_nfe")
    private Integer alleleNumberEuropeanNonFinnish;

    @Field(value = "an_oth")
    private Integer alleleNumberOther;

    @Field(value = "an_sas")
    private Integer alleleNumberSouthAsian;

    public Integer getAlleleNumber()
    {
        return alleleNumber;
    }

    public void setAlleleNumber(Integer alleleNumber)
    {
        this.alleleNumber = alleleNumber;
    }

    public Integer getAlleleNumberAfrican()
    {
        return alleleNumberAfrican;
    }

    public void setAlleleNumberAfrican(Integer alleleNumberAfrican)
    {
        this.alleleNumberAfrican = alleleNumberAfrican;
    }

    public Integer getAlleleNumberLatino()
    {
        return alleleNumberLatino;
    }

    public void setAlleleNumberLatino(Integer alleleNumberLatino)
    {
        this.alleleNumberLatino = alleleNumberLatino;
    }

    public Integer getAlleleNumberAshkenaziJewish()
    {
        return alleleNumberAshkenaziJewish;
    }

    public void setAlleleNumberAshkenaziJewish(Integer alleleNumberAshkenaziJewish)
    {
        this.alleleNumberAshkenaziJewish = alleleNumberAshkenaziJewish;
    }

    public Integer getAlleleNumberEastAsian()
    {
        return alleleNumberEastAsian;
    }

    public void setAlleleNumberEastAsian(Integer alleleNumberEastAsian)
    {
        this.alleleNumberEastAsian = alleleNumberEastAsian;
    }

    public Integer getAlleleNumberEuropeanFinnish()
    {
        return alleleNumberEuropeanFinnish;
    }

    public void setAlleleNumberEuropeanFinnish(Integer alleleNumberEuropeanFinnish)
    {
        this.alleleNumberEuropeanFinnish = alleleNumberEuropeanFinnish;
    }

    public Integer getAlleleNumberEuropeanNonFinnish()
    {
        return alleleNumberEuropeanNonFinnish;
    }

    public void setAlleleNumberEuropeanNonFinnish(Integer alleleNumberEuropeanNonFinnish)
    {
        this.alleleNumberEuropeanNonFinnish = alleleNumberEuropeanNonFinnish;
    }

    public Integer getAlleleNumberOther()
    {
        return alleleNumberOther;
    }

    public void setAlleleNumberOther(Integer alleleNumberOther)
    {
        this.alleleNumberOther = alleleNumberOther;
    }

    public Integer getAlleleNumberSouthAsian()
    {
        return alleleNumberSouthAsian;
    }

    public void setAlleleNumberSouthAsian(Integer alleleNumberSouthAsian)
    {
        this.alleleNumberSouthAsian = alleleNumberSouthAsian;
    }

}