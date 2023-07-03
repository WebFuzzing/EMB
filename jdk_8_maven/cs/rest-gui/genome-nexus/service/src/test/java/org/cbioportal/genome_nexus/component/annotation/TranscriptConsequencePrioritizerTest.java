package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TranscriptConsequencePrioritizerTest
{
    @InjectMocks
    private TranscriptConsequencePrioritizer transcriptConsequencePrioritizer;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void transcriptWithMostSevereConsequence() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        VariantAnnotation annotation;

        annotation = variantMockData.get("1:g.65325832_65325833insG");

        // "most_severe_consequence": "frameshift_variant"
        // "transcript_consequences"[0]["consequence_terms"]: ["frameshift_variant"]
        assertEquals(
            "transcript at index 0 should be selected for 1:g.65325832_65325833insG",
            annotation.getTranscriptConsequences().get(0),
            transcriptConsequencePrioritizer.transcriptWithMostSevereConsequence(annotation.getTranscriptConsequences(),
                annotation.getMostSevereConsequence())
        );

        annotation = variantMockData.get("8:g.37696499_37696500insG");

        // "most_severe_consequence": "frameshift_variant"
        // "transcript_consequences"[1]["consequence_terms"]: ["frameshift_variant"]
        assertEquals(
            "transcript at index 1 should be selected for 8:g.37696499_37696500insG",
            annotation.getTranscriptConsequences().get(1),
            transcriptConsequencePrioritizer.transcriptWithMostSevereConsequence(annotation.getTranscriptConsequences(),
                annotation.getMostSevereConsequence())
        );

        annotation = variantMockData.get("11:g.62393546_62393547delinsAA");

        // "most_severe_consequence": "frameshift_variant"
        // "transcript_consequences"[1]["consequence_terms"]: ["frameshift_variant"]
        assertEquals(
            "transcript at index 1 should be selected for 11:g.62393546_62393547delinsAA",
            annotation.getTranscriptConsequences().get(1),
            transcriptConsequencePrioritizer.transcriptWithMostSevereConsequence(annotation.getTranscriptConsequences(),
                annotation.getMostSevereConsequence())
        );

        annotation = variantMockData.get("19:g.46141892_46141893delinsAA");

        // "most_severe_consequence": "splice_acceptor_variant"
        // "transcript_consequences"[0]["consequence_terms"]: ["splice_acceptor_variant"]
        assertEquals(
            "transcript at index 0 should be selected for 19:g.46141892_46141893delinsAA",
            annotation.getTranscriptConsequences().get(0),
            transcriptConsequencePrioritizer.transcriptWithMostSevereConsequence(annotation.getTranscriptConsequences(),
                annotation.getMostSevereConsequence())
        );
    }

    @Test
    public void pickHighestPriorityConsequence()
    {
        String[][] consequenceTerms = {
            {},
            {"INVALID"},
            {"INVALID_ONE", "INVALID_TWO"},
            {"frameshift_variant"},
            {"splice_acceptor_variant", "coding_sequence_variant", "NMD_transcript_variant"},
            {"3_prime_UTR_variant", "NMD_transcript_variant"},
            {"intron_variant", "non_coding_transcript_variant"},
            {"missense_variant", "NMD_transcript_variant"},
            {"protein_altering_variant", "NMD_transcript_variant"},
            {"splice_acceptor_variant", "5_prime_UTR_variant"},
            {"splice_acceptor_variant", "non_coding_transcript_exon_variant"},
            {"INVALID", "5_prime_UTR_variant"},
        };

        assertNull(
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(null)
        );

        assertNull(
            "No consequence to select for an empty list",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[0]))
        );

        assertNull("No known consequence to select",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[1]))
        );

        assertNull("No known consequence to select",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[2]))
        );

        assertEquals("frameshift_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[3]))
        );

        assertEquals("splice_acceptor_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[4]))
        );

        assertEquals("3_prime_UTR_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[5]))
        );

        assertEquals("intron_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[6]))
        );

        assertEquals("missense_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[7]))
        );

        assertEquals("protein_altering_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[8]))
        );

        assertEquals("splice_acceptor_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[9]))
        );

        assertEquals("splice_acceptor_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[10]))
        );

        assertEquals("5_prime_UTR_variant",
            this.transcriptConsequencePrioritizer.pickHighestPriorityConsequence(Arrays.asList(consequenceTerms[11]))
        );
    }
}
