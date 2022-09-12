/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
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
import org.cbioportal.genome_nexus.component.annotation.HugoGeneSymbolResolver;
import org.cbioportal.genome_nexus.component.annotation.ProteinChangeResolver;
import org.cbioportal.genome_nexus.persistence.IndexRepository;
import org.cbioportal.genome_nexus.service.*;

import org.cbioportal.genome_nexus.service.cached.CachedVariantRegionAnnotationFetcher;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.*;

@Service
public class RegionVariantAnnotationService extends BaseVariantAnnotationServiceImpl
{
    private static final Log LOG = LogFactory.getLog(RegionVariantAnnotationService.class);

    @Autowired
    public RegionVariantAnnotationService(
        CachedVariantRegionAnnotationFetcher cachedVariantRegionAnnotationFetcher,
        // Lazy autowire services used for enrichment,
        // otherwise we are getting circular dependency issues
        @Lazy EnsemblService ensemblService,
        @Lazy CancerHotspotService hotspotService,
        @Lazy MutationAssessorService mutationAssessorService,
        @Lazy MyVariantInfoService myVariantInfoService,
        @Lazy NucleotideContextService nucleotideContextService,
        @Lazy VariantAnnotationSummaryService variantAnnotationSummaryService,
        @Lazy PostTranslationalModificationService postTranslationalModificationService,
        @Lazy SignalMutationService signalMutationService,
        @Lazy OncokbService oncokbService,
        @Lazy ClinvarVariantAnnotationService clinvarVariantAnnotationService,
        IndexRepository indexRepository,
        ProteinChangeResolver proteinChangeResolver,
        HugoGeneSymbolResolver hugoGeneSymbolResolver
    ) {
        super(
            cachedVariantRegionAnnotationFetcher,
            ensemblService,
            hotspotService,
            mutationAssessorService,
            myVariantInfoService,
            nucleotideContextService,
            variantAnnotationSummaryService,
            postTranslationalModificationService,
            signalMutationService,
            oncokbService,
            clinvarVariantAnnotationService,
            indexRepository,
            proteinChangeResolver,
            hugoGeneSymbolResolver
        );
    }
}
