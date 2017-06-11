/**
 *
 */
package org.devgateway.ocds.web.rest.controller.request;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author mpostelnicu
 *
 */
public class GroupingFilterPagingRequest extends YearFilterPagingRequest {

    @ApiModelProperty(value = "This parameter specified which category can be used for grouping the results."
            + " Possible values here are: bidSelectionMethod, bidTypeId, procuringEntityId.")
    private String groupByCategory;

    public String getGroupByCategory() {
        return groupByCategory;
    }

    public void setGroupByCategory(final String groupByCategory) {
        this.groupByCategory = groupByCategory;
    }

}
