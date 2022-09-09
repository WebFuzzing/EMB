package org.cbioportal.genome_nexus.model.my_variant_info_model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "my_variant_info.annotation")
public class MyVariantInfo
{
    @Id
    private String variant;

    @Field(value = "query")
    private String query;

    @Field(value = "hgvs")
    private String hgvs;

    @Field(value = "version")
    private Integer version;

    @Field(value = "snpeff")
    private Snpeff snpeff;

    @Field(value = "vcf")
    private Vcf vcf;

    @Field(value = "dbsnp")
    private Dbsnp dbsnp;

    @Field(value = "cosmic")
    private Cosmic cosmic;

    @Field(value = "clinvar")
    private ClinVar clinVar;

    @Field(value = "mutdb")
    private Mutdb mutdb;

    @Field(value = "gnomad_exome")
    private Gnomad gnomadExome;

    @Field(value = "gnomad_genome")
    private Gnomad gnomadGenome;

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getHgvs() {
        return hgvs;
    }

    public void setHgvs(String hgvs) {
        this.hgvs = hgvs;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Snpeff getSnpeff() {
        return snpeff;
    }

    public void setSnpeff(Snpeff snpeff) {
        this.snpeff = snpeff;
    }

    public Vcf getVcf() {
        return vcf;
    }

    public void setVcf(Vcf vcf) {
        this.vcf = vcf;
    }

    public Dbsnp getDbsnp() {
        return dbsnp;
    }

    public void setDbsnp(Dbsnp dbsnp) {
        this.dbsnp = dbsnp;
    }

    public Cosmic getCosmic() {
        return cosmic;
    }

    public void setCosmic(Cosmic cosmic) {
        this.cosmic = cosmic;
    }

    public ClinVar getClinVar() {
        return clinVar;
    }

    public void setClinVar(ClinVar clinVar) {
        this.clinVar = clinVar;
    }

    public Mutdb getMutdb() {
        return mutdb;
    }

    public void setMutdb(Mutdb mutdb) {
        this.mutdb = mutdb;
    }

    public Gnomad getGnomadExome() {
        return gnomadExome;
    }

    public void setGnomadExome(Gnomad gnomadExome) {
        this.gnomadExome = gnomadExome;
    }

    public Gnomad getGnomadGenome() {
        return gnomadGenome;
    }

    public void setGnomadGenome(Gnomad gnomadGenome) {
        this.gnomadGenome = gnomadGenome;
    }
}
