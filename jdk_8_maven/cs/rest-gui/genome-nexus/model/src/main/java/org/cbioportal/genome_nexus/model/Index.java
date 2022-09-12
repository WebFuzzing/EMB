package org.cbioportal.genome_nexus.model;

import java.util.List;

import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection="index")
public class Index {
    @Indexed
    private String variant;
    @Indexed
    private List<String> hugoSymbol;
    @Indexed
    private List<String> hgvspShort;
    @Indexed
    private List<String> hgvsp;
    @Indexed
    private List<String> cdna;
    @Indexed
    private List<String> hgvsc;
    @Indexed
    private List<String> rsid;
    

    public String getvariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public List<String> getHugoSymbol() {
        return hugoSymbol;
    }

    public void setHugoSymbol(List<String> hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    public List<String> getHgvspShort() {
        return hgvspShort;
    }

    public void setHgvspShort(List<String> hgvspShort) {
        this.hgvspShort = hgvspShort;
    }

    public List<String> getHgvsp() {
        return hgvsp;
    }

    public void setHgvsp(List<String> hgvsp) {
        this.hgvsp = hgvsp;
    }

    public List<String> getCdna() {
        return cdna;
    }

    public void setCdna(List<String> cdna) {
        this.cdna = cdna;
    }

    public List<String> getHgvsc() {
        return hgvsc;
    }

    public void setHgvsc(List<String> hgvsc) {
        this.hgvsc = hgvsc;
    }
    
    public List<String> getRsid() {
        return rsid;
    }

    public void setRsid(List<String> rsid) {
        this.rsid = rsid;
    }
}
