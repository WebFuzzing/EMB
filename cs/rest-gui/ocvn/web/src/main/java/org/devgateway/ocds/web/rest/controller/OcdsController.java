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
package org.devgateway.ocds.web.rest.controller;

import com.fasterxml.jackson.annotation.JsonView;
import io.swagger.annotations.ApiOperation;
import org.devgateway.ocds.persistence.mongo.Publisher;
import org.devgateway.ocds.persistence.mongo.Release;
import org.devgateway.ocds.persistence.mongo.ReleasePackage;
import org.devgateway.ocds.persistence.mongo.repository.main.ReleaseRepository;
import org.devgateway.ocds.persistence.mongo.spring.json.Views;
import org.devgateway.ocds.web.rest.controller.request.YearFilterPagingRequest;
import org.devgateway.ocvn.persistence.mongo.dao.VNPlanning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.data.mongodb.core.query.Query.query;

/**
 *
 * @author mpostelnicu
 *
 */
@RestController
public class OcdsController extends GenericOCDSController {

    private static final String SERVER_DOMAIN = "http://ocvn.developmentgateway.org";

    @Autowired
    private ReleaseRepository releaseRepository;

    @ApiOperation(value = "Returns a release entity for the given project id. "
            + "The project id is read from planning.budget.projectID")
    @RequestMapping(value = "/api/ocds/release/budgetProjectId/{projectId:^[a-zA-Z0-9]*$}",
            method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public Release ocdsByProjectId(@PathVariable final String projectId) {

        Release release = releaseRepository.findByBudgetProjectId(projectId);
        return release;
    }

    /**
     * Returns one {@link Release} entity found based on
     * {@link VNPlanning#getBidNo()}
     *
     * @param bidNo
     *            the bidNo
     * @return the release
     */

    @ApiOperation(value = "Returns a release entity for the given Planning Bid Number."
            + "The planning bid number is taken from planning.bidNo")
    @RequestMapping(value = "/api/ocds/release/planningBidNo/{bidNo:^[a-zA-Z0-9]*$}",
            method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public Release ocdsByPlanningBidNo(@PathVariable final String bidNo) {

        Release release = releaseRepository.findByPlanningBidNo(bidNo);
        return release;
    }

    @ApiOperation(value = "Returns a release entity for the given open contracting id (OCID).")
    @RequestMapping(value = "/api/ocds/release/ocid/{ocid}",
            method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public Release ocdsByOcid(@PathVariable final String ocid) {

        Release release = releaseRepository.findByOcid(ocid);
        return release;
    }

    @ApiOperation(value = "Returns a release package for the given open contracting id (OCID)."
            + "This will contain the OCDS package information (metadata about publisher) plus the release itself.")
    @RequestMapping(value = "/api/ocds/package/ocid/{ocid}", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public ReleasePackage ocdsPackageByOcid(@PathVariable final String ocid) {

        Release release = releaseRepository.findByOcid(ocid);
        return createReleasePackage(release);
    }

    public ReleasePackage createReleasePackage(final Release release) {
        ReleasePackage releasePackage = new ReleasePackage();
        releasePackage.setLicense("https://creativecommons.org/licenses/by/2.0/");
        releasePackage.setPublicationPolicy("https://github.com/open-contracting/sample-data/");
        releasePackage.setPublishedDate(release.getDate());
        releasePackage.getReleases().add(release);
        releasePackage.setUri(SERVER_DOMAIN + "/api/ocds/package/ocid/" + release.getOcid());
        Publisher publisher = new Publisher();

        publisher.setName("Government of Vietnam: Public Procurement Agency");
        publisher.setScheme("VN-PPA");
        publisher.setUid(release.getOcid());
        publisher.setUri(SERVER_DOMAIN);
        releasePackage.setPublisher(publisher);
        return releasePackage;
    }

    @ApiOperation(value = "Returns a release package for the given open contracting id (OCID)."
            + "This will contain the OCDS package information (metadata about publisher) plus the release itself.")
    @RequestMapping(value = "/api/ocds/package/planningBidNo/{bidNo:^[a-zA-Z0-9]*$}",
            method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public ReleasePackage packagedReleaseByPlanningBidNo(@PathVariable final String bidNo) {
        Release release = ocdsByPlanningBidNo(bidNo);

        return createReleasePackage(release);
    }

    @ApiOperation(value = "Returns a release package for the given project id. "
            + "The project id is read from planning.budget.projectID."
            + "This will contain the OCDS package information (metadata about publisher) plus the release itself.")
    @RequestMapping(value = "/api/ocds/package/budgetProjectId/{projectId:^[a-zA-Z0-9]*$}",
            method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public ReleasePackage packagedReleaseByProjectId(@PathVariable final String projectId) {
        Release release = ocdsByProjectId(projectId);

        return createReleasePackage(release);
    }

    /**
     * Returns a list of OCDS Releases, order by Id, using pagination
     *
     * @return the release data
     */
    @ApiOperation(value = "Resturns all available releases, filtered by the given criteria.")
    @RequestMapping(value = "/api/ocds/release/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public List<Release> ocdsReleases(@ModelAttribute @Valid final YearFilterPagingRequest releaseRequest) {

        Pageable pageRequest = new PageRequest(releaseRequest.getPageNumber(), releaseRequest.getPageSize(),
                Direction.ASC, "id");

        List<Release> find = mongoTemplate
                .find(query(getYearFilterCriteria(releaseRequest, "planning.bidPlanProjectDateApprove")
                        .andOperator(getDefaultFilterCriteria(releaseRequest))).with(pageRequest), Release.class);

        return find;

    }

    @ApiOperation(value = "Returns all available packages, filtered by the given criteria."
            + "This will contain the OCDS package information (metadata about publisher) plus the release itself.")
    @RequestMapping(value = "/api/ocds/package/all", method = { RequestMethod.POST, RequestMethod.GET },
            produces = "application/json")
    @JsonView(Views.Public.class)
    public List<ReleasePackage> ocdsPackages(@ModelAttribute @Valid final YearFilterPagingRequest releaseRequest) {
        List<Release> ocdsReleases = ocdsReleases(releaseRequest);
        List<ReleasePackage> releasePackages = new ArrayList<>(ocdsReleases.size());
        for (Release release : ocdsReleases) {
            releasePackages.add(createReleasePackage(release));
        }

        return releasePackages;
    }

}