/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocds.web.rest.controller.selector;

import org.devgateway.ocds.persistence.mongo.Classification;
import org.devgateway.ocds.persistence.mongo.repository.main.ClassificationRepository;
import org.devgateway.ocds.web.rest.controller.GenericOCDSController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;

import java.util.List;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
@Cacheable
@CacheConfig(cacheNames = "bidTypesJson")
public class BidTypesSearchController extends GenericOCDSController {

    @Autowired
    private ClassificationRepository classificationRepository;

    @ApiOperation(value = "Display the available bid types. "
            + "These are the Classification entities in OCDS")
    @RequestMapping(value = "/api/ocds/bidType/all",
            method = { RequestMethod.POST, RequestMethod.GET }, produces = "application/json")
    public List<Classification> bidTypes() {

        return classificationRepository.findAll(new Sort(Direction.ASC, "description"));

    }

}