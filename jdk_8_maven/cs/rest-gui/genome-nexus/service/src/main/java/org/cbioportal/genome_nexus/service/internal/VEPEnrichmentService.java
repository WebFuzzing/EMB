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
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.AnnotationEnricher;
import org.cbioportal.genome_nexus.service.EnrichmentService;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Benjamin Gross
 */
@Service
public class VEPEnrichmentService implements EnrichmentService {
    private static final Log LOG = LogFactory.getLog(VEPEnrichmentService.class);
    private Map<String, AnnotationEnricher> enrichers;

    @Override
    public void enrichAnnotation(VariantAnnotation variantAnnotation) {
        // modify JSON returned by VEP
        if (enrichers != null) {
            for (AnnotationEnricher enricher: this.enrichers.values()) {
                try {
                    enricher.enrich(variantAnnotation);
                } catch (Exception e) {
                    LOG.warn("Failed to enrich with " + enricher.getId() + ": " + variantAnnotation.getVariant() + " " + e.getLocalizedMessage());
                }
            }
        }
    }

    @Override
    public void enrichAnnotations(List<VariantAnnotation> variantAnnotations) {
        // modify JSON returned by VEP
        if (enrichers != null) {
            for (AnnotationEnricher enricher: this.enrichers.values()) {
                try {
                    enricher.enrich(variantAnnotations);
                } catch (Exception e) {
                    LOG.warn("Error while enriching " + variantAnnotations.size() + " annotations with " + enricher.getId());
                }
            }
        }
    }

    @Override
    public void registerEnricher(AnnotationEnricher enricher)
    {
        // initiate enricher list if not initiated yet
        if (enrichers == null)
        {
            // linked hash map ensures that enrichers are executed by the same order they are registered
            enrichers = new LinkedHashMap<>();
        }

        enrichers.put(enricher.getId(), enricher);
    }

    @Override
    public void unregisterEnricher(String id)
    {
        if (enrichers != null)
        {
            enrichers.put(id, null);
        }
    }
}
