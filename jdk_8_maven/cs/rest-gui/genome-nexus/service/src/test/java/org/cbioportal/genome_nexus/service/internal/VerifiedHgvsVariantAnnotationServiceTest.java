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
import org.cbioportal.genome_nexus.model.VariantAnnotation;
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
public class VerifiedHgvsVariantAnnotationServiceTest
{
    // Tested Class
    private VerifiedHgvsVariantAnnotationService verifiedHgvsVariantAnnotationService;

    @Mock
    private HgvsVariantAnnotationService hgvsVariantAnnotationService;

    // test cases
    public List<VariantTestCase> hgvsSubstitutions = null;
    public List<VariantTestCase> hgvsDeletions = null;
    public List<VariantTestCase> hgvsInsertions = null;
    public List<VariantTestCase> hgvsInsertionDeletions = null;
    public List<VariantTestCase> hgvsInversions = null; // test results of an unsupported format query
    // other types (duplications, no-change, methylation, specified alleles) are not handled by genome nexus and are not tested
    // single query stub maps
    public Map<String, Boolean> variantToVepSuccessfullyAnnotated = new HashMap<String, Boolean>();
    public Map<String, String> variantToVepAlleleString = new HashMap<String, String>();
    // argument list stubs
    public String mockIsoformOverrideSource = "mockIsoformOverrideSource";
    public Map<String, String> mockTokenMap = new HashMap<String, String>();
    public List<String> mockFields = new ArrayList<String>();

    class VariantTestCase {
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
        verifiedHgvsVariantAnnotationService = new VerifiedHgvsVariantAnnotationService(hgvsVariantAnnotationService);
        initializeTestCaseLists();
        setUpServiceStubs(); // these are based on the test case lists
    }

