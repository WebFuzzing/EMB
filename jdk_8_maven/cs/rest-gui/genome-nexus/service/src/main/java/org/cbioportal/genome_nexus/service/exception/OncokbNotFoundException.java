package org.cbioportal.genome_nexus.service.exception;

import org.cbioportal.genome_nexus.model.Alteration;

public class OncokbNotFoundException extends Exception
{
    private Alteration alteration;

    public OncokbNotFoundException(Alteration alteration) {
        super();
        this.alteration = alteration;
    }

    public Alteration getAlteration() {
        return alteration;
    }

    public void setVariant(Alteration alteration) {
        this.alteration = alteration;
    }

    @Override
    public String getMessage() {
        return "Oncokb annotation not found for hugo gene symbol: " + this.getAlteration().getHugoSymbol();
    }
}
