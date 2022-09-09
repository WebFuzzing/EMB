package org.cbioportal.genome_nexus.model;

import java.util.List;

public class VariantAnnotationSummary
{
    private String variant;
    private GenomicLocation genomicLocation;
    private String strandSign;
    private String variantType;
    private String assemblyName;
    private String canonicalTranscriptId;
    // old list of consequence summaries that only lists the canonical summary
    // when calling annotation/ and lists all when calling annotation/summary/
    // This has been replaced by tanscriptConsequenceSummary and
    // transcriptConsequenceSummaries respectively
    @Deprecated
    private List<TranscriptConsequenceSummary> transcriptConsequences;
    // all transcript consequences annotated with e.g. proteinStart/End
    private List<TranscriptConsequenceSummary> transcriptConsequenceSummaries;
    // most impactful canonical annotation, when looking to pick a single
    // annotation
    private TranscriptConsequenceSummary transcriptConsequenceSummary;

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public GenomicLocation getGenomicLocation() {
        return genomicLocation;
    }

    public void setGenomicLocation(GenomicLocation genomicLocation) {
        this.genomicLocation = genomicLocation;
    }

    public String getStrandSign() {
        return strandSign;
    }

    public void setStrandSign(String strandSign) {
        this.strandSign = strandSign;
    }

    public String getVariantType() {
        return variantType;
    }

    public void setVariantType(String variantType) {
        this.variantType = variantType;
    }

    public String getAssemblyName() {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName) {
        this.assemblyName = assemblyName;
    }

    public String getCanonicalTranscriptId() {
        return canonicalTranscriptId;
    }

    public void setCanonicalTranscriptId(String canonicalTranscriptId) {
        this.canonicalTranscriptId = canonicalTranscriptId;
    }

    public List<TranscriptConsequenceSummary> getTranscriptConsequences() {
        return this.transcriptConsequences;
    }

    public void setTranscriptConsequences(List<TranscriptConsequenceSummary> transcriptConsequences) {
        this.transcriptConsequences = transcriptConsequences;
    }

    public List<TranscriptConsequenceSummary> getTranscriptConsequenceSummaries() {
        return transcriptConsequenceSummaries;
    }

    public void setTranscriptConsequenceSummaries(List<TranscriptConsequenceSummary> transcriptConsequences) {
        this.transcriptConsequenceSummaries = transcriptConsequences;
    }

    public TranscriptConsequenceSummary getTranscriptConsequenceSummary() {
        return this.transcriptConsequenceSummary;
    }

    public void setTranscriptConsequenceSummary(TranscriptConsequenceSummary transcriptConsequenceSummary) {
        this.transcriptConsequenceSummary = transcriptConsequenceSummary;
    }
}
