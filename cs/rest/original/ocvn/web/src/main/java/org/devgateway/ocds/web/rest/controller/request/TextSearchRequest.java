/**
 *
 */
package org.devgateway.ocds.web.rest.controller.request;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Size;

/**
 * @author mpostelnicu
 *
 */
public class TextSearchRequest extends GenericPagingRequest {

    @Size(min = 3, max = 30)
    @ApiModelProperty(value = "Searches fields indexed for text search (generally name and id) "
            + "by the given keyword text. This uses full text search.")
    private String text;

    public TextSearchRequest() {
        super();
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

}
