/*
 * Copyright (c) 2016 Memorial Sloan-Kettering Cancer Center.
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

import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.exception.CancerHotspotsWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
public interface CancerHotspotService
{
    List<Hotspot> getHotspots(String transcriptId) throws CancerHotspotsWebServiceException;
    List<Hotspot> getHotspots(TranscriptConsequence transcript, VariantAnnotation annotation) throws CancerHotspotsWebServiceException;
    List<Hotspot> getHotspots() throws CancerHotspotsWebServiceException;
    List<AggregatedHotspots> getHotspotsByTranscriptIds(List<String> transcriptIds) throws CancerHotspotsWebServiceException;
    List<AggregatedHotspots> getHotspotAnnotationsByVariants(List<String> variants) throws CancerHotspotsWebServiceException;
    List<AggregatedHotspots> getHotspotAnnotationsByGenomicLocations(List<GenomicLocation> genomicLocations) throws CancerHotspotsWebServiceException;
    List<Hotspot> getHotspotAnnotationsByVariant(String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException, CancerHotspotsWebServiceException;
    List<Hotspot> getHotspotAnnotationsByGenomicLocation(String genomicLocation)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException, CancerHotspotsWebServiceException;
    List<AggregatedHotspots> getHotspotAnnotationsByProteinLocations(List<ProteinLocation> proteinLocations) throws CancerHotspotsWebServiceException;
}
