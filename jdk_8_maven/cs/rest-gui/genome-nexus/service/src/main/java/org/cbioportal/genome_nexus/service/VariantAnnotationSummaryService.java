package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

public interface VariantAnnotationSummaryService
{
    /**
     * Generate annotation summary for the canonical transcript of the given variant string.
     */
    VariantAnnotationSummary getAnnotationSummaryForCanonical(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException;

    /**
     * Generate annotation summary for the canonical transcript of the given variant annotation.
     */
    VariantAnnotationSummary getAnnotationSummaryForCanonical(VariantAnnotation annotation);

    /**
     * Generate annotation summary for all transcripts of the given variant annotation.
     */
    VariantAnnotationSummary getAnnotationSummary(VariantAnnotation annotation);

    /**
     * Generate annotation summary for all transcripts of the given variant string.
     */
    VariantAnnotationSummary getAnnotationSummary(String variant, String isoformOverrideSource)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException;

    /**
     * Generate annotation summaries for all transcripts of the given variant strings.
     */
    List<VariantAnnotationSummary> getAnnotationSummary(List<String> variants, String isoformOverrideSource);

    /**
     * Generate annotation summaries for the canonical transcript of the given variant strings.
     */
    List<VariantAnnotationSummary> getAnnotationSummaryForCanonical(List<String> variants, String isoformOverrideSource);
}
