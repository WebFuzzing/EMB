package org.cbioportal.genome_nexus.service.internal;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;

import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.cached.CachedMutationAssessorFetcher;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.mock.MutationAssessorMockData;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MutationAssessorServiceTest
{
    @InjectMocks
    private MutationAssessorServiceImpl service;

    @Mock
    private CachedMutationAssessorFetcher fetcher;

    private MutationAssessorMockData mutationAssessorMockData = new MutationAssessorMockData();
    private VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();

    @Test
    public void getMutationAssessorByMutationAssessorVariant()
        throws ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        Map<String, MutationAssessor> mockData = this.mutationAssessorMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(fetcher.fetchAndCache("7,140453136,A,T")).thenReturn(mockData.get("7,140453136,A,T"));
        Mockito.when(fetcher.fetchAndCache("12,25398285,C,A")).thenReturn(mockData.get("12,25398285,C,A"));
        Mockito.when(fetcher.fetchAndCache("INVALID")).thenReturn(mockData.get("INVALID"));

        MutationAssessor mutationAssessor1 = service.getMutationAssessorByMutationAssessorVariant("7,140453136,A,T");
        assertEquals(mutationAssessor1.getHugoSymbol(), mockData.get("7,140453136,A,T").getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessorByMutationAssessorVariant("12,25398285,C,A");
        assertEquals(mutationAssessor2.getHugoSymbol(), mockData.get("12,25398285,C,A").getHugoSymbol());

        MutationAssessor mutationAssessor3 = service.getMutationAssessorByMutationAssessorVariant("INVALID");
        assertEquals(mutationAssessor3.getHugoSymbol(), mockData.get("INVALID").getHugoSymbol());
    }

    @Test
    public void getMutationAssessorByVariantAnnotation()
        throws ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException,
        IOException
    {
        Map<String, MutationAssessor> maMockData = this.mutationAssessorMockData.generateData();
        Map<String, VariantAnnotation> variantMockData = this.variantAnnotationMockData.generateData();

        // mock methods in order to prevent hitting the live mutation assessor web API
        Mockito.when(fetcher.fetchAndCache("7,140453136,A,T")).thenReturn(maMockData.get("7,140453136,A,T"));
        Mockito.when(fetcher.fetchAndCache("12,25398285,C,A")).thenReturn(maMockData.get("12,25398285,C,A"));

        MutationAssessor mutationAssessor1 = service.getMutationAssessor(variantMockData.get("7:g.140453136A>T"));
        assertEquals(mutationAssessor1.getHugoSymbol(), maMockData.get("7,140453136,A,T").getHugoSymbol());

        MutationAssessor mutationAssessor2 = service.getMutationAssessor(variantMockData.get("12:g.25398285C>A"));
        assertEquals(mutationAssessor2.getHugoSymbol(), maMockData.get("12,25398285,C,A").getHugoSymbol());
    }
}
