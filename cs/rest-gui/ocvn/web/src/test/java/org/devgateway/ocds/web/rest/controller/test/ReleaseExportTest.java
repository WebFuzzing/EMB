package org.devgateway.ocds.web.rest.controller.test;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import java.io.File;
import org.apache.log4j.Logger;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.OcdsSchemaValidatorService;
import org.devgateway.ocds.persistence.mongo.spring.json.JsonImport;
import org.devgateway.ocds.persistence.mongo.spring.json.ReleaseJsonImport;
import org.devgateway.toolkit.web.AbstractWebTest;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author idobre
 * @since 6/1/16
 */
public class ReleaseExportTest extends AbstractWebTest {
    private static Logger logger = Logger.getLogger(ReleaseExportTest.class);

    @Autowired
    private ReleaseRepository releaseRepository;

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private OcdsSchemaValidatorService ocdsSchemaValidator;

    @Autowired
    private OcdsSchemaValidatorService ocdsSchemaAllRequiredValidator;

    @Before
    public final void setUp() throws Exception {
        // just be sure that the release collection is empty
        releaseRepository.deleteAll();

        // use a web app context setup because we want to do an integration test that involves loading of
        // Controllers, Services and Repositories from the Spring configuration.
        // for a more focused unit tests we can use:
        // this.mockMvc = MockMvcBuilders.standaloneSetup(new OcdsController()).build();
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();

    }

    @After
    public final void tearDown() {
        // be sure to clean up the release collection
        releaseRepository.deleteAll();
    }

    @Test
    public void testReleaseExportIsValid() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("json/award-release-test.json").getFile());

        final JsonImport releaseJsonImport = new ReleaseJsonImport(releaseRepository, file, false);
        final Release release = (Release) releaseJsonImport.importObject();

        final MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/ocds/release/ocid/" + release.getOcid()).
                        accept(MediaType.APPLICATION_JSON_UTF8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).
                andReturn();
        final String content = result.getResponse().getContentAsString();

        final JsonNode jsonNodeResponse = JsonLoader.fromString(content);
        final OcdsSchemaValidatorService.ProcessingReportWithNode processingReport =
                ocdsSchemaValidator.validate(jsonNodeResponse);

        if (!processingReport.getReport().isSuccess()) {
            for (ProcessingMessage processingMessage : processingReport.getReport()) {
                logger.error(">>> processingMessage: \n" + processingMessage);
            }
        }
        Assert.assertEquals("Is the release valid?", true, processingReport.getReport().isSuccess());
    }

    @Test
    public void testWholeStandardIsImplemented() throws Exception {
        final ClassLoader classLoader = getClass().getClassLoader();
        final File file = new File(classLoader.getResource("json/full-release.json").getFile());

        final JsonImport releaseJsonImport = new ReleaseJsonImport(releaseRepository, file, false);
        final Release release = (Release) releaseJsonImport.importObject();

        final MvcResult result = this.mockMvc.perform(
                MockMvcRequestBuilders.get("/api/ocds/release/ocid/" + release.getOcid()).
                        accept(MediaType.APPLICATION_JSON_UTF8)).
                andExpect(status().isOk()).
                andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8)).
                andReturn();
        final String content = result.getResponse().getContentAsString();

        final JsonNode jsonNodeResponse = JsonLoader.fromString(content);
        final OcdsSchemaValidatorService.ProcessingReportWithNode processingReport =
                ocdsSchemaAllRequiredValidator.validate(jsonNodeResponse);

        if (!processingReport.getReport().isSuccess()) {
            for (ProcessingMessage processingMessage : processingReport.getReport()) {
                logger.error(">>> processingMessage: \n" + processingMessage);
            }
        }
        Assert.assertEquals("Do we implement the entire standard (with all fields required)?",
                true, processingReport.getReport().isSuccess());
    }
}
