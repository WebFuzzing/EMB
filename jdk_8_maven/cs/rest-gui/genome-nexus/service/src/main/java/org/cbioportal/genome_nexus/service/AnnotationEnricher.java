package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.VariantAnnotation;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface AnnotationEnricher
{
    void enrich(VariantAnnotation annotation);
    void enrich(List<VariantAnnotation> annotations);
    String getId();
}
