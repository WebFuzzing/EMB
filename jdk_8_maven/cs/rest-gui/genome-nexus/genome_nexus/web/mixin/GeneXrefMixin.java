package org.cbioportal.genome_nexus.web.mixin;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GeneXrefMixin
{
    @JsonProperty(value = "display_id", required = true)
    @ApiModelProperty(value = "Display id", required = true)
    private String displayId;

    @JsonProperty(value = "primary_id", required = true)
    @ApiModelProperty(value = "Primary id", required = true)
    private String primaryId;

    @JsonProperty(value = "version", required = true)
    @ApiModelProperty(value = "Version", required = true)
    private String version;

    @JsonProperty(value = "description", required = true)
    @ApiModelProperty(value = "Description", required = true)
    private String description;

    @JsonProperty(value = "dbname", required = true)
    @ApiModelProperty(value = "Database name", required = true)
    private String dbName;

    @JsonProperty(value = "synonyms", required = true)
    @ApiModelProperty(value = "Synonyms", required = false)
    private List<String> synonyms;

    @JsonProperty(value = "info_text", required = true)
    @ApiModelProperty(value = "Database info text", required = false)
    private String infoText;

    @JsonProperty(value = "info_types", required = true)
    @ApiModelProperty(value = "Database info type", required = false)
    private String infoType;

    @JsonProperty(value = "db_display_name", required = true)
    @ApiModelProperty(value = "Database display name", required = true)
    private String dbDisplayName;
}
