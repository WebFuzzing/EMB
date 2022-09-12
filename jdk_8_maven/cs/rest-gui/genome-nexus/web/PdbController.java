package org.cbioportal.genome_nexus.web;

import java.util.List;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.PdbDataService;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderNotFoundException;
import org.cbioportal.genome_nexus.service.exception.PdbHeaderWebServiceException;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Selcuk Onur Sumer
 */
@PublicApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "pdb-controller", description = "PDB Controller")
public class PdbController
{
    private final PdbDataService pdbDataService;

    @Autowired
    public PdbController(PdbDataService pdbDataService)
    {
        this.pdbDataService = pdbDataService;
    }

    @ApiOperation(value = "Retrieves PDB header info by a PDB id",
        nickname = "fetchPdbHeaderGET")
    @RequestMapping(value = "pdb/header/{pdbId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public PdbHeader fetchPdbHeaderGET(
        @ApiParam(value = "PDB id, for example 1a37",
            required = true,
            allowMultiple = true)
        @PathVariable String pdbId) throws PdbHeaderNotFoundException, PdbHeaderWebServiceException
    {
        return pdbDataService.getPdbHeader(pdbId);
    }

    @ApiOperation(value = "Retrieves PDB header info by a PDB id",
        nickname = "fetchPdbHeaderPOST")
    @RequestMapping(value = "pdb/header",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PdbHeader> fetchPdbHeaderPOST(
        @ApiParam(value = "List of pdb ids, for example [\"1a37\",\"1a4o\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> pdbIds)
    {
        return this.pdbDataService.getPdbHeaders(pdbIds);
    }
}
