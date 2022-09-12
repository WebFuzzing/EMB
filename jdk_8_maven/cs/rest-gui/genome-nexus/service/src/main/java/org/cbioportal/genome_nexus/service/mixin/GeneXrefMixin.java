package org.cbioportal.genome_nexus.service.mixin;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeneXrefMixin
{
    @JsonProperty(value = "display_id", required = true)
    private String displayId;

    @JsonProperty(value = "primary_id", required = true)
    private String primaryId;

    @JsonProperty(value = "version", required = true)
    private String version;

    @JsonProperty(value = "description", required = true)
    private String description;

    @JsonProperty(value = "dbname", required = true)
    private String dbName;

    @JsonProperty(value = "synonyms", required = true)
    private List<String> synonyms;

    @JsonProperty(value = "info_text", required = true)
    private String infoText;

    @JsonProperty(value = "info_type", required = true)
    private String infoType;

    @JsonProperty(value = "db_display_name", required = true)
    private String dbDisplayName;
}
