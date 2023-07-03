package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.mongodb.core.mapping.Field;

public class Gene
{

    @Field(value = "gene_id")
    private String geneId;

    @Field(value = "symbol")
    private String symbol;


    public String getGeneId()
    {
        return geneId;
    }

    public void setGeneId(String geneId)
    {
        this.geneId = geneId;
    }
    public String getSymbol()
    {
        return symbol;
    }

    public void setSymbol(String symbol)
    {
        this.symbol = symbol;
    }

}