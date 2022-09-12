package org.cbioportal.genome_nexus.component.search;

import org.cbioportal.genome_nexus.component.annotation.*;
import org.cbioportal.genome_nexus.model.*;
import org.cbioportal.genome_nexus.service.mock.SignalMockData;
import org.cbioportal.genome_nexus.service.mock.VariantAnnotationMockData;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.Silent.class)
public class SignalSearchEngineTest {

    @InjectMocks
    private SignalIndexBuilder indexBuilder;

    @Spy
    private SignalSearchEngine searchEngine;

    @Spy
    private NotationConverter notationConverter;

    @Mock
    private ProteinChangeResolver proteinChangeResolver;

    private final VariantAnnotationMockData variantAnnotationMockData = new VariantAnnotationMockData();
    private final SignalMockData signalMockData = new SignalMockData();

    private List<SignalQuery> signalIndex = new ArrayList<>();

    @Before
    public void setupTests() throws IOException
    {
        // only init once
        if (signalIndex.size() == 0) {
            Map<String, SignalMutation> signalMockData = this.signalMockData.generateData();
            Map<String, VariantAnnotation> variantAnnotationMockData = this.variantAnnotationMockData.generateData();

            Mockito
                .when(proteinChangeResolver.resolveHgvspShort(variantAnnotationMockData.get("7:g.55241617G>A")))
                .thenReturn("p.V689M");
            Mockito
                .when(proteinChangeResolver.resolveHgvspShort(variantAnnotationMockData.get("7:g.140453136A>T")))
                .thenReturn("p.V600E");
            Mockito
                .when(proteinChangeResolver.resolveHgvspShort(variantAnnotationMockData.get("13:g.32914438del")))
                .thenReturn("p.S1982Rfs*22");
            Mockito
                .when(proteinChangeResolver.resolveHgvspShort(variantAnnotationMockData.get("17:g.41276045_41276046del")))
                .thenReturn("p.E23Vfs*17");
            Mockito
                .when(proteinChangeResolver.resolveHgvspShort(variantAnnotationMockData.get("17:g.41276046_41276047insG")))
                .thenReturn("p.E23Afs*18");

            this.signalIndex = this.indexBuilder.buildQueryIndex(
                new ArrayList<>(signalMockData.values()),
                new ArrayList<>(variantAnnotationMockData.values())
            );
        }
    }

    @Test
    public void searchByHugoSymbol() {
        List<SignalQuery> brca1 = this.searchEngine.searchByHugoSymbol(this.signalIndex, "BRCA1");
        List<SignalQuery> brca2 = this.searchEngine.searchByHugoSymbol(this.signalIndex, "brca2");
        List<SignalQuery> brca = this.searchEngine.searchByHugoSymbol(this.signalIndex, "Brca");
        List<SignalQuery> rca2 = this.searchEngine.searchByHugoSymbol(this.signalIndex, "RCA2");
        List<SignalQuery> noSuchGene = this.searchEngine.searchByHugoSymbol(this.signalIndex, "no_such_gene");

        // query string: BRCA1
        // expected: 1 result, exact match
        assertEquals(
            "BRCA1 query should return only one gene",
            1,
            brca1.size()
        );
        assertEquals(
            "BRCA1 query should be an exact match",
            SignalMatchType.EXACT,
            brca1.get(0).getMatchType()
        );
        assertEquals(
            "BRCA1 query should be a gene query only",
            SignalQueryType.GENE,
            brca1.get(0).getQueryType()
        );
        assertEquals(
            "BRCA1 query should match the hugo symbol",
            "BRCA1",
            brca1.get(0).getHugoSymbol()
        );

        // query string: brca2
        // expected: 1 result, exact match
        assertEquals(
            "brca2 query should return only one gene",
            1,
            brca2.size()
        );
        assertEquals(
            "brca2 query should be an exact match",
            SignalMatchType.EXACT,
            brca2.get(0).getMatchType()
        );
        assertEquals(
            "brca2 query should be a gene query only",
            SignalQueryType.GENE,
            brca2.get(0).getQueryType()
        );
        assertEquals(
            "brca2 query should match the hugo symbol",
            "BRCA2",
            brca2.get(0).getHugoSymbol()
        );

        // query string: Brca
        // expected: 2 results, starts with
        assertEquals(
            "Brca query should return two genes",
            2,
            brca.size()
        );
        assertEquals(
            "BRCA1 match should start with Brca",
            SignalMatchType.STARTS_WITH,
            brca.get(0).getMatchType()
        );
        assertEquals(
            "BRCA2 match should start with Brca",
            SignalMatchType.STARTS_WITH,
            brca.get(1).getMatchType()
        );

        // query string: RCA2
        // expected: 1 result, partial match
        assertEquals(
            "RCA2 query should only return one gene",
            1,
            rca2.size()
        );
        assertEquals(
            "RCA2 query should be a partial match",
            SignalMatchType.PARTIAL,
            rca2.get(0).getMatchType()
        );
        assertEquals(
            "RCA2 query should be a gene query only",
            SignalQueryType.GENE,
            rca2.get(0).getQueryType()
        );
        assertEquals(
            "RCA2 query should match the hugo symbol",
            "BRCA2",
            rca2.get(0).getHugoSymbol()
        );

        // query string: no_such_gene
        // expected: no result
        assertEquals(
            "no_such_gene query should NOT match anything",
            0,
            noSuchGene.size()
        );
    }