    private void initializeTestCaseLists()
    {
        if (hgvsSubstitutions == null) {
            hgvsSubstitutions = new ArrayList<VariantTestCase>();
            hgvsSubstitutions.add(new VariantTestCase("5:g.138163256C>T", true, "C/T", "valid substitution"));
            hgvsSubstitutions.add(new VariantTestCase("5:g.138163256A>T", false, null, "discrepant RefAllele"));
            hgvsSubstitutions.add(new VariantTestCase("5:g.138163256>T", false, null, "missing RefAllele"));
            hgvsDeletions = new ArrayList<VariantTestCase>();
            hgvsDeletions.add(new VariantTestCase("5:g.138163256delC", true, "C/-", "1nt deletion with RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163256delA", false, null, "1nt deletion with discrepant RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163256del", true, "C/-", "1nt deletion missing RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163255_138163256delTC", true, "TC/-", "2nt deletion with RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163255_138163256delCC", false, null, "2nt deletion with discrepant RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163255_138163256delCCCC", false, null, "2nt deletion with invalid RefAllele"));
            hgvsDeletions.add(new VariantTestCase("5:g.138163255_138163256del", true, "TC/-", "2nt deletion missing RefAllele"));
            hgvsInsertions = new ArrayList<VariantTestCase>();
            hgvsInsertions.add(new VariantTestCase("5:g.138163255_138163256insT", true, "-/T", "1nt insertion"));
            hgvsInsertions.add(new VariantTestCase("5:g.138163255_138163256insTT", true, "-/TT", "2nt insertion"));
            hgvsInsertions.add(new VariantTestCase("5:g.138163255_138163256ins", false, null, "insertion missing TumorSeqAllele"));
            hgvsInsertionDeletions = new ArrayList<VariantTestCase>();
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delinsT", true, "C/T", "1nt deletion without RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delCinsT", true, "C/T", "1nt deletion with RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delAinsT", false, null, "1nt deletion with discrepant RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delinsC", false, null, "1nt deletion without RefAllele, 1nt insertion no change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delinsTT", true, "C/TT", "1nt deletion without RefAllele, 2nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delCinsTT", true, "C/TT", "1nt deletion with RefAllele, 2nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163256delAinsTT", false, null, "1nt deletion with discrepant RefAllele, 2nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delinsG", true, "TC/G", "2nt deletion without RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delTCinsA", true, "TC/A", "2nt deletion with RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delTAinsG", false, null, "2nt deletion with discrepant RefAllele, 1nt insertion"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delTCinsTC", false, null, "2nt deletion with RefAllele, 2nt insertion no change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delTCinsTT", true, "C/T", "2nt deletion with RefAllele, 2nt insertion, partial change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delTCinsGG", true, "TC/GG", "2nt deletion with RefAllele, 2nt insertion, full change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delCCinsTC", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion no change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delCCinsTT", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion, partial change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delCCinsGG", false, null, "2nt deletion with discrepant RefAllele, 2nt insertion, full change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delinsTC", false, null, "2nt deletion without RefAllele, 2nt insertion no change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delinsTT", true, "C/T", "2nt deletion without RefAllele, 2nt insertion, partial change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delinsGG", true, "TC/GG", "2nt deletion without RefAllele, 2nt insertion, full change"));
            hgvsInsertionDeletions.add(new VariantTestCase("5:g.138163255_138163256delCCCCinsTT", false, null, "2nt deletion with invalid RefAllele, 2nt insertion"));
            hgvsInversions = new ArrayList<VariantTestCase>();
            hgvsInversions.add(new VariantTestCase("5:g.138163255_138163256inv", true, "TC/GA", "inversions not supported - but will run as passthrough"));
            hgvsInversions.add(new VariantTestCase("5:g.138163255_138163256invTC", false, null, "inversion format does not allow specification of RefAllele"));
        }
    }

    private void setUpQueryToStubMaps(String variantQuery, boolean successfullyAnnotated, String alleleString)
    {
        variantToVepSuccessfullyAnnotated.put(variantQuery, successfullyAnnotated);
        variantToVepAlleleString.put(variantQuery, alleleString);
    }

    private void setUpQueryToStubMaps()
    {
        // VEP responses for these test cases are extracted from queries to http://grch37.rest.ensembl.org/vep/human/hgvs/<variant>
        // these contain only the elements neccessary for testing the business logic in VerifiedGenomicLocationAnnotationService
        setUpQueryToStubMaps("5:g.138163256C>T", true, "C/T");
        setUpQueryToStubMaps("5:g.138163256A>T", false, null);
        setUpQueryToStubMaps("5:g.138163256>T", false, null);
        setUpQueryToStubMaps("5:g.138163256delC", true, "C/-");
        setUpQueryToStubMaps("5:g.138163256delA", true, "C/-");
        setUpQueryToStubMaps("5:g.138163256del", true, "C/-");
        setUpQueryToStubMaps("5:g.138163255_138163256delTC", true, "TC/-");
        setUpQueryToStubMaps("5:g.138163255_138163256delCC", true, "TC/-");
        setUpQueryToStubMaps("5:g.138163255_138163256delCCCC", true, "TC/-");
        setUpQueryToStubMaps("5:g.138163255_138163256del", true, "TC/-");
        setUpQueryToStubMaps("5:g.138163255_138163256insT", true, "-/T");
        setUpQueryToStubMaps("5:g.138163255_138163256insTT", true, "-/TT");
        setUpQueryToStubMaps("5:g.138163255_138163256ins", false, null);
        setUpQueryToStubMaps("5:g.138163256delinsT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163256delCinsT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163256delAinsT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163256delinsC", false, null);
        setUpQueryToStubMaps("5:g.138163256delinsTT", true, "C/TT");
        setUpQueryToStubMaps("5:g.138163256delCinsTT", true, "C/TT");
        setUpQueryToStubMaps("5:g.138163256delAinsTT", true, "C/TT");
        setUpQueryToStubMaps("5:g.138163255_138163256delinsG", true, "TC/G");
        setUpQueryToStubMaps("5:g.138163255_138163256delTCinsA", true, "TC/A");
        setUpQueryToStubMaps("5:g.138163255_138163256delTAinsG", true, "TC/G");
        setUpQueryToStubMaps("5:g.138163255_138163256delTCinsTC", false, null);
        setUpQueryToStubMaps("5:g.138163255_138163256delTCinsTT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163255_138163256delTCinsGG", true, "TC/GG");
        setUpQueryToStubMaps("5:g.138163255_138163256delCCinsTC", false, null);
        setUpQueryToStubMaps("5:g.138163255_138163256delCCinsTT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163255_138163256delCCinsGG", true, "TC/GG");
        setUpQueryToStubMaps("5:g.138163255_138163256delinsTC", false, null);
        setUpQueryToStubMaps("5:g.138163255_138163256delinsTT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163255_138163256delinsGG", true, "TC/GG");
        setUpQueryToStubMaps("5:g.138163255_138163256delCCCCinsTT", true, "C/T");
        setUpQueryToStubMaps("5:g.138163255_138163256inv", true, "TC/GA");
        setUpQueryToStubMaps("5:g.138163255_138163256invTC", false, null);
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

    private void stubHgvsVariantAnnotationServiceMethodsForType(List<VariantTestCase> variantTestCaseList)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, TestCaseInsufficentlyModeledException
    {
        // create stubs for single variant queries, and begin building lists too
        ArrayList<String> queryStringList = new ArrayList<String>();
        ArrayList<VariantAnnotation> responseList = new ArrayList<VariantAnnotation>();
        for (VariantTestCase testCase : variantTestCaseList) {
            queryStringList.add(testCase.originalVariantQuery);
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
            Mockito.when(hgvsVariantAnnotationService.getAnnotation(testCase.originalVariantQuery)).thenReturn(response);
            Mockito.when(hgvsVariantAnnotationService.getAnnotation(testCase.originalVariantQuery, mockIsoformOverrideSource, mockTokenMap, mockFields)).thenReturn(response);
        }
        // create stubs for multi variant queries from built lists .. after jittering order
        // order of response not guaranteed to match order of query - business logic should handle this properly
        VariantAnnotation movedItem = responseList.remove(0);
        responseList.add(movedItem); // first element is now last
        Mockito.when(hgvsVariantAnnotationService.getAnnotations(queryStringList)).thenReturn(responseList);
        Mockito.when(hgvsVariantAnnotationService.getAnnotations(queryStringList, mockIsoformOverrideSource, mockTokenMap, mockFields)).thenReturn(responseList);

    }

    private void setUpServiceStubs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException, TestCaseInsufficentlyModeledException
    {
        setUpQueryToStubMaps();
        stubHgvsVariantAnnotationServiceMethodsForType(hgvsSubstitutions);
        stubHgvsVariantAnnotationServiceMethodsForType(hgvsDeletions);
        stubHgvsVariantAnnotationServiceMethodsForType(hgvsInsertions);
        stubHgvsVariantAnnotationServiceMethodsForType(hgvsInsertionDeletions);
        stubHgvsVariantAnnotationServiceMethodsForType(hgvsInversions);
    }

    private void runTestSetByVariantString(List<VariantTestCase> variantTestCaseList, boolean supplyOtherParameters)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        for (VariantTestCase testCase : variantTestCaseList) {
            VariantAnnotation testResponse = null;
            if (supplyOtherParameters) {
                testResponse = verifiedHgvsVariantAnnotationService.getAnnotation(testCase.originalVariantQuery, mockIsoformOverrideSource, mockTokenMap, mockFields);
            } else {
                testResponse = verifiedHgvsVariantAnnotationService.getAnnotation(testCase.originalVariantQuery);
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

    private void runTestSetByVariantList(List<VariantTestCase> variantTestCaseList, boolean supplyOtherParameters)
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        // make list of variant query strings and submit
        List<String> queryHgvsList = variantTestCaseList.stream().map(t -> t.originalVariantQuery).collect(Collectors.toList());
        List<VariantAnnotation> variantResponse = null;
        if (supplyOtherParameters) {
            variantResponse = verifiedHgvsVariantAnnotationService.getAnnotations(queryHgvsList, mockIsoformOverrideSource, mockTokenMap, mockFields);
        } else {
            variantResponse = verifiedHgvsVariantAnnotationService.getAnnotations(queryHgvsList);
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
        runTestSetByVariantString(hgvsSubstitutions, false);
    }

    @Test
    public void getAnnotationForDeletionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsDeletions, false);
    }

    @Test
    public void getAnnotationForInsertionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInsertions, false);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInsertionDeletions, false);
    }

    @Test
    public void getAnnotationForInversionsByVariantString()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInversions, false);
    }

    // Tests of the getAnnotation(String, overrideSource, tokenMap, fields) function

    @Test
    public void getAnnotationForSubstitutionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsSubstitutions, true);
    }

    @Test
    public void getAnnotationForDeletionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsDeletions, true);
    }

    @Test
    public void getAnnotationForInsertionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInsertions, true);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInsertionDeletions, true);
    }

    @Test
    public void getAnnotationForInversionsByVariantStringWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantString(hgvsInversions, true);
    }

    // Tests of the getAnnotation(List<String>) function

    @Test
    public void getAnnotationForSubstitutionsByVariantLists()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsSubstitutions, false);
    }

    @Test
    public void getAnnotationForDeletionsByVariantLists()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsDeletions, false);
    }

    @Test
    public void getAnnotationForInsertionsByVariantLists()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInsertions, false);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantLists()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInsertionDeletions, false);
    }

    @Test
    public void getAnnotationForInversionsByVariantLists()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInversions, false);
    }

    // Tests of the getAnnotations(List<String>, overrideSource, tokenMap, fields) function

    @Test
    public void getAnnotationForSubstitutionsByVariantListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsSubstitutions, true);
    }

    @Test
    public void getAnnotationForDeletionsByVariantListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsDeletions, true);
    }

    @Test
    public void getAnnotationForInsertionsByVariantListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInsertions, true);
    }

    @Test
    public void getAnnotationForInsertionDeletionsByVariantListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInsertionDeletions, true);
    }

    @Test
    public void getAnnotationForInversionsByVariantListWithArgs()
        throws VariantAnnotationWebServiceException, VariantAnnotationNotFoundException
    {
        runTestSetByVariantList(hgvsInversions, true);
    }

}
