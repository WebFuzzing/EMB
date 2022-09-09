package org.cbioportal.genome_nexus.service.enricher;

import static org.junit.Assert.assertEquals;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

// TODO: fix unnecessary stub tests
@RunWith(MockitoJUnitRunner.Silent.class)
public class IsoformAnnotationEnricherTest
{
    @Mock
    EnsemblService ensemblService;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void enrichAnnotationWithNoOverride() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        IsoformAnnotationEnricher enricher = new IsoformAnnotationEnricher(
            "genome_nexus", "genome_nexus", this.ensemblService
        );

        // override canonical transcripts with no one matching transcript
        Mockito
            .when(this.ensemblService.getCanonicalTranscriptIdsBySource("genome_nexus"))
            .thenReturn(new HashSet<>());

        VariantAnnotation annotation = variantMockData.get("3:g.14106026_14106037del");
        enricher.enrich(annotation);
        List<TranscriptConsequence> canonicalTranscripts =
            annotation
                .getTranscriptConsequences()
                .stream()
                .filter(t -> t.getCanonical() != null && t.getCanonical().equals("1"))
                .collect(Collectors.toList());

        assertEquals(
            1,
            canonicalTranscripts.size()
        );

        // original VEP assignment should stay intact when there is no matching transcript override
        assertEquals(
            "ENST00000424053",
            canonicalTranscripts.get(0).getTranscriptId()
        );
    }

    @Test
    public void enrichAnnotationWithSingleOverride() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        IsoformAnnotationEnricher enricher = new IsoformAnnotationEnricher(
            "uniprot", "uniprot", this.ensemblService
        );

        // override canonical transcripts with just one matching transcript
        Set<String> isoformOverrides = new HashSet<>();
        isoformOverrides.add("ENST00000429201");
        Mockito
            .when(this.ensemblService.getCanonicalTranscriptIdsBySource("uniprot"))
            .thenReturn(isoformOverrides);

        VariantAnnotation annotation = variantMockData.get("3:g.14106026_14106037del");
        enricher.enrich(annotation);
        List<TranscriptConsequence> canonicalTranscripts =
            annotation
                .getTranscriptConsequences()
                .stream()
                .filter(t -> t.getCanonical() != null && t.getCanonical().equals("1"))
                .collect(Collectors.toList());

        assertEquals(
            1,
            canonicalTranscripts.size()
        );

        assertEquals(
            "ENST00000429201",
            canonicalTranscripts.get(0).getTranscriptId()
        );
    }

    @Test
    public void enrichAnnotationWithMultipleOverride() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        IsoformAnnotationEnricher enricher = new IsoformAnnotationEnricher(
            "mskcc", "mskcc", this.ensemblService
        );

        // override canonical transcripts with just one matching transcript
        Set<String> isoformOverrides = new HashSet<>();
        isoformOverrides.add("ENST00000532753");
        isoformOverrides.add("ENST00000532924");
        isoformOverrides.add("ENST00000532880");
        Mockito
            .when(this.ensemblService.getCanonicalTranscriptIdsBySource("mskcc"))
            .thenReturn(isoformOverrides);

        VariantAnnotation annotation = variantMockData.get("3:g.14106026_14106037del");
        enricher.enrich(annotation);
        List<TranscriptConsequence> canonicalTranscripts =
            annotation
                .getTranscriptConsequences()
                .stream()
                .filter(t -> t.getCanonical() != null && t.getCanonical().equals("1"))
                .collect(Collectors.toList());

        assertEquals(
            3,
            canonicalTranscripts.size()
        );

        assertEquals(
            "ENST00000532753",
            canonicalTranscripts.get(0).getTranscriptId()
        );

        assertEquals(
            "ENST00000532880",
            canonicalTranscripts.get(1).getTranscriptId()
        );

        assertEquals(
            "ENST00000532924",
            canonicalTranscripts.get(2).getTranscriptId()
        );
    }
}
