package org.cbioportal.genome_nexus.service.annotation;

import org.cbioportal.genome_nexus.component.annotation.CanonicalTranscriptResolver;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.mock.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.Silent.class)
public class EntrezGeneIdResolverTest
{
    @InjectMocks
    private EntrezGeneIdResolver entrezGeneIdResolver;

    @Mock
    private CanonicalTranscriptResolver canonicalTranscriptResolver;

    @Mock
    private GeneXrefService geneXrefService;

    @Mock
    private EnsemblService ensemblService;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final GeneXrefMockData geneXrefMockData = new GeneXrefMockData();
    private final CanonicalTranscriptResolverMocker canonicalTranscriptResolverMocker = new CanonicalTranscriptResolverMocker();
    private final GeneXrefServiceMocker geneXrefServiceMocker = new GeneXrefServiceMocker();

    private void mockGetEntrezGeneIdByHugoSymbol(String hugoSymbol, String entrezGeneId) {
        Mockito.when(ensemblService.getEntrezGeneIdByHugoSymbol(hugoSymbol)).thenReturn(entrezGeneId);
    }

    private void mockEnsemblService() {
        mockGetEntrezGeneIdByHugoSymbol("JAK1", "3716");
        mockGetEntrezGeneIdByHugoSymbol("FGD5", "152273");
        mockGetEntrezGeneIdByHugoSymbol("ZBTB20", "26137");
        mockGetEntrezGeneIdByHugoSymbol("DRD5", "1816");
        mockGetEntrezGeneIdByHugoSymbol("SHROOM3", "57619");
        mockGetEntrezGeneIdByHugoSymbol("IFNGR1", "3459");
        mockGetEntrezGeneIdByHugoSymbol("BRAF", "673");
        mockGetEntrezGeneIdByHugoSymbol("GPR124", "25960");
        mockGetEntrezGeneIdByHugoSymbol("TSC1", "7248");
        mockGetEntrezGeneIdByHugoSymbol("CHUK", "1147");
        mockGetEntrezGeneIdByHugoSymbol("GANAB", "23193");
        mockGetEntrezGeneIdByHugoSymbol("KRAS", "3845");
        mockGetEntrezGeneIdByHugoSymbol("FLT3", "2322");
        mockGetEntrezGeneIdByHugoSymbol("USP7", "7874");
        mockGetEntrezGeneIdByHugoSymbol("EML2", "24139");
        mockGetEntrezGeneIdByHugoSymbol("CHEK2", "11200");
        mockGetEntrezGeneIdByHugoSymbol("MYH9", "4627");
    }

    @Test
    public void resolveEntrezGeneIdForCanonical() throws IOException, EnsemblWebServiceException
    {
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();
        Map<String, List<GeneXref>> geneXrefMockData = this.geneXrefMockData.generateData();

        this.canonicalTranscriptResolverMocker.mockMethods(variantMockData, this.canonicalTranscriptResolver);
        this.geneXrefServiceMocker.mockMethods(geneXrefMockData, this.geneXrefService);
        this.mockEnsemblService();

        assertEquals(
            "3716",
            this.resolveForCanonical(variantMockData.get("1:g.65325832_65325833insG"))
        );

        assertNull(
            "No EntrezGene info exists for the canonical transcript of 3:g.14106026_14106037del",
            this.resolveForCanonical(variantMockData.get("3:g.14106026_14106037del"))
        );

        assertEquals(
            "152273",
            this.resolveForCanonical(variantMockData.get("3:g.14940279_14940280insCAT"))
        );

        assertEquals(
            "26137",
            this.resolveForCanonical(variantMockData.get("3:g.114058003del"))
        );

        assertEquals(
            "1816",
            this.resolveForCanonical(variantMockData.get("4:g.9784947_9784948insAGA"))
        );

        assertEquals(
            "57619",
            this.resolveForCanonical(variantMockData.get("4:g.77675978_77675979insC"))
        );

        assertEquals(
            "3459",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506del"))
        );

        assertEquals(
            "3459",
            this.resolveForCanonical(variantMockData.get("6:g.137519505_137519506delinsA"))
        );

        assertEquals(
            "673",
            this.resolveForCanonical(variantMockData.get("7:g.140453136A>T"))
        );

        assertEquals(
            "25960",
            this.resolveForCanonical(variantMockData.get("8:g.37696499_37696500insG"))
        );

        assertEquals(
            "7248",
            this.resolveForCanonical(variantMockData.get("9:g.135797242delinsAT"))
        );

        assertEquals(
            "1147",
            this.resolveForCanonical(variantMockData.get("10:g.101953779del"))
        );

        assertEquals(
            "23193",
            this.resolveForCanonical(variantMockData.get("11:g.62393546_62393547delinsAA"))
        );

        assertEquals(
            "3845",
            this.resolveForCanonical(variantMockData.get("12:g.25398285C>A"))
        );

        assertEquals(
            "2322",
            this.resolveForCanonical(variantMockData.get("13:g.28608258_28608275del"))
        );

        assertEquals(
            "7874",
            this.resolveForCanonical(variantMockData.get("16:g.9057113_9057114insCTG"))
        );

        assertEquals(
            "24139",
            this.resolveForCanonical(variantMockData.get("19:g.46141892_46141893delinsAA"))
        );

        assertEquals(
            "11200",
            this.resolveForCanonical(variantMockData.get("22:g.29091840_29091841delinsCA"))
        );

        assertEquals(
            "4627",
            this.resolveForCanonical(variantMockData.get("22:g.36689419_36689421del"))
        );
    }

    private String resolveForCanonical(VariantAnnotation variantAnnotation) throws EnsemblWebServiceException
    {
        return this.entrezGeneIdResolver.resolve(
            this.canonicalTranscriptResolver.resolve(variantAnnotation)
        );
    }
}
