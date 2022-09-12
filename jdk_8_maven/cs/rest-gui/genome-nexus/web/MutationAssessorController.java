package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.MutationAssessor;
import org.cbioportal.genome_nexus.service.MutationAssessorService;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorNotFoundException;
import org.cbioportal.genome_nexus.service.exception.MutationAssessorWebServiceException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "mutation-assessor-controller", description = "Mutation Assessor Controller")
public class MutationAssessorController
{
    private final MutationAssessorService mutationAssessorService;

    @Autowired
    public MutationAssessorController(MutationAssessorService mutationAssessorService)
    {
        this.mutationAssessorService = mutationAssessorService;
    }

    @ApiOperation(value = "Retrieves mutation assessor information for the provided list of variants",
        nickname = "fetchMutationAssessorAnnotationGET")
    @RequestMapping(value = "/mutation_assessor/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public MutationAssessor fetchMutationAssessorAnnotationGET(
        @ApiParam(value="A variant. For example 7:g.140453136A>T",
            required = true,
            allowMultiple = true)
        @PathVariable String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        MutationAssessorWebServiceException, MutationAssessorNotFoundException
    {
        return this.mutationAssessorService.getMutationAssessor(variant);
    }

    @ApiOperation(value = "Retrieves mutation assessor information for the provided list of variants",
        nickname = "postMutationAssessorAnnotation")
    @RequestMapping(value = "/mutation_assessor",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<MutationAssessor> fetchMutationAssessorAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"7:g.140453136A>T\",\"12:g.25398285C>A\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> variants)
    {
        return this.mutationAssessorService.getMutationAssessor(variants);
    }
}
