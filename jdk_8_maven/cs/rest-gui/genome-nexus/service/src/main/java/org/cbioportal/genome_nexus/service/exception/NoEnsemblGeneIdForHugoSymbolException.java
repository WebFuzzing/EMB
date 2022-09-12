package org.cbioportal.genome_nexus.service.exception;

public class NoEnsemblGeneIdForHugoSymbolException extends Exception
{
    private String hugoSymbol;

    public NoEnsemblGeneIdForHugoSymbolException(String hugoSymbol)
    {
        super();
        this.hugoSymbol = hugoSymbol;
    }

    public String getHugoSymbol() {
        return hugoSymbol;
    }

    public void sethugoSymbol(String hugoSymbol) {
        this.hugoSymbol = hugoSymbol;
    }

    @Override
    public String getMessage() {
        return "Hugo Symbol not found: " + this.getHugoSymbol();
    }
}
