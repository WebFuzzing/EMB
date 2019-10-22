package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import javax.validation.Valid;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocds.web.rest.controller.request.TextSearchRequest;
import org.devgateway.ocvn.persistence.mongo.dao.OrgDepartment;
import org.devgateway.ocvn.persistence.mongo.repository.main.OrgDepartmentRepository;
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
public class OrgDepartmentSearchController extends GenericOCDSController {

    @Autowired
    private OrgDepartmentRepository orgDepartmentRepository;

    @RequestMapping(value = "/api/ocds/orgDepartment/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<OrgDepartment> orgDepartments() {

        return orgDepartmentRepository.findAll(new Sort(Direction.ASC, Fields.UNDERSCORE_ID));

    }

    @RequestMapping(value = "/api/ocds/orgDepartment/search", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<OrgDepartment> departmentSearch(@ModelAttribute @Valid final TextSearchRequest request) {
        return mongoTemplate.find(textSearchQuery(request), OrgDepartment.class);
    }

}