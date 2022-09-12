package org.cbioportal.genome_nexus.model;


public class ProteinLocation
{
    private String transcriptId;
    private Integer start;
    private Integer end;
    private String mutationType;

    public ProteinLocation() {}

    public ProteinLocation(String transcriptId, Integer start, Integer end, String mutationType) {
        this.transcriptId = transcriptId;
        this.start = start;
        this.end = end;
        this.mutationType = mutationType;
    }

    public String getTranscriptId() {
        return transcriptId;
    }

    public Integer getStart() {
        return start;
    }

    public Integer getEnd() {
        return end;
    }

    public String getMutationType() {
        return mutationType;
    }

}
