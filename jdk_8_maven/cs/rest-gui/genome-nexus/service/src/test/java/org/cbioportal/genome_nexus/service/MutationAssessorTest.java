package org.cbioportal.genome_nexus.service;

import com.mongodb.BasicDBObject;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.cached.CachedMutationAssessorFetcher;
import org.cbioportal.genome_nexus.service.config.ExternalResourceObjectMapper;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.service.internal.MutationAssessorServiceImpl;
import org.cbioportal.genome_nexus.service.remote.MutationAssessorDataFetcher;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;
import java.net.URL;

import static org.junit.Assert.*;

// TODO need to convert this into a proper integration test, disabled for now due to direct access to a real web API.
public class MutationAssessorTest
{
    // for debugging
    private static Logger log = Logger.getLogger(String.valueOf(MutationAssessorTest.class));

    // normally taken from application.properties file
    private String url =
        "http://mutationassessor.org/r3/?cm=var&var=VARIANT&frm=json&fts=input,rgaa,rgvt,var,gene,F_impact,F_score,chr,rs_pos";

    //@Test
    public void testStringInputs()
        throws IOException, ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        ExternalResourceTransformer<MutationAssessor> transformer =
            new ExternalResourceTransformer<>(new ExternalResourceObjectMapper());
        CachedMutationAssessorFetcher fetcher = new CachedMutationAssessorFetcher(
            transformer,
            null,
            new MutationAssessorDataFetcher(transformer, url));
        MutationAssessorServiceImpl service = new MutationAssessorServiceImpl(fetcher, null);

        String urlString1 = url.replace("VARIANT", "7,140453136,A,T");
        MutationAssessor mutationObj1 = service.getMutationAssessorByMutationAssessorVariant("7,140453136,A,T"); // 7:g.140453136A>T
        MutationAssessor mutationObj2 = transformer.transform(
            new BasicDBObject(/*getReturnString(urlString1)*/), MutationAssessor.class).get(0);

        // getVariant() not tested because variants set manually
        assertEquals(mutationObj1.getHugoSymbol(), mutationObj2.getHugoSymbol());
        assertEquals(mutationObj1.getFunctionalImpact(), mutationObj2.getFunctionalImpact());
        assertEquals(mutationObj1.getFunctionalImpactScore(), mutationObj2.getFunctionalImpactScore(), 0);


        String urlString2 = url.replace("VARIANT", "12,25398285,C,A");
        MutationAssessor mutationObj21 = service.getMutationAssessorByMutationAssessorVariant("12,25398285,C,A"); // 12:g.25398285C>A
        MutationAssessor mutationObj22 = transformer.transform(
            new BasicDBObject(/*getReturnString(urlString2)*/), MutationAssessor.class).get(0);

        assertEquals(mutationObj21.getHugoSymbol(), mutationObj22.getHugoSymbol());
        assertEquals(mutationObj21.getFunctionalImpact(), mutationObj22.getFunctionalImpact());
        assertEquals(mutationObj21.getFunctionalImpactScore(), mutationObj22.getFunctionalImpactScore(), 0);

    }

    //@Test
    public void testJunk()
        throws IOException, ResourceMappingException, MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        ExternalResourceTransformer<MutationAssessor> transformer =
            new ExternalResourceTransformer<>(new ExternalResourceObjectMapper());
        CachedMutationAssessorFetcher fetcher = new CachedMutationAssessorFetcher(
            transformer,
            null,
            new MutationAssessorDataFetcher(transformer, url));
        MutationAssessorServiceImpl service = new MutationAssessorServiceImpl(fetcher, null);

        String urlString = url.replace("VARIANT", "junkInput");
        MutationAssessor mutationObj1 = service.getMutationAssessorByMutationAssessorVariant("junkInput");
        MutationAssessor mutationObj2 = transformer.transform(
            new BasicDBObject(/*getReturnString(urlString)*/), MutationAssessor.class).get(0);

        // getVariant() not tested because variants set manually
        assertEquals(mutationObj1.getHugoSymbol(), mutationObj2.getHugoSymbol());
        assertEquals(mutationObj1.getFunctionalImpact(), mutationObj2.getFunctionalImpact());
        assertEquals(mutationObj1.getFunctionalImpactScore(), mutationObj2.getFunctionalImpactScore(), 0);
    }

    private String getReturnString(String urlString) throws IOException {
        String output = "";
        URL url = new URL(urlString);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "UTF-8")))
        {
            for (String line; (line = reader.readLine()) != null;)
            {
                output += line;
            }
        }
        return output;
    }

}
