package org.cbioportal.genome_nexus.service.enricher;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.component.annotation.OncokbResolver;
import org.cbioportal.genome_nexus.model.Alteration;
import org.cbioportal.genome_nexus.model.OncokbAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.OncokbService;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;
import org.cbioportal.genome_nexus.service.exception.OncokbNotFoundException;
import org.cbioportal.genome_nexus.service.exception.OncokbWebServiceException;
import org.oncokb.client.CancerGene;
import org.oncokb.client.IndicatorQueryResp;

public class OncokbAnnotationEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(OncokbAnnotationEnricher.class);
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;
    private final OncokbResolver oncokbResolver = new OncokbResolver();

    private OncokbService oncokbService;
    private String token;

    public OncokbAnnotationEnricher(
        String id,
        OncokbService oncokbService,
        VariantAnnotationSummaryService variantAnnotationSummaryService,
        String token
    ) {
        super(id);
        this.oncokbService = oncokbService;
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
        this.token = token;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            IndicatorQueryResp oncokb = null;
            VariantAnnotationSummary annotationSummary =
                this.variantAnnotationSummaryService.getAnnotationSummaryForCanonical(annotation);
            Alteration alteration = oncokbResolver.resolve(annotationSummary);

            // get OncoKB cancer gene list from database
            List<CancerGene> oncokbCancerGenesList = this.oncokbService.getOncokbCancerGenesList();
            Set<String> hugoSymbolSet = oncokbCancerGenesList.stream().map(CancerGene::getHugoSymbol).collect(Collectors.toSet());
            // if the query gene is not in the hugo symbol set, then skip the query
            if (hugoSymbolSet.contains(alteration.getHugoSymbol())) {
                try {
                    oncokb = oncokbService.getOncokb(alteration, token);
                } catch (OncokbWebServiceException e) {
                    LOG.warn(e.getLocalizedMessage());
                } catch (OncokbNotFoundException e) {
                    // fail silently for this variant annotation
                }
            }

            if (oncokb != null)
            {
                OncokbAnnotation oncokbAnnotation = new OncokbAnnotation();
                oncokbAnnotation.setAnnotation(oncokb);
                annotation.setOncokbAnnotation(oncokbAnnotation);
            }
        }
    }

}
