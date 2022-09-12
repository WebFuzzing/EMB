package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextNotFoundException;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

public interface NucleotideContextService
{
    // variant: hgvs variant (ex: 7:g.140453136A>T)
    NucleotideContext getNucleotideContext(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
            NucleotideContextNotFoundException, NucleotideContextWebServiceException;

    // variant: mutation assessor variant (e.g. 17:37880219..37880221:1)
    NucleotideContext getNucleotideContextByEnsembleSequenceQuery(String sequenceQuery)
        throws NucleotideContextNotFoundException, NucleotideContextWebServiceException;

    // variant: hgvs variant (ex: 7:g.140453136A>T)
    List<NucleotideContext> getNucleotideContext(List<String> variants);

    NucleotideContext getNucleotideContext(VariantAnnotation annotation)
        throws NucleotideContextNotFoundException, NucleotideContextWebServiceException;
}