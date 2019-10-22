package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocvn.persistence.mongo.dao.OrgGroup;
import org.devgateway.ocvn.persistence.mongo.repository.main.OrgGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author mpostelnicu
 * 
 */
@RestController
public class OrgGroupSearchController extends GenericOCDSController {

    @Autowired
    private OrgGroupRepository orgGroupRepository;

    @RequestMapping(value = "/api/ocds/orgGroup/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<OrgGroup> orgGroups() {

        return orgGroupRepository.findAll(new Sort(Direction.ASC, Fields.UNDERSCORE_ID));

    }

    @RequestMapping(value = "/api/ocds/orgGroup/search", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<OrgGroup> groupsSearch(@ModelAttribute @Valid final TextSearchRequest request) {
        return mongoTemplate.find(textSearchQuery(request), OrgGroup.class);
    }
}