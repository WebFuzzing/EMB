/*
 * Copyright (c) 2016 - 2021 Memorial Sloan-Kettering Cancer Center.
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

package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.component.annotation.HotspotFilter;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.persistence.HotspotRepository;
import org.cbioportal.genome_nexus.service.CancerHotspotService;
import org.cbioportal.genome_nexus.service.GenomicLocationAnnotationService;
import org.cbioportal.genome_nexus.service.VariantAnnotationService;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Selcuk Onur Sumer
 */
@Service
public class CancerHotspotServiceImpl implements CancerHotspotService
{

    private final HotspotRepository hotspotRepository;
    private final VariantAnnotationService variantAnnotationService;
    private final HotspotFilter hotspotFilter;

    private final GenomicLocationAnnotationService genomicLocationAnnotationService;

    @Autowired
    public CancerHotspotServiceImpl(HotspotRepository hotspotRepository,
                                    VariantAnnotationService verifiedHgvsVariantAnnotationService,
                                    GenomicLocationAnnotationService verifiedGenomicLocationAnnotationServiceImpl,
                                    HotspotFilter hotspotFilter)
    {
        this.hotspotRepository = hotspotRepository;
        this.variantAnnotationService = verifiedHgvsVariantAnnotationService;
        this.genomicLocationAnnotationService = verifiedGenomicLocationAnnotationServiceImpl;
        this.hotspotFilter = hotspotFilter;
    }

    @Override
    public List<Hotspot> getHotspots(String transcriptId) throws CancerHotspotsWebServiceException
    {
        return this.hotspotRepository.findByTranscriptId(transcriptId);
    }

    @Override
    public List<Hotspot> getHotspots(TranscriptConsequence transcript,
                                     VariantAnnotation annotation) throws CancerHotspotsWebServiceException
    {
        Set<Hotspot> hotspots = new LinkedHashSet<>();

        for (Hotspot hotspot : this.getHotspots(transcript.getTranscriptId()))
        {
            // include only the hotspots matching certain criteria
            if (this.filterHotspot(hotspot, transcript, annotation)) {
                hotspots.add(hotspot);
            }
        }

        return new ArrayList<>(hotspots);
    }

    @Override
    public List<Hotspot> getHotspots()
    {
        return this.hotspotRepository.findAll();
    }

    public List<AggregatedHotspots> getHotspotsByTranscriptIds(List<String> transcriptIds) throws CancerHotspotsWebServiceException
    {
        List<AggregatedHotspots> hotspots = new ArrayList<>();
        for (String transcriptId : transcriptIds) {
            AggregatedHotspots aggregatedHotspots = new AggregatedHotspots();

            // add protein location information
            aggregatedHotspots.setTranscriptId(transcriptId);

            // query hotspots service by protein location
            aggregatedHotspots.setHotspots(this.getHotspots(transcriptId));
            hotspots.add(aggregatedHotspots);
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspotAnnotationsByVariant(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        CancerHotspotsWebServiceException
    {
        VariantAnnotation variantAnnotation = this.variantAnnotationService.getAnnotation(variant);
        List<Hotspot> hotspots = new ArrayList<>();

        if (variantAnnotation != null)
        {
            hotspots.addAll(this.getHotspotAnnotations(variantAnnotation));
        }

        return hotspots;
    }

    @Override
    public List<AggregatedHotspots> getHotspotAnnotationsByVariants(List<String> variants)
        throws CancerHotspotsWebServiceException
    {
        List<VariantAnnotation> variantAnnotations = this.variantAnnotationService.getAnnotations(variants);

        List<AggregatedHotspots> hotspots = new ArrayList<>();

        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            AggregatedHotspots aggregatedHotspots = new AggregatedHotspots();
            aggregatedHotspots.setHotspots(this.getHotspotAnnotations(variantAnnotation));
            aggregatedHotspots.setVariant(variantAnnotation.getVariant());

            hotspots.add(aggregatedHotspots);
        }

        return hotspots;
    }

    @Override
    public List<Hotspot> getHotspotAnnotationsByGenomicLocation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        CancerHotspotsWebServiceException
    {
        VariantAnnotation variantAnnotation = genomicLocationAnnotationService.getAnnotation(genomicLocation);
        return this.getHotspotAnnotations(variantAnnotation);
    }

    @Override
    public List<AggregatedHotspots> getHotspotAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations)
        throws CancerHotspotsWebServiceException
    {
        List<VariantAnnotation> variantAnnotations = this.genomicLocationAnnotationService.getAnnotations(genomicLocations);

        Map<String, GenomicLocation> variantToGenomicLocation = genomicLocations.stream()
            .collect(Collectors.toMap(genomicLocationAnnotationService::getVariantFormat, Function.identity()));
        List<AggregatedHotspots> hotspots = new ArrayList<>();
        for (VariantAnnotation variantAnnotation : variantAnnotations)
        {
            AggregatedHotspots aggregatedHotspots = new AggregatedHotspots();
            aggregatedHotspots.setHotspots(this.getHotspotAnnotations(variantAnnotation));
            aggregatedHotspots.setVariant(variantAnnotation.getVariant());
            aggregatedHotspots.setGenomicLocation(variantToGenomicLocation.get(variantAnnotation.getVariant()));
            hotspots.add(aggregatedHotspots);
        }

        return hotspots;
    }

    protected Boolean filterHotspot(Hotspot hotspot, TranscriptConsequence transcript, VariantAnnotation annotation)
    {
        return this.hotspotFilter.filter(hotspot, transcript, annotation);
    }

    private List<Hotspot> getHotspotAnnotations(VariantAnnotation variantAnnotation)
        throws CancerHotspotsWebServiceException
    {
        // we don't want to return duplicate items, so using a set here
        Set<Hotspot> hotspots = new LinkedHashSet<>();

        if (variantAnnotation.getTranscriptConsequences() != null)
        {
            for (TranscriptConsequence transcript : variantAnnotation.getTranscriptConsequences())
            {
                hotspots.addAll(this.getHotspots(transcript, variantAnnotation));
            }
        }

        return new ArrayList<>(hotspots);
    }

    @Override
    public List<AggregatedHotspots> getHotspotAnnotationsByProteinLocations(List<ProteinLocation> proteinLocations)
        throws CancerHotspotsWebServiceException
    {
        List<AggregatedHotspots> hotspots = new ArrayList<>();
        for (ProteinLocation proteinLocation : proteinLocations)
        {
            AggregatedHotspots aggregatedHotspots = new AggregatedHotspots();

            // add protein location information
            aggregatedHotspots.setProteinLocation(proteinLocation);

            // query hotspots service by protein location
            aggregatedHotspots.setHotspots(hotspotFilter.proteinLocationHotspotsFilter(this.getHotspots(proteinLocation.getTranscriptId()), proteinLocation));
            hotspots.add(aggregatedHotspots);
        }

        return hotspots;
    }
}
