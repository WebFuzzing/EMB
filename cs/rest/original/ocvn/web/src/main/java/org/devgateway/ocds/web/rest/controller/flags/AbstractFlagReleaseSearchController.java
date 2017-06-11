package org.devgateway.ocds.web.rest.controller.flags;

import com.fasterxml.jackson.annotation.JsonView;
import org.devgateway.ocds.persistence.mongo.FlaggedRelease;
import org.devgateway.ocds.persistence.mongo.spring.json.Views;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.validation.Valid;
import java.util.List;

/**
 * Created by mpostelnicu on 12/2/2016.
 */
public abstract class AbstractFlagReleaseSearchController extends AbstractFlagController {


    @JsonView(Views.Internal.class)
    protected List<FlaggedRelease> releaseFlagSearch(@ModelAttribute @Valid final YearFilterPagingRequest filter) {
        Query query = new Query(Criteria.where(getFlagProperty()).is(true).
                andOperator(getYearDefaultFilterCriteria(filter, getYearProperty())))
                .with(new PageRequest(filter.getPageNumber(), filter.getPageSize()));
        List<FlaggedRelease> results = mongoTemplate.find(query, FlaggedRelease.class);
        return results;
    }
}
