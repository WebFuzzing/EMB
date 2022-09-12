package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.CuriousCases;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesWebServiceException;

public interface CuriousCasesService
{
    CuriousCases getCuriousCases(String genomicLocation) throws CuriousCasesNotFoundException, CuriousCasesWebServiceException;
}
