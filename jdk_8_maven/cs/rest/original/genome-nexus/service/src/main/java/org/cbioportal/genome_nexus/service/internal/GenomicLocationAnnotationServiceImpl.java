/*
 * Copyright (c) 2015 - 2021 Memorial Sloan-Kettering Cancer Center.
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
import org.cbioportal.genome_nexus.service.cached.CachedVariantRegionAnnotationFetcher;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.*;

import java.util.*;

@Service
public class GenomicLocationAnnotationServiceImpl implements GenomicLocationAnnotationService
{
    private static final Log LOG = LogFactory.getLog(GenomicLocationAnnotationService.class);

    private final Boolean isRegionAnnotationEnabled;
    private final NotationConverter notationConverter;
    private final VariantAnnotationService variantAnnotationService;
    private final GenomicLocationToVariantFormat genomicLocationToVariantFormat;
    private final GenomicLocationStringToVariantFormat genomicLocationStringToVariantFormat;

    private final GenomicLocationsToVariantFormats genomicLocationsToVariantFormats;

    @Autowired
    public GenomicLocationAnnotationServiceImpl(CachedVariantRegionAnnotationFetcher cachedVariantRegionAnnotationFetcher,
                                                NotationConverter notationConverter,
                                                // Lazy autowire services used for enrichment,
                                                // otherwise we are getting circular dependency issues
                                                @Lazy VariantAnnotationService verifiedHgvsVariantAnnotationService,
                                                @Lazy VariantAnnotationService regionVariantAnnotationService)

    {
        this.notationConverter = notationConverter;
        this.isRegionAnnotationEnabled = cachedVariantRegionAnnotationFetcher.hasValidURI();
        if (this.isRegionAnnotationEnabled) {
            this.variantAnnotationService = regionVariantAnnotationService;
            this.genomicLocationToVariantFormat = notationConverter::genomicToEnsemblRestRegion;
            this.genomicLocationStringToVariantFormat = notationConverter::genomicToEnsemblRestRegion;
            this.genomicLocationsToVariantFormats = notationConverter::genomicToEnsemblRestRegion;
        } else {
            this.variantAnnotationService = verifiedHgvsVariantAnnotationService;
            this.genomicLocationToVariantFormat = notationConverter::genomicToHgvs;
            this.genomicLocationStringToVariantFormat = notationConverter::genomicToHgvs;
            this.genomicLocationsToVariantFormats = notationConverter::genomicToHgvs;

        }
    }

    @Override
    public VariantAnnotation getAnnotation(GenomicLocation genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(this.genomicLocationToVariantFormat.convert(genomicLocation));
        genomicLocation.setOriginalInput(genomicLocation.toString());
        variantAnnotation.setOriginalVariantQuery(genomicLocation.getOriginalInput());
        return variantAnnotation;
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        VariantAnnotation variantAnnotation = this.getAnnotation(this.notationConverter.parseGenomicLocation(genomicLocation));
        variantAnnotation.setOriginalVariantQuery(genomicLocation);
        return variantAnnotation;
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations)
    {
        Map<String, Set<String>> convertedVarsToOrigVarQueryMap = mapConvertedVarsToOrigVarQuery(genomicLocations);
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();
        this.variantAnnotationService.getAnnotations(
                this.genomicLocationsToVariantFormats.convert(genomicLocations)
        ).stream().forEachOrdered((a) -> {
            if (convertedVarsToOrigVarQueryMap.get(a.getVariant()).size() == 1) {
                String origVarQuery = convertedVarsToOrigVarQueryMap.get(a.getVariant()).toArray(new String[0])[0];
                a.setOriginalVariantQuery(origVarQuery);
                variantAnnotations.add(a);
            }
            else {
                for (String origVarQuery : convertedVarsToOrigVarQueryMap.get(a.getVariant())) {
                    // if query multiple "identical" variants
                    // for example "12,25398284,25398284,C,A" and "12,25398283,25398284,AC,AA"
                    // both variants should be returned
                    Gson gson = new Gson();
                    VariantAnnotation tempAnnotation = gson.fromJson(gson.toJson(a), VariantAnnotation.class);
                    tempAnnotation.setOriginalVariantQuery(origVarQuery);
                    variantAnnotations.add(tempAnnotation);
                }
            }
        });
        return variantAnnotations;
    }

    @Override
    public VariantAnnotation getAnnotation(String genomicLocation,
                                           String isoformOverrideSource,
                                           Map<String, String> token,
                                           List<String> fields)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(
                this.genomicLocationStringToVariantFormat.convert(genomicLocation),
                isoformOverrideSource,
                token,
                fields);
        variantAnnotation.setOriginalVariantQuery(genomicLocation);
        return variantAnnotation;
    }

    @Override
    public List<VariantAnnotation> getAnnotations(List<GenomicLocation> genomicLocations,
                                                                    String isoformOverrideSource,
                                                                    Map<String, String> token,
                                                                    List<String> fields)
    {
        Map<String, Set<String>> convertedVarsToOrigVarQueryMap = mapConvertedVarsToOrigVarQuery(genomicLocations);
        List<VariantAnnotation> variantAnnotations = new ArrayList<>();
        this.variantAnnotationService.getAnnotations(
                this.genomicLocationsToVariantFormats.convert(genomicLocations),
                isoformOverrideSource,
                token,
                fields
        ).stream().forEachOrdered((a) -> {
            if (convertedVarsToOrigVarQueryMap.get(a.getVariant()).size() == 1) {
                String origVarQuery = convertedVarsToOrigVarQueryMap.get(a.getVariant()).toArray(new String[0])[0];
                a.setOriginalVariantQuery(origVarQuery);
                variantAnnotations.add(a);
            }
            else {
                for (String origVarQuery : convertedVarsToOrigVarQueryMap.get(a.getVariant())) {
                    // if query multiple "identical" variants
                    // for example "12,25398284,25398284,C,A" and "12,25398283,25398284,AC,AA"
                    // both variants should be returned
                    Gson gson = new Gson();
                    VariantAnnotation tempAnnotation = gson.fromJson(gson.toJson(a), VariantAnnotation.class);
                    tempAnnotation.setOriginalVariantQuery(origVarQuery);
                    variantAnnotations.add(tempAnnotation);
                }
            }
        });
        return variantAnnotations;
    }

    private Map<String, Set<String>> mapConvertedVarsToOrigVarQuery(List<GenomicLocation> genomicLocations) {
        Map<String, Set<String>> convertedVarsToOrigVarQueryMap = new HashMap<>();
        genomicLocations.forEach((gl) -> {
            gl.setOriginalInput(gl.toString());
            // If there are multiple identical genomic locations in the "genomicLocations" list
            // For example: 12,25398284,25398284,C,A and 12,25398283,25398284,AC,AA
            // We should keep all records in the "convertedVarsToOrigVarQueryMap"
            convertedVarsToOrigVarQueryMap.computeIfAbsent(this.genomicLocationToVariantFormat.convert(gl), k -> new HashSet<>()).add(gl.getOriginalInput());
        });
        return convertedVarsToOrigVarQueryMap;
    }

    @FunctionalInterface
    private static interface GenomicLocationToVariantFormat {
        String convert(GenomicLocation genomicLocation);
    }

    @FunctionalInterface
    private static interface GenomicLocationStringToVariantFormat {
        String convert(String genomicLocation);
    }

    @FunctionalInterface
    private static interface GenomicLocationsToVariantFormats {
        List<String> convert(List<GenomicLocation> genomicLocation);
    }

    @Override
    public String getVariantFormat(GenomicLocation genomicLocation) {
        return genomicLocationToVariantFormat.convert(genomicLocation);
    }
}
