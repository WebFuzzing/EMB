/**
 *
 */
package org.devgateway.ocds.persistence.mongo.spring;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.github.fge.jsonpatch.mergepatch.JsonMergePatch;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ListReportProvider;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.main.JsonSchema;
import com.github.fge.jsonschema.main.JsonSchemaFactory;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author mpostelnicu
 */
public class OcdsSchemaValidatorService {
    private final Logger logger = LoggerFactory.getLogger(OcdsSchemaValidatorService.class);
    private String schemaLocation = null;
    private JsonSchema schema;

    public static final String OCDS_SCHEMA_LOCATION = "/release-schema.json";
    public static final String OCDS_SCHEMA_ALL_REQUIRED = "/release-schema-all-required.json";
    public static final String OCDS_LOCATION_PATCH_LOCATION = "/location_patch_schema.json";
    public static final String OCDS_BID_EXTENSION = "/ocds_bid_extension.mergepatch.json";

    private ObjectMapper jacksonObjectMapper;
    private String[] patchResourceNames;
    private JsonNode ocdsSchemaNode;

    public class ProcessingReportWithNode {
        private ProcessingReport report;
        private String failedValidationObjString;

        public ProcessingReportWithNode(final ProcessingReport report, final String failedValidationObjString) {
            this.report = report;
            this.failedValidationObjString = failedValidationObjString;
        }

        public ProcessingReport getReport() {
            return report;
        }

        public String getFailedValidationObjString() {
            return failedValidationObjString;
        }

        @Override
        public String toString() {
            return report.toString() + " FAILED OBJECT OCID: " + failedValidationObjString;
        }
    }

    public OcdsSchemaValidatorService(final ObjectMapper jacksonObjectMapper) {
        this.jacksonObjectMapper = jacksonObjectMapper;
    }

    public OcdsSchemaValidatorService(final ObjectMapper jacksonObjectMapper, String schemaLocation) {
        this.jacksonObjectMapper = jacksonObjectMapper;
        this.schemaLocation = schemaLocation;
    }

    public OcdsSchemaValidatorService withJsonPatches(final String... patchResourceNames) {
        this.patchResourceNames = patchResourceNames;
        return this;
    }

    /**
     * Intializes the JSON schema validator plus the provided patches
     */
    public void init() {
        try {

            ocdsSchemaNode = JsonLoader.fromResource(schemaLocation == null ? OCDS_SCHEMA_LOCATION : schemaLocation);

            if (patchResourceNames != null && patchResourceNames.length > 0) {
                for (int i = 0; i < patchResourceNames.length; i++) {
                    JsonNode node = JsonLoader.fromResource(patchResourceNames[i]);
                    if (patchResourceNames[i].contains("mergepatch")) {
                        JsonMergePatch patch = JsonMergePatch.fromJson(node);
                        ocdsSchemaNode = patch.apply(ocdsSchemaNode);
                    } else {
                        JsonPatch patch = JsonPatch.fromJson(node);
                        ocdsSchemaNode = patch.apply(ocdsSchemaNode);
                    }
                }
            }

            logger.debug(jacksonObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ocdsSchemaNode));

            schema = JsonSchemaFactory.newBuilder()
                    .setReportProvider(new ListReportProvider(LogLevel.ERROR, LogLevel.FATAL)).freeze()
                    .getJsonSchema(ocdsSchemaNode);

        } catch (ProcessingException | IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } catch (JsonPatchException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates the incoming {@link JsonNode} against OCDS schema
     *
     * @param jsonNode
     * @return
     */
    public ProcessingReportWithNode validate(final JsonNode jsonNode) {
        try {
            ProcessingReport processingReport = schema.validate(jsonNode);
            ProcessingReportWithNode processingReportWithNode = null;
            try {
                processingReportWithNode = new ProcessingReportWithNode(processingReport, processingReport.isSuccess()
                        ? null : jacksonObjectMapper.writerWithDefaultPrettyPrinter().
                        writeValueAsString(jsonNode.get("ocid")));
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                e.printStackTrace();
            }
            return processingReportWithNode;
        } catch (ProcessingException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public ProcessingReportWithNode validate(final Object object) {
        JsonNode value = jacksonObjectMapper.convertValue(object, JsonNode.class);
        return validate(value);
    }

    public <S> List<ProcessingReportWithNode> validateAll(final Collection<S> values) {
        return values.stream().map(this::validate).collect(Collectors.toList());
    }

    public JsonNode findValueInOcdsSchema(final String fieldName) {
        return ocdsSchemaNode.findValue(fieldName);
    }

}
