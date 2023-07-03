package org.cbioportal.genome_nexus.service.exception;

public class PfamDomainNotFoundException extends Exception
{
    private String pfamAccession;

    public PfamDomainNotFoundException(String pfamAccession) {
        super();
        this.pfamAccession = pfamAccession;
    }

    public String getPfamAccession() {
        return pfamAccession;
    }

    public void setPfamAccession(String pfamAccession) {
        this.pfamAccession = pfamAccession;
    }

    @Override
    public String getMessage() {
        return "PFAM domain not found: " + this.getPfamAccession();
    }
}
