package org.cbioportal.genome_nexus.component.annotation;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

// TODO: fix unnecessary stub tests
@RunWith(MockitoJUnitRunner.Silent.class)
public class CanonicalTranscriptResolverTest
{
    @InjectMocks
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Spy
    private TranscriptConsequencePrioritizer consequencePrioritizer;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void resolveCanonicalTranscript() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        assertEquals(
            variantMockData.get("1:g.65325832_65325833insG").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertEquals(
            variantMockData.get("3:g.14106026_14106037del").getTranscriptConsequences().get(1),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            variantMockData.get("3:g.14940279_14940280insCAT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            variantMockData.get("3:g.114058003del").getTranscriptConsequences().get(5),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            variantMockData.get("4:g.9784947_9784948insAGA").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            variantMockData.get("4:g.77675978_77675979insC").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            variantMockData.get("6:g.137519505_137519506del").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            variantMockData.get("6:g.137519505_137519506delinsA").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            variantMockData.get("7:g.140453136A>T").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            variantMockData.get("8:g.37696499_37696500insG").getTranscriptConsequences().get(2),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            variantMockData.get("9:g.135797242delinsAT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            variantMockData.get("10:g.101953779del").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            variantMockData.get("11:g.62393546_62393547delinsAA").getTranscriptConsequences().get(1),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            variantMockData.get("12:g.25398285C>A").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            variantMockData.get("13:g.28608258_28608275del").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            variantMockData.get("16:g.9057113_9057114insCTG").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            variantMockData.get("19:g.46141892_46141893delinsAA").getTranscriptConsequences().get(8),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            variantMockData.get("22:g.29091840_29091841delinsCA").getTranscriptConsequences().get(5),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            variantMockData.get("22:g.36689419_36689421del").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("22:g.36689419_36689421del"))
        );

        assertEquals(
            variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT").getTranscriptConsequences().get(0),
            this.canonicalTranscriptResolver.resolve(variantMockData.get("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT"))
        );
    }

    @Test
    public void resolveCanonicalTranscriptWithMultipleCanonicals() throws IOException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        VariantAnnotation annotation = variantMockData.get("3:g.14106026_14106037del");

        // mark "ENST00000532753", "ENST00000532924", and "ENST00000532880" canonical
        for (TranscriptConsequence transcript: annotation.getTranscriptConsequences()) {
            if (
                transcript.getTranscriptId().equals("ENST00000532753") ||
                transcript.getTranscriptId().equals("ENST00000532924") ||
                transcript.getTranscriptId().equals("ENST00000532880")
            ) {
                transcript.setCanonical("1");
            }
            else {
                transcript.setCanonical("0");
            }
        }

        // should pick the most impactful one (inframe_deletion)
        assertEquals(
            "ENST00000532924",
            this.canonicalTranscriptResolver
                .resolve(variantMockData.get("3:g.14106026_14106037del"))
                .getTranscriptId()
        );
    }
}
