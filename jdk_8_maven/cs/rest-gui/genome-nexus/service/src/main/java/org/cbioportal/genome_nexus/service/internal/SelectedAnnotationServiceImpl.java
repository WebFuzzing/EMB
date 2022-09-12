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
 * This file is part of cBioPortal Genome Nexus.
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

package org.cbioportal.genome_nexus.service;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.GenomicLocationAnnotationService;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationQueryMixedFormatException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.service.internal.HgvsVariantAnnotationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;

@Service
public class SelectedAnnotationServiceImpl implements SelectedAnnotationService {
    private final VariantAnnotationService verifiedHgvsVariantAnnotationService;
    private final GenomicLocationAnnotationService genomicLocationAnnotationService;
    private final NotationConverter notationConverter;
    private final Boolean isRegionAnnotationEnabled;

private static final Log LOG = LogFactory.getLog(SelectedAnnotationServiceImpl.class);

    @Autowired
    public SelectedAnnotationServiceImpl(
            VariantAnnotationService verifiedHgvsVariantAnnotationService,
            GenomicLocationAnnotationService genomicLocationAnnotationServiceImpl,
            NotationConverter notationConverter,
            @Value("${gn_vep.region.url:}")
            String vepRegionUrl) {
        this.verifiedHgvsVariantAnnotationService = verifiedHgvsVariantAnnotationService;
        this.genomicLocationAnnotationService = genomicLocationAnnotationServiceImpl;
        this.notationConverter = notationConverter;
        this.isRegionAnnotationEnabled = vepRegionUrl != null && vepRegionUrl.length() > 0;
    }

    @Override
    public VariantAnnotation getAnnotation(
            String variant,
            String isoformOverrideSource,
            Map<String, String> token,
            List<String> fields) throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException {
        if (needToConvertHgvsToRegionForAnnotation(variant)) {
            GenomicLocation variantAsGenomicLocation = notationConverter.hgvsgToGenomicLocation(variant);
            // TODO: we should provide a getAnnotation() call to genomicLocationAnnotationService which accepts GenomicLocation
            return genomicLocationAnnotationService.getAnnotation(variantAsGenomicLocation.toString(), isoformOverrideSource, token, fields);
        } else {
            // TODO: do we support configuration of Genome-Nexus where non-hgvs variants will arrive at /annotation/{variant} endpoint and they get sent to a (non-hgvs) vep.url ? If not, make this service hgvs only.
            return verifiedHgvsVariantAnnotationService.getAnnotation(variant, isoformOverrideSource, token, fields);
        }
    }

    @Override
    public List<VariantAnnotation> getAnnotations(
            List<String> variants,
            String isoformOverrideSource,
            Map<String, String> token,
            List<String> fields) throws VariantAnnotationNotFoundException, VariantAnnotationQueryMixedFormatException, VariantAnnotationWebServiceException {
        if (needToConvertHgvsToRegionForAnnotation(variants)) {
            List<GenomicLocation> variantsAsGenomicLocations = notationConverter.hgvsgToGenomicLocations(variants);
            return genomicLocationAnnotationService.getAnnotations(variantsAsGenomicLocations, isoformOverrideSource, token, fields);
        } else {
            // all variants will be sent to the hgvsAnnotationService, so non-hgvs formatted variants will fail annotation and hgvs formatted variants may succeed
            // TODO: do we support configuration of Genome-Nexus where non-hgvs variants will arrive at /annotation endpoint and they get sent to a (non-hgvs) vep.url ? If not, make this service hgvs only.
            return verifiedHgvsVariantAnnotationService.getAnnotations(variants, isoformOverrideSource, token, fields);
        }
    }

    private boolean isHgvsFormat(String variant) {
        return variant.contains("g.");
    }

    private boolean needToConvertHgvsToRegionForAnnotation(String variant) {
        if (!isRegionAnnotationEnabled) {
            return false; // without a local gn_vep service processing region format queries, all variants are processed via hgvsAnnotationService
        }
        if (!isHgvsFormat(variant)) {
            return false;
        }
        return true;
    }

    private boolean needToConvertHgvsToRegionForAnnotation(List<String> variants) throws VariantAnnotationQueryMixedFormatException {
        if (!isRegionAnnotationEnabled) {
            return false; // without a local gn_vep service processing region format queries, all variants are processed via hgvsAnnotationService
        }
        if (variants.isEmpty()) {
            return false; // no variants to annotate
        }
        boolean someElementIsHgvs = false;
        boolean someElementIsNonHgvs = false;
        for (String variant : variants) {
            if (isHgvsFormat(variant)) {
                someElementIsHgvs = true;
            } else {
                someElementIsNonHgvs = true;
            }
            if (someElementIsHgvs && someElementIsNonHgvs) {
                throw new VariantAnnotationQueryMixedFormatException("This server cannot process a mixture of HGVS formatted and non-HGVS formatted variants submitted in a single annotation request");
            }
        }
        if (!someElementIsHgvs) {
            return false; // hgvs not present in any variant
        }
        return true;
    }

}
