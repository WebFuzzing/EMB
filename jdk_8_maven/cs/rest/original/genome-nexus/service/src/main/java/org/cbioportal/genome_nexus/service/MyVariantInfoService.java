package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MyVariantInfoWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

public interface MyVariantInfoService
{
    // variant: hgvs variant (ex: 7:g.140453136A>T)
    MyVariantInfo getMyVariantInfoByHgvsVariant(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MyVariantInfoWebServiceException, MyVariantInfoNotFoundException;

    // variant: myvariantinfo variant (ex: chr7:g.140453136A>T)
    MyVariantInfo getMyVariantInfoByMyVariantInfoVariant(String variant)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException;

    // variant: hgvs variant (ex: 7:g.140453136A>T)
    List<MyVariantInfo> getMyVariantInfoByHgvsVariant(List<String> variants);

    MyVariantInfo getMyVariantInfoByAnnotation(VariantAnnotation annotation)
        throws MyVariantInfoNotFoundException, MyVariantInfoWebServiceException;

    List<MyVariantInfo> getMyVariantInfoByAnnotation(List<VariantAnnotation> annotations)
        throws MyVariantInfoWebServiceException;
}
