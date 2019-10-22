package org.devgateway.ocds.web.rest.controller.request;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;

public class OrganizationIdWrapper {

    @ApiModelProperty(value = "List of organization identifiers")
    private List<String> id;

    public List<String> getId() {
        return id;
    }

    public void setId(List<String> ids) {
        this.id = ids;
    }

}
