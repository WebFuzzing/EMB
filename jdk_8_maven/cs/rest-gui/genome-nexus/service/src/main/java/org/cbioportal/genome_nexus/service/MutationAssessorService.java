package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

public interface MutationAssessorService
{
    // variant: hgvs variant (ex: 7:g.140453136A>T)
    MutationAssessor getMutationAssessor(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MutationAssessorWebServiceException, MutationAssessorNotFoundException;

    // variant: mutation assessor variant (ex: 7,140453136,A,T)
    MutationAssessor getMutationAssessorByMutationAssessorVariant(String variant)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException;

    // variant: hgvs variant (ex: 7:g.140453136A>T)
    List<MutationAssessor> getMutationAssessor(List<String> variants);

    MutationAssessor getMutationAssessor(VariantAnnotation annotation)
        throws MutationAssessorNotFoundException, MutationAssessorWebServiceException;
}
