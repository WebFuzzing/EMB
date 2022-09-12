package org.cbioportal.genome_nexus.service.enricher;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfoAnnotation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MyVariantInfoService;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MyVariantInfoAnnotationEnricher extends BaseAnnotationEnricher
{
    private static final Log LOG = LogFactory.getLog(MyVariantInfoAnnotationEnricher.class);

    private MyVariantInfoService myVariantInfoService;

    public MyVariantInfoAnnotationEnricher(
        String id,
        MyVariantInfoService myVariantInfoService
    ) {
        super(id);
        this.myVariantInfoService = myVariantInfoService;
    }

    @Override
    public void enrich(VariantAnnotation annotation) {
        if (annotation != null)
        {
            MyVariantInfo myVariantInfo = null;

            try {
                myVariantInfo = myVariantInfoService.getMyVariantInfoByAnnotation(annotation);
            } catch (MyVariantInfoWebServiceException e) {
                // message is always null for MyVariantInfoWebServiceException, log response instead
                LOG.warn(e.getResponseBody());
            } catch (MyVariantInfoNotFoundException e) {
                // fail silently for this variant annotation
            }

            this.enrichAnnotationWithMyVariantInfo(annotation, myVariantInfo);
        }
    }

    @Override
    public void enrich(List<VariantAnnotation> annotations) {
        try {
            List<MyVariantInfo> myVariantInfo = myVariantInfoService.getMyVariantInfoByAnnotation(annotations);
            Map<String, MyVariantInfo> hgvsToMyVariantInfoMap =
                myVariantInfo.stream().collect(Collectors.toMap(MyVariantInfo::getHgvs, v -> v));

            for (VariantAnnotation annotation: annotations) {
                this.enrichAnnotationWithMyVariantInfo(annotation,
                    hgvsToMyVariantInfoMap.get(annotation.getHgvsg()));
            }
        } catch (MyVariantInfoWebServiceException e) {
            // message is always null for MyVariantInfoWebServiceException, log response instead
            LOG.warn(e.getResponseBody());
        }
    }

    private void enrichAnnotationWithMyVariantInfo(
        VariantAnnotation annotation,
        MyVariantInfo myVariantInfo
    ) {
        if (myVariantInfo != null)
        {
            MyVariantInfoAnnotation myVariantInfoAnnotation = new MyVariantInfoAnnotation();
            myVariantInfoAnnotation.setAnnotation(myVariantInfo);

            annotation.setMyVariantInfoAnnotation(myVariantInfoAnnotation);
        }
    }
}
