/*
 * Copyright (c) 2021 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.cbioportal.genome_nexus.service.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

@Service
public class VerifiedGenomicLocationAnnotationServiceImpl implements GenomicLocationAnnotationService
{
    private static final Log LOG = LogFactory.getLog(VerifiedGenomicLocationAnnotationServiceImpl.class);

    private final GenomicLocationAnnotationService genomicLocationAnnotationService;
    private final NotationConverter notationConverter;

    @Autowired
    public VerifiedGenomicLocationAnnotationServiceImpl(
            GenomicLocationAnnotationService genomicLocationAnnotationService,
            NotationConverter notationConverter)
    {
        this.genomicLocationAnnotationService = genomicLocationAnnotationService;
        this.notationConverter = notationConverter;
    }

    @Override
    public VariantAnnotation getAnnotation(GenomicLocation genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = genomicLocationAnnotationService.getAnnotation(genomicLocation);
        VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(annotation);
        return verifiedAnnotation;
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation annotation = genomicLocationAnnotationService.getAnnotation(genomicLocation);
        VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(annotation);
        return verifiedAnnotation;
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations)
    {
        List<VariantAnnotation> annotations = genomicLocationAnnotationService.getAnnotations(genomicLocations);
        for (int index = 0; index < annotations.size(); index = index + 1) {
            VariantAnnotation annotation = annotations.get(index);
            VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(annotation);
            annotations.set(index, verifiedAnnotation);
        }
        return annotations;
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation,
                                           String isoformOverrideSource,
                                           Map<String, String> token,
                                           List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        VariantAnnotation annotation = genomicLocationAnnotationService.getAnnotation(genomicLocation, isoformOverrideSource, token, fields);
        VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(annotation);
        return verifiedAnnotation;
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations,
                                                                    String isoformOverrideSource,
                                                                    Map<String, String> token,
                                                                    List<String> fields)
    {
        List<VariantAnnotation> annotations = genomicLocationAnnotationService.getAnnotations(genomicLocations, isoformOverrideSource, token, fields);
        for (int index = 0; index < annotations.size(); index = index + 1) {
            VariantAnnotation annotation = annotations.get(index);
            VariantAnnotation verifiedAnnotation = verifyOrFailAnnotation(annotation);
            annotations.set(index, verifiedAnnotation);
        }
        return annotations;
    }

    @Override
    public String getVariantFormat(GenomicLocation genomicLocation) {
        return genomicLocationAnnotationService.getVariantFormat(genomicLocation);
    }

    private VariantAnnotation verifyOrFailAnnotation(VariantAnnotation annotation)
    {
        String originalVariantQuery = annotation.getOriginalVariantQuery(); // save for failed response
        String originalVariant = annotation.getVariant(); // save for failed response
        String originalQuery = originalVariantQuery;
        if (originalVariantQuery == null || originalVariantQuery.length() == 0) {
            originalQuery = originalVariant;
        }
        String providedReferenceAllele = notationConverter.parseGenomicLocation(originalQuery).getReferenceAllele();
        if (providedReferenceAllele.length() == 0) {
            // no comparison possible : allele not specified in query
            return annotation;
        }
        LOG.debug("verifying providedReferenceAllele : '" + providedReferenceAllele + "'");
        String responseReferenceAllele = getReferenceAlleleFromAnnotation(annotation);
        if (responseReferenceAllele.length() != providedReferenceAllele.length()) {
            // for altered length Deletion-Insertion responses, recover full reference allele with followup query
            String followUpVariant = constructFollowUpQuery(originalQuery);
            if (followUpVariant.length() > 0) {
                try {
                    LOG.debug("performing followup annotation request to get VEP genome assembly sequence : '" + providedReferenceAllele + "'");
                    VariantAnnotation followUpAnnotation = genomicLocationAnnotationService.getAnnotation(followUpVariant);
                    responseReferenceAllele = getReferenceAlleleFromAnnotation(followUpAnnotation);
                } catch (VariantAnnotationNotFoundException|VariantAnnotationWebServiceException vae) {
                    // followup validation failed - could not verify provided allele, so accept failure
                    LOG.debug("followup annotation request failed - Reference_Allele could not be verified");
                }
            }
        }
        if (providedReferenceAllele.equals(responseReferenceAllele)) {
            // validation complete
            return annotation;
        }
        // return annotation failure
        return createFailedAnnotation(originalVariantQuery, originalVariant);
    }

    private String constructFollowUpQuery(String originalQuery)
    {
        // create a deletion variant covering the referenced genome positions
        // this code should only run for delins variants where part of the TumorSeq allele matches the reference genome
        GenomicLocation followUpQueryGenomicLocation = notationConverter.parseGenomicLocation(originalQuery);
        followUpQueryGenomicLocation.setVariantAllele("-");
        if (followUpQueryGenomicLocation.getReferenceAllele().equals("-")) {
            return ""; // unexpectantly called constructFollowUpQuery on insertion query -- this annotation will fail
        }
        return followUpQueryGenomicLocation.toString();
    }

    private String getReferenceAlleleFromAnnotation(VariantAnnotation annotation)
    {
        String alleleString = annotation.getAlleleString();
        if (alleleString == null) {
            // maybe original annotation attempt failed
            return "";
        }
        int slashPosition = alleleString.indexOf('/');
        if (slashPosition == -1 || slashPosition == 0) {
            return "";
        }
        return alleleString.substring(0,slashPosition);

    }

    private VariantAnnotation createFailedAnnotation(String originalVariantQuery, String originalVariant)
    {
        VariantAnnotation annotation = new VariantAnnotation();
        if (originalVariantQuery != null && originalVariantQuery.length() > 0) {
            annotation.setOriginalVariantQuery(originalVariantQuery);
        }
        if (originalVariant != null && originalVariant.length() > 0) {
            annotation.setVariant(originalVariant);
        }
        annotation.setSuccessfullyAnnotated(false);
        return annotation;
    }
}
