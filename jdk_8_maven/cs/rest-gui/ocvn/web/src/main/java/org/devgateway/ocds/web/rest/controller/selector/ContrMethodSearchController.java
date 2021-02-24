package org.devgateway.ocds.web.rest.controller.selector;

import java.util.List;

import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.devgateway.ocvn.persistence.mongo.dao.ContrMethod;
import org.devgateway.ocvn.persistence.mongo.repository.main.ContrMethodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 * @author mpostelnicu
 * 
 */
@RestController
public class ContrMethodSearchController extends GenericOCDSController {

    @Autowired
    private ContrMethodRepository contrMethodRepository;

    @RequestMapping(value = "/api/ocds/contrMethod/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    public List<ContrMethod> contrMethods() {

        return contrMethodRepository.findAll(new Sort(Direction.ASC, Fields.UNDERSCORE_ID));

    }

}