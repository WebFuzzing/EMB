package org.cbioportal.genome_nexus.service.mixin.my_variant_info_mixin;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.cbioportal.genome_nexus.model.my_variant_info_model.Alleles;
import org.cbioportal.genome_nexus.model.my_variant_info_model.Hg19;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DbsnpMixin
{
    @JsonProperty(value = "_license", required = true)
    private String license;

    @JsonProperty(value = "allele_origin", required = true)
    private String alleleOrigin;

    @JsonProperty(value = "alleles", required = true)
    private Alleles alleles;

    @JsonProperty(value = "alt", required = true)
    private String alt;

    @JsonProperty(value = "chrom", required = true)
    private String chrom;

    @JsonProperty(value = "class", required = true)
    private String _class;

    @JsonProperty(value = "dbsnp_build", required = true)
    private Integer dbsnpBuild;

    @JsonProperty(value = "flags", required = true)
    private String flags;

    @JsonProperty(value = "hg19", required = true)
    private Hg19 hg19;

    @JsonProperty(value = "ref", required = true)
    private String ref;

    @JsonProperty(value = "rsid", required = true)
    private String rsid;

    @JsonProperty(value = "validated", required = true)
    private Boolean validated;

    @JsonProperty(value = "var_subtype", required = true)
    private String varSubtype;

    @JsonProperty(value = "vartype", required = true)
    private String vartype;
}