package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.OrganizationRepository;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.web.bind.annotation.PathVariable;

/**
 *
 * @author mpostelnicu
 *
 */
public abstract class AbstractOrganizationSearchController extends GenericOCDSController {

    @Autowired
    protected OrganizationRepository organizationRepository;

    protected List<Organization> organizationSearchTextByType(final TextSearchRequest request,
                                                              Organization.OrganizationType type) {
        Query query = null;

        if (request.getText() == null) {
            query = new Query();
        } else {
            query = TextQuery.queryText(new TextCriteria().matching(request.getText())).sortByScore();
        }
        if (type != null) {
            query.addCriteria(Criteria.where("roles").is(type))
                    .with(new PageRequest(request.getPageNumber(), request.getPageSize()));
        }

        List<Organization> orgs = mongoTemplate.find(query, Organization.class);

        return orgs;
    }

    public abstract Organization byId(@PathVariable String id);

    public abstract List<Organization> searchText(@Valid TextSearchRequest request);


}