    @Test
    public void searchByVariant() {
        List<SignalQuery> v7g55241617G_A = this.searchEngine.searchByVariant(this.signalIndex, "7:g.55241617G>A");
        List<SignalQuery> v7g = this.searchEngine.searchByVariant(this.signalIndex, "7:g");
        List<SignalQuery> noSuchVariant = this.searchEngine.searchByVariant(this.signalIndex, "no_such_variant");

        // query string: 7:g.55241617G>A
        // expected: 1 result, exact match
        assertEquals(
            "7:g.55241617G>A query should return only one variant",
            1,
            v7g55241617G_A.size()
        );
        assertEquals(
            "7:g.55241617G>A query should be an exact match",
            SignalMatchType.EXACT,
            v7g55241617G_A.get(0).getMatchType()
        );
        assertEquals(
            "7:g.55241617G>A query should be a variant query only",
            SignalQueryType.VARIANT,
            v7g55241617G_A.get(0).getQueryType()
        );
        assertEquals(
            "7:g.55241617G>A query should match the variant",
            "7:g.55241617G>A",
            v7g55241617G_A.get(0).getVariant()
        );

        // query string: 7:g
        // expected: 4 results, 2 starts with + 2 partial
        // When there are multiple matches, the ones start with the query string
        // has ranked higher compared to partial matches
        assertEquals(
            "7:g search should return 4 variants",
            4,
            v7g.size()
        );
        assertEquals(
            "First match should start with the query",
            SignalMatchType.STARTS_WITH,
            v7g.get(0).getMatchType()
        );
        assertEquals(
            "Second match should start with the query",
            SignalMatchType.STARTS_WITH,
            v7g.get(1).getMatchType()
        );
        assertEquals(
            "Third match should be partial",
            SignalMatchType.PARTIAL,
            v7g.get(2).getMatchType()
        );
        assertEquals(
            "Fourth match should be partial",
            SignalMatchType.PARTIAL,
            v7g.get(3).getMatchType()
        );

        // query string: no_such_variant
        // expected: no result
        assertEquals(
            "no_such_variant query should NOT match anything",
            0,
            noSuchVariant.size()
        );
    }

