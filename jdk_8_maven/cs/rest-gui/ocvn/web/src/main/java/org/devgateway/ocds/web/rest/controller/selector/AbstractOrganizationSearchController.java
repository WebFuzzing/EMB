package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;
import java.util.regex.Pattern;
import javax.validation.Valid;
import org.devgateway.ocds.persistence.mongo.Organization;
import org.devgateway.ocds.persistence.mongo.repository.main.OrganizationRepository;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author mpostelnicu
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
            //this is for Full Text Search, in case we need this later, right now it's not very useful
            //query = TextQuery.queryText(new TextCriteria().matching(request.getText())).sortByScore();

            query = new Query().addCriteria(Criteria.where("name")
                    .regex(Pattern.compile(request.getText(), Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE)));
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