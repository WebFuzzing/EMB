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

package org.cbioportal.genome_nexus.model;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfoAnnotation;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

/**
 * @author Benjamin Gross
 * @author Selcuk Onur Sumer
 */
@Document(collection = "vep.annotation")
public class VariantAnnotation
{
    @Id
    private String variant;        // used as an id for mongodb record
    private String annotationJSON; // raw annotation JSON

    private String variantId;     // variant id
    private String assemblyName;  // NCBI build number
    private String seqRegionName; // chromosome
    private Integer start;         // start position
    private Integer end;           // end position
    private String alleleString;  // reference allele & variant allele
    private Integer strand;
    private String mostSevereConsequence;
    private List<ColocatedVariant> colocatedVariants;
    private List<IntergenicConsequences> intergenicConsequences;
    private List<TranscriptConsequence> transcriptConsequences;
    private Boolean successfullyAnnotated;

    private MutationAssessorAnnotation mutationAssessorAnnotation;
    private NucleotideContextAnnotation nucleotideContextAnnotation;
    private MyVariantInfoAnnotation myVariantInfoAnnotation;
    private HotspotAnnotation hotspotAnnotation;
    private PtmAnnotation ptmAnnotation;
    private OncokbAnnotation oncokbAnnotation;
    private ClinvarAnnotation clinvarAnnotation;
    private VariantAnnotationSummary annotationSummary;
    private SignalAnnotation signalAnnotation;
    private String originalVariantQuery;
    private Map<String, Object> dynamicProps;

    public VariantAnnotation()
    {
        this(null, null);
    }

    public VariantAnnotation(String variant)
    {
        this(variant, null);
    }

    public VariantAnnotation(String variant, String annotationJSON)
    {
        this.variant = variant;
        this.annotationJSON = annotationJSON;
        this.dynamicProps = new LinkedHashMap<>();
        this.successfullyAnnotated = !(annotationJSON == null);
    }

    public String getVariant()
    {
        return variant;
    }

    public void setVariant(String variant)
    {
        this.variant = variant;
    }

    public String getAnnotationJSON()
    {
        return annotationJSON;
    }

    public void setAnnotationJSON(String annotationJSON)
    {
        this.annotationJSON = annotationJSON;
    }

    @Field(value="id")
    public String getVariantId()
    {
        return variantId;
    }

    public void setVariantId(String variantId)
    {
        this.variantId = variantId;
    }

    @Field(value="assembly_name")
    public String getAssemblyName()
    {
        return assemblyName;
    }

    public void setAssemblyName(String assemblyName)
    {
        this.assemblyName = assemblyName;
    }

    @Field(value="seq_region_name")
    public String getSeqRegionName()
    {
        return seqRegionName;
    }

    public void setSeqRegionName(String seqRegionName)
    {
        this.seqRegionName = seqRegionName;
    }

    @Field(value="start")
    public Integer getStart()
    {
        return start;
    }

    public void setStart(Integer start)
    {
        this.start = start;
    }

    @Field(value="end")
    public Integer getEnd()
    {
        return end;
    }

    public void setEnd(Integer end)
    {
        this.end = end;
    }

    @Field(value="allele_string")
    public String getAlleleString()
    {
        return alleleString;
    }

    public void setAlleleString(String alleleString)
    {
        this.alleleString = alleleString;
    }

    @Field(value="strand")
    public Integer getStrand()
    {
        return strand;
    }

    public void setStrand(Integer strand)
    {
        this.strand = strand;
    }

    @Field(value="most_severe_consequence")
    public String getMostSevereConsequence()
    {
        return mostSevereConsequence;
    }

    public void setMostSevereConsequence(String mostSevereConsequence)
    {
        this.mostSevereConsequence = mostSevereConsequence;
    }

    @Field(value="colocated_variants")
    public List<ColocatedVariant> getColocatedVariants()
    {
        return colocatedVariants;
    }
    public void set(List<ColocatedVariant> colocatedVariants)
    {
        this.colocatedVariants = colocatedVariants;
    }

