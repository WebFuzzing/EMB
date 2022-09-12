package org.cbioportal.genome_nexus.service.exception;

public class EnsemblTranscriptNotFoundException extends Exception
{
    private String id;
    private String isoformOverrideSource;

    public EnsemblTranscriptNotFoundException(String id)
    {
        super();
        this.id = id;
    }

    public EnsemblTranscriptNotFoundException(String id, String isoformOverrideSource) {
        super();
        this.id = id;
        this.isoformOverrideSource = isoformOverrideSource;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIsoformOverrideSource() {
        return isoformOverrideSource;
    }

    public void setIsoformOverrideSource(String isoformOverrideSource) {
        this.isoformOverrideSource = isoformOverrideSource;
    }

    @Override
    public String getMessage() {
        return "Ensembl transcript not found: " + this.getId();
    }
}
