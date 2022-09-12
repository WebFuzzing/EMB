package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.model.NucleotideContextAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.NucleotideContextService;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextNotFoundException;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextWebServiceException;

public class NucleotideContextAnnotationEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(NucleotideContextAnnotationEnricher.class);

    private NucleotideContextService nucleotideContextService;

    public NucleotideContextAnnotationEnricher(
        String id,
        NucleotideContextService nucleotideContextService
    ) {
        super(id);
        this.nucleotideContextService = nucleotideContextService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            NucleotideContext nucleotideContext = null;

            try {
                nucleotideContext = nucleotideContextService.getNucleotideContext(annotation);
            } catch (NucleotideContextWebServiceException e) {
                LOG.warn(e.getLocalizedMessage());
            } catch (NucleotideContextNotFoundException e) {
                // fail silently for this variant annotation
            }

            if (nucleotideContext != null)
            {
                NucleotideContextAnnotation nucleotideContextAnnotation = new NucleotideContextAnnotation();
                nucleotideContextAnnotation.setAnnotation(nucleotideContext);

                annotation.setNucleotideContextAnnotation(nucleotideContextAnnotation);
            }
        }
    }

}