    @Field(value="intergenic_consequences")
    public List<IntergenicConsequences> getIntergenicConsequences()
    {
        return intergenicConsequences;
    }
    public void setIntergenicConsequences(List<IntergenicConsequences> intergenicConsequences)
    {
        this.intergenicConsequences = intergenicConsequences;
    }

    @Field(value="transcript_consequences")
    public List<TranscriptConsequence> getTranscriptConsequences()
    {
        return transcriptConsequences;
    }

    public void setTranscriptConsequences(List<TranscriptConsequence> transcriptConsequences)
    {
        this.transcriptConsequences = transcriptConsequences;
    }

    public VariantAnnotationSummary getAnnotationSummary() {
        return annotationSummary;
    }

    public void setAnnotationSummary(VariantAnnotationSummary annotationSummary) {
        this.annotationSummary = annotationSummary;
    }

    @Override
    public String toString()
    {
        return annotationJSON;
    }

    public NucleotideContextAnnotation getNucleotideContextAnnotation() {
        return nucleotideContextAnnotation;
    }

    public void setNucleotideContextAnnotation(NucleotideContextAnnotation nucleotideContextAnnotation) {
        this.nucleotideContextAnnotation = nucleotideContextAnnotation;
    }

    public MutationAssessorAnnotation getMutationAssessorAnnotation() {
        return mutationAssessorAnnotation;
    }

    public void setMutationAssessorAnnotation(MutationAssessorAnnotation mutationAssessorAnnotation) {
        this.mutationAssessorAnnotation = mutationAssessorAnnotation;
    }

    public MyVariantInfoAnnotation getMyVariantInfoAnnotation() {
        return myVariantInfoAnnotation;
    }

    public void setMyVariantInfoAnnotation(MyVariantInfoAnnotation myVariantInfoAnnotation) {
        this.myVariantInfoAnnotation = myVariantInfoAnnotation;
    }

    public HotspotAnnotation getHotspotAnnotation() {
        return hotspotAnnotation;
    }

    public void setHotspotAnnotation(HotspotAnnotation hotspotAnnotation) {
        this.hotspotAnnotation = hotspotAnnotation;
    }

    public PtmAnnotation getPtmAnnotation() {
        return ptmAnnotation;
    }

    public String getHgvsg() {
        if (this.getVariantId() != null && this.getVariantId().contains("g."))
        {
            return this.getVariantId();
        } else if (this.getTranscriptConsequences() == null) {
            return null;
        } else {
            // id is not of hgvsg format
            for (TranscriptConsequence ts : this.getTranscriptConsequences()) {
                if (ts.getHgvsg() != null && !ts.getHgvsg().isEmpty()) {
                    return ts.getHgvsg();
                }
            }
            return null;
        }
    }

    public void setPtmAnnotation(PtmAnnotation ptmAnnotation) {
        this.ptmAnnotation = ptmAnnotation;
    }

    public SignalAnnotation getSignalAnnotation() {
        return signalAnnotation;
    }

    public void setSignalAnnotation(SignalAnnotation signalAnnotation) {
        this.signalAnnotation = signalAnnotation;
    }

    public OncokbAnnotation getOncokbAnnotation() {
        return oncokbAnnotation;
    }

    public void setOncokbAnnotation(OncokbAnnotation oncokbAnnotation) {
        this.oncokbAnnotation = oncokbAnnotation;
    }

    public ClinvarAnnotation getClinvarAnnotation() {
        return clinvarAnnotation;
    }

    public void setClinvarAnnotation(ClinvarAnnotation clinvarAnnotation) {
        this.clinvarAnnotation = clinvarAnnotation;
    }

    public void setSuccessfullyAnnotated(Boolean successfullyAnnotated) {
        this.successfullyAnnotated = successfullyAnnotated;
    }

    public Boolean isSuccessfullyAnnotated() {
        return successfullyAnnotated;
    }

    public void setOriginalVariantQuery(String originalVariantQuery) {
        this.originalVariantQuery = originalVariantQuery;
    }

    public String getOriginalVariantQuery() {
        return this.originalVariantQuery;
    }

    public void setDynamicProp(String key, Object value)
    {
        this.dynamicProps.put(key, value);
    }

    public Map<String, Object> getDynamicProps()
    {
        return this.dynamicProps;
    }
}
