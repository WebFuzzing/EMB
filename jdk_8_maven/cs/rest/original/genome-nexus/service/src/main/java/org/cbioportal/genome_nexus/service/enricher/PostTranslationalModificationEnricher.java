package org.cbioportal.genome_nexus.service.enricher;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.model.PtmAnnotation;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.PostTranslationalModificationService;

import java.util.ArrayList;
import java.util.List;

public class PostTranslationalModificationEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(MyVariantInfoAnnotationEnricher.class);

    private PostTranslationalModificationService postTranslationalModificationService;

    public PostTranslationalModificationEnricher(
        String id,
        PostTranslationalModificationService postTranslationalModificationService
    ) {
        super(id);
        this.postTranslationalModificationService = postTranslationalModificationService;
    }

    @Override
    public void enrich(VariantAnnotation annotation)
    {
        if (annotation != null && annotation.getTranscriptConsequences() != null)
        {
            List<List<PostTranslationalModification>> ptmsList = new ArrayList<>();

            for (TranscriptConsequence transcript : annotation.getTranscriptConsequences())
            {
                ptmsList.add(postTranslationalModificationService.getPostTranslationalModifications(
                    transcript, annotation));
            }

            PtmAnnotation ptmAnnotation = new PtmAnnotation();

            if (ptmsList.size() > 0)
            {
                ptmAnnotation.setAnnotation(ptmsList);
            }

            annotation.setPtmAnnotation(ptmAnnotation);
        }
    }
}
