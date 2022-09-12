package org.cbioportal.genome_nexus.service.exception;

public class PdbHeaderNotFoundException extends Exception
{
    private String pdbId;

    public PdbHeaderNotFoundException(String pdbId) {
        super();
        this.pdbId = pdbId;
    }

    public String getPdbId() {
        return pdbId;
    }

    public void setPdbId(String pdbId) {
        this.pdbId = pdbId;
    }

    @Override
    public String getMessage() {
        return "PDB header not found: " + this.getPdbId();
    }
}
