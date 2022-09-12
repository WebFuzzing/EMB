package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.NucleotideContext;
import org.cbioportal.genome_nexus.service.NucleotideContextService;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextNotFoundException;
import org.cbioportal.genome_nexus.service.exception.NucleotideContextWebServiceException;
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
@Api(tags = "nucleotide-context-controller", description = "Nucleotide Context Controller")
public class NucleotideContextController
{
    private final NucleotideContextService nucleotideContextService;

    @Autowired
    public NucleotideContextController(NucleotideContextService nucleotideContextService)
    {
        this.nucleotideContextService = nucleotideContextService;
    }

    @ApiOperation(value = "Retrieves nucleotide context information for the provided list of variants",
        nickname = "fetchNucleotideContextAnnotationGET")
    @RequestMapping(value = "/nucleotide_context/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public NucleotideContext fetchNucleotideContextAnnotationGET(
        @ApiParam(value="A variant. For example 7:g.140453136A>T",
            required = true,
            allowMultiple = true)
        @PathVariable String variant)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException,
        NucleotideContextWebServiceException, NucleotideContextNotFoundException
    {
        return this.nucleotideContextService.getNucleotideContext(variant);
    }

    @ApiOperation(value = "Retrieves nucleotide context information for the provided list of variants",
        nickname = "postNucleotideContextAnnotation")
    @RequestMapping(value = "/nucleotide_context",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<NucleotideContext> fetchNucleotideContextAnnotationPOST(
        @ApiParam(value="List of variants. For example [\"7:g.140453136A>T\",\"12:g.25398285C>A\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> variants)
    {
        return this.nucleotideContextService.getNucleotideContext(variants);
    }
}