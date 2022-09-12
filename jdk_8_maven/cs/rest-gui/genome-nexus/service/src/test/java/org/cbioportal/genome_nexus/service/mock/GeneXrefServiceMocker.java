package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.mockito.Mockito;

import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.eq;

public class GeneXrefServiceMocker
{
    public void mockMethods(Map<String, List<GeneXref>> geneXrefMockData,
                            GeneXrefService geneXrefService)
        throws EnsemblWebServiceException
    {
        this.mockGetGeneXrefs(geneXrefMockData, geneXrefService);
        this.mockGetEntrezGeneXref(geneXrefMockData, geneXrefService);
    }

    public void mockGetGeneXrefs(Map<String, List<GeneXref>> geneXrefMockData,
                                 GeneXrefService geneXrefService)
        throws EnsemblWebServiceException
    {
        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000020181")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000020181")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000027697")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000027697")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000089597")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000089597")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000100345")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000100345")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000122025")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000122025")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000125746")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000125746")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000133703")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000133703")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000138771")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000138771")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000154783")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000154783")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000157764")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000157764")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000162434")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000162434")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000165699")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000165699")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000169676")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000169676")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000180438")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000180438")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000181722")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000181722")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000183765")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000183765")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000187555")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000187555")
        );

        Mockito.when(
            geneXrefService.getGeneXrefs("ENSG00000213341")
        ).thenReturn(
            geneXrefMockData.get("ENSG00000213341")
        );
    }

    public void mockGetEntrezGeneXref(Map<String, List<GeneXref>> geneXrefMockData,
                                      GeneXrefService geneXrefService)
        throws EnsemblWebServiceException
    {
        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000020181"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000020181").get(5)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000027697"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000027697").get(8)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000089597"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000089597").get(5)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000100345"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000100345").get(12)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000122025"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000122025").get(8)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000125746"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000125746").get(4)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000133703"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000133703").get(15)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000138771"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000138771").get(9)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000154783"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000154783").get(5)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000157764"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000157764").get(19)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000162434"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000162434").get(4)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000165699"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000165699").get(8)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000169676"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000169676").get(8)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000180438"), Mockito.anyString())
        ).thenReturn(
            null
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000181722"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000181722").get(3)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000183765"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000183765").get(10)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000187555"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000187555").get(8)
        );

        Mockito.when(
            geneXrefService.getEntrezGeneXref(eq("ENSG00000213341"), Mockito.anyString())
        ).thenReturn(
            geneXrefMockData.get("ENSG00000213341").get(5)
        );
    }
}
