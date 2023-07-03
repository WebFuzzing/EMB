package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Homozygotes
{

    @Field(value = "hom")
    private Integer homozygotes;

    @Field(value = "hom_afr")
    private Integer homozygotesAfrican;

    @Field(value = "hom_amr")
    private Integer homozygotesLatino;

    @Field(value = "hom_asj")
    private Integer homozygotesAshkenaziJewish;

    @Field(value = "hom_eas")
    private Integer homozygotesEastAsian;

    @Field(value = "hom_fin")
    private Integer homozygotesEuropeanFinnish;

    @Field(value = "hom_nfe")
    private Integer homozygotesEuropeanNonFinnish;

    @Field(value = "hom_oth")
    private Integer homozygotesOther;

    @Field(value = "hom_sas")
    private Integer homozygotesSouthAsian;

    public Integer getHomozygotes()
    {
        return homozygotes;
    }

    public void setHomozygotes(Integer homozygotes)
    {
        this.homozygotes = homozygotes;
    }

    public Integer getHomozygotesAfrican()
    {
        return homozygotesAfrican;
    }

    public void setHomozygotesAfrican(Integer homozygotesAfrican)
    {
        this.homozygotesAfrican = homozygotesAfrican;
    }

    public Integer getHomozygotesLatino()
    {
        return homozygotesLatino;
    }

    public void setHomozygotesLatino(Integer homozygotesLatino)
    {
        this.homozygotesLatino = homozygotesLatino;
    }

    public Integer getHomozygotesAshkenaziJewish()
    {
        return homozygotesAshkenaziJewish;
    }

    public void setHomozygotesAshkenaziJewish(Integer homozygotesAshkenaziJewish)
    {
        this.homozygotesAshkenaziJewish = homozygotesAshkenaziJewish;
    }

    public Integer getHomozygotesEastAsian()
    {
        return homozygotesEastAsian;
    }

    public void setHomozygotesEastAsian(Integer homozygotesEastAsian)
    {
        this.homozygotesEastAsian = homozygotesEastAsian;
    }

    public Integer getHomozygotesEuropeanFinnish()
    {
        return homozygotesEuropeanFinnish;
    }

    public void setHomozygotesEuropeanFinnish(Integer homozygotesEuropeanFinnish)
    {
        this.homozygotesEuropeanFinnish = homozygotesEuropeanFinnish;
    }

    public Integer getHomozygotesEuropeanNonFinnish()
    {
        return homozygotesEuropeanNonFinnish;
    }

    public void setHomozygotesEuropeanNonFinnish(Integer homozygotesEuropeanNonFinnish)
    {
        this.homozygotesEuropeanNonFinnish = homozygotesEuropeanNonFinnish;
    }

    public Integer getHomozygotesOther()
    {
        return homozygotesOther;
    }

    public void setHomozygotesOther(Integer homozygotesOther)
    {
        this.homozygotesOther = homozygotesOther;
    }

    public Integer getHomozygotesSouthAsian()
    {
        return homozygotesSouthAsian;
    }

    public void setHomozygotesSouthAsian(Integer homozygotesSouthAsian)
    {
        this.homozygotesSouthAsian = homozygotesSouthAsian;
    }

}