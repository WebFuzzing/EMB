package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.PfamDomain;
import org.cbioportal.genome_nexus.service.PfamDomainService;
import org.cbioportal.genome_nexus.service.exception.PfamDomainNotFoundException;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Selcuk Onur Sumer
 */
@PublicApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "pfam-controller", description = "PFAM Controller")
public class PfamController
{
    private final PfamDomainService pfamDomainService;

    @Autowired
    public PfamController(PfamDomainService pfamDomainService)
    {
        this.pfamDomainService = pfamDomainService;
    }

    @ApiOperation(value = "Retrieves PFAM domains by PFAM domain accession IDs",
        nickname = "fetchPfamDomainsByPfamAccessionPOST")
    @RequestMapping(value = "/pfam/domain",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<PfamDomain> fetchPfamDomainsByPfamAccessionPOST(
        @ApiParam(value = "List of PFAM domain accession IDs. For example [\"PF02827\",\"PF00093\",\"PF15276\"]",
            required = true,
            allowMultiple = true)
        @RequestBody List<String> pfamAccessions)
    {
        return this.pfamDomainService.getPfamDomainsByPfamAccession(pfamAccessions);
    }

    @ApiOperation(value = "Retrieves a PFAM domain by a PFAM domain ID",
        nickname = "fetchPfamDomainsByAccessionGET")
    @RequestMapping(value = "/pfam/domain/{pfamAccession}",
        method = RequestMethod.GET,
        produces = "application/json")
    public PfamDomain fetchPfamDomainsByAccessionGET(
        @ApiParam(value = "A PFAM domain accession ID. For example PF02827",
            required = true,
            allowMultiple = true)
        @PathVariable String pfamAccession) throws PfamDomainNotFoundException
    {
        return this.pfamDomainService.getPfamDomainByPfamAccession(pfamAccession);
    }
}
