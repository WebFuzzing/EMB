package org.cbioportal.genome_nexus.model;

import java.util.List;

public class AggregatedHotspots
{
    private GenomicLocation genomicLocation;
    private String variant;
    private String transcriptId;
    private ProteinLocation proteinLocation;
    private List<Hotspot> hotspots;

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

    public List<Hotspot> getHotspots() {
        return hotspots;
    }

    public void setHotspots(List<Hotspot> hotspots) {
        this.hotspots = hotspots;
    }

    public void setProteinLocation(ProteinLocation proteinLocation) {
        this.proteinLocation = proteinLocation;
    }

    public ProteinLocation getProteinLocation() {
        return proteinLocation;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public void setTranscriptId(String transcriptId) {
        this.transcriptId = transcriptId;
    }
}
