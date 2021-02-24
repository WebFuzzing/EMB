package org.devgateway.ocds.web.rest.controller.request;

import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author mpostelnicu
 *
 */
public class OrganizationSearchRequest extends GenericPagingRequest {

    @Size(min = 3, max = 30)
    @ApiModelProperty(value = "Searches organization fields (name and id) by the given keyword text. "
            + "This uses full text search.")
    private String text;

    public OrganizationSearchRequest() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
