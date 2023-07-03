package org.cbioportal.genome_nexus.model;

public class TranscriptConsequenceSummary
{
    private String transcriptId;
    private String codonChange;
    private String aminoAcids;
    private String aminoAcidRef;
    private String aminoAcidAlt;
    private String entrezGeneId;
    private String consequenceTerms;
    private String hugoGeneSymbol;
    private String hgvspShort;
    private String hgvsp;
    private String hgvsc;
    private IntegerRange proteinPosition;
    private String refSeq;
    private String variantClassification;
    private String exon;
    private Double polyphenScore;
    private String polyphenPrediction;
    private Double siftScore;
    private String siftPrediction;
    private String uniprotId;

    public String getTranscriptId() {
        return transcriptId;
    }

    public String getAminoAcidAlt() {
		return aminoAcidAlt;
	}

	public void setAminoAcidAlt(String aminoAcidAlt) {
		this.aminoAcidAlt = aminoAcidAlt;
	}

	public String getAminoAcidRef() {
		return aminoAcidRef;
	}

	public void setAminoAcidRef(String aminoAcidRef) {
		this.aminoAcidRef = aminoAcidRef;
	}

	public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }

    public String getCodonChange() {
        return codonChange;
    }

    public void setCodonChange(String codonChange) {
        this.codonChange = codonChange;
    }

    public String getAminoAcids() {
        return aminoAcids;
    }

    public void setAminoAcids(String aminoAcids) {
        this.aminoAcids = aminoAcids;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    public String getConsequenceTerms() {
        return consequenceTerms;
    }

    public void setConsequenceTerms(String consequenceTerms) {
        this.consequenceTerms = consequenceTerms;
    }

    public String getHugoGeneSymbol() {
        return hugoGeneSymbol;
    }

    public void setHugoGeneSymbol(String hugoGeneSymbol) {
        this.hugoGeneSymbol = hugoGeneSymbol;
    }

    public String getHgvspShort() {
        return hgvspShort;
    }

    public void setHgvspShort(String hgvspShort) {
        this.hgvspShort = hgvspShort;
    }

    public String getHgvsp() {
        return hgvsp;
    }

    public void setHgvsp(String hgvsp) {
        this.hgvsp = hgvsp;
    }

    public String getHgvsc() {
        return hgvsc;
    }

    public void setHgvsc(String hgvsc) {
        this.hgvsc = hgvsc;
    }

    public IntegerRange getProteinPosition() {
        return proteinPosition;
    }

    public void setProteinPosition(IntegerRange proteinPosition) {
        this.proteinPosition = proteinPosition;
    }

    public String getRefSeq() {
        return refSeq;
    }

    public void setRefSeq(String refSeq) {
        this.refSeq = refSeq;
    }

    public String getVariantClassification() {
        return variantClassification;
    }

    public void setVariantClassification(String variantClassification) {
        this.variantClassification = variantClassification;
    }

    public String getExon() {
        return exon;
    }

    public void setExon(String exon) {
        this.exon = exon;
    }

    public Double getPolyphenScore() {
        return polyphenScore;
    }

    public void setPolyphenScore(Double polyphenScore) {
        this.polyphenScore = polyphenScore; 
    }

    public String getPolyphenPrediction() {
        return polyphenPrediction;
    }

    public void setPolyphenPrediction(String polyphenPrediction) {
        this.polyphenPrediction = polyphenPrediction; 
    }

    public Double getSiftScore() {
        return siftScore;
    }

    public void setSiftScore(Double siftScore) {
        this.siftScore = siftScore; 
    }

    public String getSiftPrediction() {
        return siftPrediction;
    }

    public void setSiftPrediction(String siftPrediction) {
        this.siftPrediction = siftPrediction; 
    }

    public String getUniprotId() {
        return uniprotId;
    }

    public void setUniprotId(String uniprotId) {
        this.uniprotId = uniprotId; 
    }
}