    @Test
    public void searchByRegion() {
        List<SignalQuery> r13_32914438_32914438 = this.searchEngine.searchByRegion(this.signalIndex, "13:32914438-32914438");
        List<SignalQuery> r17_4127604 = this.searchEngine.searchByRegion(this.signalIndex, "17:4127604");
        List<SignalQuery> noSuchRegion = this.searchEngine.searchByRegion(this.signalIndex, "no_such_region");

        // query string: 13:32914438-32914438
        // expected: 1 result, exact match
        assertEquals(
            "13:32914438-32914438 query should return only 1 region",
            1,
            r13_32914438_32914438.size()
        );
        assertEquals(
            "13:32914438-32914438 query should be an exact match",
            SignalMatchType.EXACT,
            r13_32914438_32914438.get(0).getMatchType()
        );
        assertEquals(
            "13:32914438-32914438 query should be a region query only",
            SignalQueryType.REGION,
            r13_32914438_32914438.get(0).getQueryType()
        );
        assertEquals(
            "13:32914438-32914438 query should match the region",
            "13:32914438-32914438",
            r13_32914438_32914438.get(0).getRegion()
        );

        // query string: 17:4127604
        // expected: 2 results, starts with
        assertEquals(
            "17:4127604 query should return 2 regions",
            2,
            r17_4127604.size()
        );
        assertEquals(
            "17:41276045-41276046 should start with 17:4127604",
            SignalMatchType.STARTS_WITH,
            r17_4127604.get(0).getMatchType()
        );
        assertEquals(
            "17:41276046-41276047 should start with 17:4127604",
            SignalMatchType.STARTS_WITH,
            r17_4127604.get(1).getMatchType()
        );

        // query string: no_such_region
        // expected: no result
        assertEquals(
            "no_such_variant query should NOT match anything",
            0,
            noSuchRegion.size()
        );
    }

    @Test
    public void searchByAlteration() {
        List<SignalQuery> v600e = this.searchEngine.searchByAlteration(this.signalIndex, "v600e");
        List<SignalQuery> v6 = this.searchEngine.searchByAlteration(this.signalIndex, "V6");
        List<SignalQuery> e23 = this.searchEngine.searchByAlteration(this.signalIndex, "E23");
        List<SignalQuery> noSuchAlteration = this.searchEngine.searchByAlteration(this.signalIndex, "no_such_alteration");

        // query string: v600e
        // expected: 1 result, exact match
        assertEquals(
            "v600e query should return only 1 alteration",
            1,
            v600e.size()
        );
        assertEquals(
            "v600e query should be an exact match",
            SignalMatchType.EXACT,
            v600e.get(0).getMatchType()
        );
        assertEquals(
            "v600e query should be an alteration query only",
            SignalQueryType.ALTERATION,
            v600e.get(0).getQueryType()
        );
        assertEquals(
            "v600e query should match the alteration",
            "V600E",
            v600e.get(0).getAlteration()
        );

        // query string: V6
        // expected: 2 results, starts with
        assertEquals(
            "V6 query should return 2 alterations",
            2,
            v6.size()
        );
        assertEquals(
            "V600E should start with V6",
            SignalMatchType.STARTS_WITH,
            v6.get(0).getMatchType()
        );
        assertEquals(
            "V689M should start with V6",
            SignalMatchType.STARTS_WITH,
            v6.get(1).getMatchType()
        );

        // query string: e23
        // expected: 2 results, starts with
        assertEquals(
            "e23 query should return 2 alterations",
            2,
            e23.size()
        );
        assertEquals(
            "E23Vfs*17 should start with e23",
            SignalMatchType.STARTS_WITH,
            e23.get(0).getMatchType()
        );
        assertEquals(
            "E23Afs*18 should start with e23",
            SignalMatchType.STARTS_WITH,
            e23.get(1).getMatchType()
        );

        // query string: no_such_alteration
        // expected: no result
        assertEquals(
            "no_such_alteration query should NOT match anything",
            0,
            noSuchAlteration.size()
        );
    }

    @Test
    public void searchByGeneAndAlteration() {
        List<SignalQuery> brafV600e = this.searchEngine.searchByAlteration(this.signalIndex, "braf v600e");
        List<SignalQuery> brV6 = this.searchEngine.searchByAlteration(this.signalIndex, "Br V6");

        // query string: braf v600e
        // expected: 1 result, exact match
        assertEquals(
            "braf v600e query should return only 1 alteration",
            1,
            brafV600e.size()
        );
        assertEquals(
            "braf v600e query should be an exact match",
            SignalMatchType.EXACT,
            brafV600e.get(0).getMatchType()
        );

        // query string: Br V6
        // expected: 1 result, starts with
        assertEquals(
            "Br V6 query should return only 1 alteration",
            1,
            brV6.size()
        );
        assertEquals(
            "V600E should start with V6",
            SignalMatchType.STARTS_WITH,
            brV6.get(0).getMatchType()
        );
    }
}
