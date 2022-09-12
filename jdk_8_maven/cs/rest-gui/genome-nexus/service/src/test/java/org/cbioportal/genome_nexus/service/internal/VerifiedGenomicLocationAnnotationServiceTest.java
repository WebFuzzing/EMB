/*
 * Copyright (c) 2021 Memorial Sloan-Kettering Cancer Center.
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
 * This file is part of cBioPortal.
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

package org.cbioportal.genome_nexus.service.internal;

import java.util.*;
import java.util.stream.Collectors;
import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.model.GenomicLocation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.GenomicLocationAnnotationService;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.Silent.class)
public class VerifiedGenomicLocationAnnotationServiceTest
{
    // Tested Class
    private VerifiedGenomicLocationAnnotationServiceImpl verifiedGenomicLocationAnnotationServiceImpl;

    // Use the production NotationConverter utility
    private NotationConverter notationConverter = new NotationConverter();

    @Mock
    private GenomicLocationAnnotationService glVariantAnnotationService;

    // case lists
    public List<VariantTestCase> glSubstitutions = null;
    public List<VariantTestCase> glDeletions = null;
    public List<VariantTestCase> glInsertions = null;
    public List<VariantTestCase> glInsertionDeletions = null;
    // single query stub maps
    public Map<String, Boolean> variantToVepSuccessfullyAnnotated = new HashMap<String, Boolean>();
    public Map<String, String> variantToVepAlleleString = new HashMap<String, String>();
    // argument list stubs
    public String mockIsoformOverrideSource = "mockIsoformOverrideSource";
    public Map<String, String> mockTokenMap = new HashMap<String, String>();
    public List<String> mockFields = new ArrayList<String>();

    class VariantTestCase
    {
        public String originalVariantQuery;
        public boolean expectedGnSuccessfullyAnnotated;
        public String expectedGnAlleleString;
        public String description;
        public VariantTestCase(
                String originalVariantQuery,
                boolean expectedGnSuccessfullyAnnotated,
                String expectedGnAlleleString,
                String description) {
            this.originalVariantQuery =  originalVariantQuery;
            this.expectedGnSuccessfullyAnnotated = expectedGnSuccessfullyAnnotated;
            this.expectedGnAlleleString = expectedGnAlleleString;
            this.description = description;
        }
    }

    class TestCaseInsufficentlyModeledException extends Exception
    {
        public TestCaseInsufficentlyModeledException(String msg) {
            super(msg);
        }
    }

    @Before
    public void setUp()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, TestCaseInsufficentlyModeledException
    {
        verifiedGenomicLocationAnnotationServiceImpl = new VerifiedGenomicLocationAnnotationServiceImpl(glVariantAnnotationService, notationConverter);
        initializeTestCaseLists();
        setUpServiceStubs(); // these are based on the test case lists
    }

    private void initializeTestCaseLists()
    {
        if (glSubstitutions == null) {
            glSubstitutions = new ArrayList<VariantTestCase>();
            glSubstitutions.add(new VariantTestCase("5,138163256,138163256,C,T", true, "C/T", "valid substitution"));
            glSubstitutions.add(new VariantTestCase("5,138163256,138163256,A,T", false, null, "discrepant RefAllele"));
            glDeletions = new ArrayList<VariantTestCase>();
            glDeletions.add(new VariantTestCase("5,138163256,138163256,C,-", true, "C/-", "1nt deletion with RefAllele"));
            glDeletions.add(new VariantTestCase("5,138163256,138163256,A,-", false, null, "1nt deletion with discrepant RefAllele"));
            glDeletions.add(new VariantTestCase("5,138163255,138163256,TC,-", true, "TC/-", "2nt deletion with RefAllele"));
            glDeletions.add(new VariantTestCase("5,138163255,138163256,CC,-", false, null, "2nt deletion with discrepant RefAllele"));
            glDeletions.add(new VariantTestCase("5,138163255,138163256,CCCC,-", false, null, "2nt deletion with invalid RefAllele"));
            glInsertions = new ArrayList<VariantTestCase>();
            glInsertions.add(new VariantTestCase("5,138163255,138163256,-,T", true, "-/T", "1nt insertion"));
            glInsertions.add(new VariantTestCase("5,138163255,138163256,-,TT", true, "-/TT", "2nt insertion"));
            glInsertions.add(new VariantTestCase("5,138163255,138163256,-,-", true, "-/-", "insertion missing TumorSeqAllele")); // note : different result than ensembl
            glInsertionDeletions = new ArrayList<VariantTestCase>();
            glInsertionDeletions.add(new VariantTestCase("5,138163256,138163256,C,T", true, "C/T", "1nt deletion with RefAllele, 1nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163256,138163256,A,T", false, null, "1nt deletion with discrepant RefAllele, 1nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163256,138163256,C,TT", true, "C/TT", "1nt deletion with RefAllele, 2nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163256,138163256,A,TT", false, null, "1nt deletion with discrepant RefAllele, 2nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,TC,A", true, "TC/A", "2nt deletion with RefAllele, 1nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,TA,G", false, null, "2nt deletion with discrepant RefAllele, 1nt insertion"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,TC,TC", true, "TC/TC", "2nt deletion with RefAllele, 2nt insertion no change")); // note : different result than ensembl
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,TC,TT", true, "C/T", "2nt deletion with RefAllele, 2nt insertion, partial change"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,TC,GG", true, "TC/GG", "2nt deletion with RefAllele, 2nt insertion, full change"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,CC,TC", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion no change")); // note : different result than ensembl
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,CC,TT", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion, partial change"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,CC,GG", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion, full change"));
            glInsertionDeletions.add(new VariantTestCase("5,138163255,138163256,CCCC,TT", false, null, "2nt deletion with invalid RefAllele, 2nt insertion"));
        }
    }

    private void setUpQueryToStubMaps(String variantQuery, boolean successfullyAnnotated, String alleleString)
    {
        variantToVepSuccessfullyAnnotated.put(variantQuery, successfullyAnnotated);
        variantToVepAlleleString.put(variantQuery, alleleString);
    }

    private void setUpQueryToStubMaps()
    {
        // VEP responses for these test cases were extracted from queries to http://genie.genomenexus.org/annotation/genomic/<variant>
        // these contain only the elements neccessary for testing the business logic in VerifiedGenomicLocationAnnotationService
        setUpQueryToStubMaps("5,138163256,138163256,C,T", true, "C/T");
        setUpQueryToStubMaps("5,138163256,138163256,A,T", false, null);
        setUpQueryToStubMaps("5,138163256,138163256,C,-", true, "C/-");
        setUpQueryToStubMaps("5,138163256,138163256,A,-", true, "C/-");
        setUpQueryToStubMaps("5,138163255,138163256,TC,-", true, "TC/-");
        setUpQueryToStubMaps("5,138163255,138163256,CC,-", true, "TC/-");
        setUpQueryToStubMaps("5,138163255,138163256,CCCC,-", true, "TC/-");
        setUpQueryToStubMaps("5,138163255,138163256,-,T", true, "-/T");
        setUpQueryToStubMaps("5,138163255,138163256,-,TT", true, "-/TT");
        setUpQueryToStubMaps("5,138163255,138163256,-,-", true, "-/-");
        setUpQueryToStubMaps("5,138163256,138163256,C,TT", true, "C/TT");
        setUpQueryToStubMaps("5,138163256,138163256,A,TT", true, "C/TT");
        setUpQueryToStubMaps("5,138163255,138163256,TC,A", true, "TC/A");
        setUpQueryToStubMaps("5,138163255,138163256,TA,G", true, "TC/G");
        setUpQueryToStubMaps("5,138163255,138163256,TC,TC", true, "TC/TC");
        setUpQueryToStubMaps("5,138163255,138163256,TC,TT", true, "C/T");
        setUpQueryToStubMaps("5,138163255,138163256,TC,GG", true, "TC/GG");
        setUpQueryToStubMaps("5,138163255,138163256,CC,TC", true, "TC/TC");
        setUpQueryToStubMaps("5,138163255,138163256,CC,TT", true, "TC/TT");
        setUpQueryToStubMaps("5,138163255,138163256,CC,GG", true, "TC/GG");
        setUpQueryToStubMaps("5,138163255,138163256,CCCC,TT", true, "TC/TT");
    }

    private VariantAnnotation stubAnnotation(String originalVariantQuery, String variant, boolean successfullyAnnotated, String alleleString)
    {
        VariantAnnotation stub = new VariantAnnotation();
        stub.setOriginalVariantQuery(originalVariantQuery);
        stub.setVariant(variant);
        stub.setAlleleString(alleleString);
        stub.setSuccessfullyAnnotated(successfullyAnnotated);
        return stub;
    }

    private void stubGenomicLocationAnnotationServiceMethodsForType(List<VariantTestCase> variantTestCaseList)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, TestCaseInsufficentlyModeledException
    {
        // create stubs for single variant queries, and begin building lists too
        ArrayList<String> queryStringList = new ArrayList<String>();
        ArrayList<GenomicLocation> queryGenomicLocationList = new ArrayList<GenomicLocation>();
        ArrayList<VariantAnnotation> responseList = new ArrayList<VariantAnnotation>();
        for (VariantTestCase testCase : variantTestCaseList) {
            GenomicLocation genomicLocation = notationConverter.parseGenomicLocation(testCase.originalVariantQuery);
            queryStringList.add(testCase.originalVariantQuery);
            queryGenomicLocationList.add(genomicLocation);
            if (!variantToVepSuccessfullyAnnotated.containsKey(testCase.originalVariantQuery)) {
                throw new TestCaseInsufficentlyModeledException("No Vep successfully_annotated stub defined for original_variant_query : " + testCase.originalVariantQuery);
            }
            Boolean successfullyAnnotatedStub = variantToVepSuccessfullyAnnotated.get(testCase.originalVariantQuery);
            if (successfullyAnnotatedStub == null) {
                throw new TestCaseInsufficentlyModeledException("No Vep successfully_annotated stub defined for original_variant_query : " + testCase.originalVariantQuery);
            }
            if (!variantToVepAlleleString.containsKey(testCase.originalVariantQuery)) {
                throw new TestCaseInsufficentlyModeledException("No Vep allele_string stub defined for original_variant_query : " + testCase.originalVariantQuery);
            }
            String alleleStringStub = variantToVepAlleleString.get(testCase.originalVariantQuery); // null is an acceptable stub value for allele_string
            VariantAnnotation response = stubAnnotation(
                    testCase.originalVariantQuery,
                    testCase.originalVariantQuery,
                    successfullyAnnotatedStub,
                    alleleStringStub);
            responseList.add(response);
            //response.setAnnotationJSON("{ \"originalVariantQuery\" : \"" + testCase.originalVariantQuery + "\", \"successfullyAnnotated\" : " + successfullyAnnotatedStub + ", \"allele\" : \"" + alleleStringStub + "\" }");
            Mockito.when(glVariantAnnotationService.getAnnotation(testCase.originalVariantQuery)).thenReturn(response);
            Mockito.when(glVariantAnnotationService.getAnnotation(testCase.originalVariantQuery, mockIsoformOverrideSource, mockTokenMap, mockFields)).thenReturn(response);
            Mockito.when(glVariantAnnotationService.getAnnotation(genomicLocation)).thenReturn(response);
        }
        // create stubs for multi variant queries from built lists .. after jittering order
        // order of response not guaranteed to match order of query - business logic should handle this properly
        VariantAnnotation movedItem = responseList.remove(0);
        responseList.add(movedItem); // first element is now last
        Mockito.when(glVariantAnnotationService.getAnnotations(queryGenomicLocationList)).thenReturn(responseList);
        Mockito.when(glVariantAnnotationService.getAnnotations(queryGenomicLocationList, mockIsoformOverrideSource, mockTokenMap, mockFields)).thenReturn(responseList);
    }

    private void setUpServiceStubs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, TestCaseInsufficentlyModeledException
    {
        setUpQueryToStubMaps();
        stubGenomicLocationAnnotationServiceMethodsForType(glSubstitutions);
        stubGenomicLocationAnnotationServiceMethodsForType(glDeletions);
        stubGenomicLocationAnnotationServiceMethodsForType(glInsertions);
        stubGenomicLocationAnnotationServiceMethodsForType(glInsertionDeletions);
    }

    private void runTestSetByVariantString(List<VariantTestCase> variantTestCaseList, boolean supplyOtherParameters)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        for (VariantTestCase testCase : variantTestCaseList) {
            VariantAnnotation testResponse = null;
            if (supplyOtherParameters) {
                testResponse = verifiedGenomicLocationAnnotationServiceImpl.getAnnotation(testCase.originalVariantQuery, mockIsoformOverrideSource, mockTokenMap, mockFields);
            } else {
                testResponse = verifiedGenomicLocationAnnotationServiceImpl.getAnnotation(testCase.originalVariantQuery);
            }
            Assert.assertEquals(testCase.originalVariantQuery + " : response query field does not match request query string", testCase.originalVariantQuery, testResponse.getOriginalVariantQuery());
            if (testCase.expectedGnSuccessfullyAnnotated) {
                Assert.assertTrue(testCase.originalVariantQuery + " : expected successful annotation", testResponse.isSuccessfullyAnnotated());
            } else {
                Assert.assertFalse(testCase.originalVariantQuery + " : expected failed annotation", testResponse.isSuccessfullyAnnotated());
            }
            if (testResponse.isSuccessfullyAnnotated()) {
                Assert.assertEquals(testCase.originalVariantQuery + " : Variant Allele comparison", testCase.expectedGnAlleleString, testResponse.getAlleleString());
            }
        }
    }

    private void runTestSetByGenomicLocation(List<VariantTestCase> variantTestCaseList)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        for (VariantTestCase testCase : variantTestCaseList) {
            GenomicLocation genomicLocation = notationConverter.parseGenomicLocation(testCase.originalVariantQuery);
            VariantAnnotation testResponse = null;
            testResponse = verifiedGenomicLocationAnnotationServiceImpl.getAnnotation(testCase.originalVariantQuery);
            Assert.assertEquals(genomicLocation.toString() + " : response query field does not match request query string", genomicLocation.toString(), testResponse.getOriginalVariantQuery());
            if (testCase.expectedGnSuccessfullyAnnotated) {
                Assert.assertTrue(genomicLocation.toString() + " : expected successful annotation", testResponse.isSuccessfullyAnnotated());
            } else {
                Assert.assertFalse(genomicLocation.toString() + " : expected failed annotation", testResponse.isSuccessfullyAnnotated());
            }
            if (testResponse.isSuccessfullyAnnotated()) {
                Assert.assertEquals(genomicLocation.toString() + " : Variant Allele comparison", testCase.expectedGnAlleleString, testResponse.getAlleleString());
            }
        }
    }

    private void runTestSetByGenomicLocationList(List<VariantTestCase> variantTestCaseList, boolean supplyOtherParameters)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        // make list of variant query strings and submit
        List<GenomicLocation> queryGenomicLocations = variantTestCaseList.stream().map(t -> notationConverter.parseGenomicLocation(t.originalVariantQuery)).collect(Collectors.toList());
        List<VariantAnnotation> variantResponse = null;
        if (supplyOtherParameters) {
            variantResponse = verifiedGenomicLocationAnnotationServiceImpl.getAnnotations(queryGenomicLocations, mockIsoformOverrideSource, mockTokenMap, mockFields);
        } else {
            variantResponse = verifiedGenomicLocationAnnotationServiceImpl.getAnnotations(queryGenomicLocations);
        }
        // check each element of response against expectations
        HashMap<String, VariantAnnotation> queryToResponse = new HashMap<String, VariantAnnotation>();
        for (VariantAnnotation responseElement : variantResponse) {
            if (queryToResponse.put(responseElement.getOriginalVariantQuery(), responseElement) != null) {
                Assert.fail("More than one response received for query string " + responseElement.getOriginalVariantQuery());
            }
        }
        for (VariantTestCase testCase : variantTestCaseList) {
            VariantAnnotation testResponse = queryToResponse.get(testCase.originalVariantQuery);
            if (testResponse == null) {
                Assert.fail("Response did not include record for query string " + testCase.originalVariantQuery);
            }
            if (testCase.expectedGnSuccessfullyAnnotated) {
                Assert.assertTrue(testCase.originalVariantQuery + " : expected successful annotation", testResponse.isSuccessfullyAnnotated());
            } else {
                Assert.assertFalse(testCase.originalVariantQuery + " : expected failed annotation", testResponse.isSuccessfullyAnnotated());
            }
            if (testResponse.isSuccessfullyAnnotated()) {
                Assert.assertEquals(testCase.originalVariantQuery + " : Variant Allele comparison", testCase.expectedGnAlleleString, testResponse.getAlleleString());
            }
        }
    }

    // Tests of the getAnnotation(String) function

    @Test
    public void getAnnotationForSubstitutionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glSubstitutions, false);
    }

    @Test
    public void getAnnotationForDeletionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glDeletions, false);
    }

    @Test
    public void getAnnotationForInsertionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glInsertions, false);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glInsertionDeletions, false);
    }

    // Tests of the getAnnotation(String, overrideSource, tokenMap, fields) function

    @Test
    public void getAnnotationForSubstitutionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glSubstitutions, true);
    }

    @Test
    public void getAnnotationForDeletionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glDeletions, true);
    }

    @Test
    public void getAnnotationForInsertionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glInsertions, true);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(glInsertionDeletions, true);
    }

    // Tests of the getAnnotation(GenomicLocation) function

    @Test
    public void getAnnotationForSubstitutionsByGenomicLocation()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocation(glSubstitutions);
    }

    @Test
    public void getAnnotationForDeletionsByGenomicLocation()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocation(glDeletions);
    }

    @Test
    public void getAnnotationForInsertionsByGenomicLocation()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocation(glInsertions);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByGenomicLocation()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocation(glInsertionDeletions);
    }

    // Tests of the getAnnotations(List<GenomicLocation>) function

    @Test
    public void getAnnotationsForSubstitutionsByGenomicLocationList()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glSubstitutions, false);
    }

    @Test
    public void getAnnotationsForDeletionsByGenomicLocationList()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glDeletions, false);
    }

    @Test
    public void getAnnotationsForInsertionsByGenomicLocationList()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glInsertions, false);
    }

    @Test
    public void getAnnotationsForInsertionDeletionsByGenomicLocationList()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glInsertionDeletions, false);
    }

    // Tests of the getAnnotations(List<GenomicLocation>, overrideSource, tokenMap, fields) function

    @Test
    public void getAnnotationsForSubstitutionsByGenomicLocationListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glSubstitutions, true);
    }

    @Test
    public void getAnnotationsForDeletionsByGenomicLocationListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glDeletions, true);
    }

    @Test
    public void getAnnotationsForInsertionsByGenomicLocationListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glInsertions, true);
    }

    @Test
    public void getAnnotationsForInsertionDeletionsByGenomicLocationListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByGenomicLocationList(glInsertionDeletions, true);
    }

}
