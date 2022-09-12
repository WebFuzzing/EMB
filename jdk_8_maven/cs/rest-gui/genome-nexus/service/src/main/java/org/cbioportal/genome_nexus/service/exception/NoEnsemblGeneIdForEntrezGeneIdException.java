package org.cbioportal.genome_nexus.service.exception;

public class NoEnsemblGeneIdForEntrezGeneIdException extends Exception
{
    private String entrezGeneId;

    public NoEnsemblGeneIdForEntrezGeneIdException(String entrezGeneId)
    {
        super();
        this.entrezGeneId = entrezGeneId;
    }

    public String getEntrezGeneId() {
        return entrezGeneId;
    }

    public void setEntrezGeneId(String entrezGeneId) {
        this.entrezGeneId = entrezGeneId;
    }

    @Override
    public String getMessage() {
        return "Entrez Gene Id not found: " + this.getEntrezGeneId();
    }
}