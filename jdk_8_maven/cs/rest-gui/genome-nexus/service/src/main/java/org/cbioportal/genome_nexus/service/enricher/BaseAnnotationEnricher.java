package org.cbioportal.genome_nexus.service.enricher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;

import java.util.List;

public abstract class BaseAnnotationEnricher implements AnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(BaseAnnotationEnricher.class);

    protected String id;

    public BaseAnnotationEnricher(String id) {
        this.id = id;
    }

    @Override
    public void enrich(List<VariantAnnotation> annotations) {
        // default implementation, just iterate over all annotations and call enrich one by one
        for (VariantAnnotation annotation: annotations) {
            try {
                this.enrich(annotation);
            } catch (Exception e) {
                LOG.warn("Failed to enrich multiple annotations with " + this.getId() + ": " + annotation.getVariant() + " " + e.getLocalizedMessage());
            }

        }
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
