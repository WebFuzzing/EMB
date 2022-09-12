package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.mock.CanonicalTranscriptResolverMocker;
import org.cbioportal.genome_nexus.service.mock.GeneXrefMockData;
import org.cbioportal.genome_nexus.service.mock.GeneXrefServiceMocker;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

// TODO: fix unnecessary stub mockito (silence for now)
@RunWith(MockitoJUnitRunner.Silent.class)
public class EntrezGeneXrefResolverTest
{
    private EntrezGeneXrefResolver entrezGeneXrefResolver = new EntrezGeneXrefResolver();

    @Mock
    private GeneXrefService geneXrefService;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final GeneXrefMockData geneXrefMockData = new GeneXrefMockData();
    private final GeneXrefServiceMocker geneXrefServiceMocker = new GeneXrefServiceMocker();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();

    @Test
    public void resolveEntrezGeneIdForCanonical() throws IOException, EnsemblWebServiceException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, List<GeneXref>> geneXrefMockData = this.geneXrefMockData.generateData();

        this.geneXrefServiceMocker.mockMethods(geneXrefMockData, this.geneXrefService);
        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);

        assertEquals(
            "3716",
            this.resolveGeneXref("1:g.65325832_65325833insG", variantMockData).getPrimaryId()
        );

        assertNull(
            "No EntrezGene info exists for the canonical transcript of 3:g.14106026_14106037del",
            this.resolveGeneXref("3:g.14106026_14106037del", variantMockData)
        );

        assertEquals(
            "152273",
            this.resolveGeneXref("3:g.14940279_14940280insCAT", variantMockData).getPrimaryId()
        );

        assertEquals(
            "152273",
            this.resolveGeneXref("3:g.14940279_14940280insCAT", variantMockData).getPrimaryId()
        );

        assertEquals(
            "26137",
            this.resolveGeneXref("3:g.114058003del", variantMockData).getPrimaryId()
        );

        assertEquals(
            "1816",
            this.resolveGeneXref("4:g.9784947_9784948insAGA", variantMockData).getPrimaryId()
        );

        assertEquals(
            "57619",
            this.resolveGeneXref("4:g.77675978_77675979insC", variantMockData).getPrimaryId()
        );

        assertEquals(
            "3459",
            this.resolveGeneXref("6:g.137519505_137519506del", variantMockData).getPrimaryId()
        );

        assertEquals(
            "3459",
            this.resolveGeneXref("6:g.137519505_137519506delinsA", variantMockData).getPrimaryId()
        );

        assertEquals(
            "673",
            this.resolveGeneXref("7:g.140453136A>T", variantMockData).getPrimaryId()
        );

        assertEquals(
            "25960",
            this.resolveGeneXref("8:g.37696499_37696500insG", variantMockData).getPrimaryId()
        );

        assertEquals(
            "7248",
            this.resolveGeneXref("9:g.135797242delinsAT", variantMockData).getPrimaryId()
        );

        assertEquals(
            "1147",
            this.resolveGeneXref("10:g.101953779del", variantMockData).getPrimaryId()
        );

        assertEquals(
            "23193",
            this.resolveGeneXref("11:g.62393546_62393547delinsAA", variantMockData).getPrimaryId()
        );

        assertEquals(
            "3845",
            this.resolveGeneXref("12:g.25398285C>A", variantMockData).getPrimaryId()
        );

        assertEquals(
            "2322",
            this.resolveGeneXref("13:g.28608258_28608275del", variantMockData).getPrimaryId()
        );

        assertEquals(
            "7874",
            this.resolveGeneXref("16:g.9057113_9057114insCTG", variantMockData).getPrimaryId()
        );

        assertEquals(
            "24139",
            this.resolveGeneXref("19:g.46141892_46141893delinsAA", variantMockData).getPrimaryId()
        );

        assertEquals(
            "11200",
            this.resolveGeneXref("22:g.29091840_29091841delinsCA", variantMockData).getPrimaryId()
        );

        assertEquals(
            "4627",
            this.resolveGeneXref("22:g.36689419_36689421del", variantMockData).getPrimaryId()
        );
    }

    private GeneXref resolveGeneXref(
        String variantId,
        Map<String, VariantAnnotation> variantMockData
    ) throws EnsemblWebServiceException
    {
        return this.entrezGeneXrefResolver.resolve(
            this.geneXrefService.getGeneXrefs(
                this.canonicalTranscriptResolver.resolve(variantMockData.get(variantId)).getGeneId()
            ),
            this.canonicalTranscriptResolver.resolve(variantMockData.get(variantId)).getGeneSymbol()
        );
    }
}
