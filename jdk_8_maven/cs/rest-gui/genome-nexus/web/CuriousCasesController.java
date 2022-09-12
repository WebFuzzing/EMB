package org.cbioportal.genome_nexus.web;

import org.cbioportal.genome_nexus.model.CuriousCases;
import org.cbioportal.genome_nexus.service.CuriousCasesService;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesNotFoundException;
import org.cbioportal.genome_nexus.service.exception.CuriousCasesWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "curious-cases-controller", description = "Curious Cases Controller")
public class CuriousCasesController
{
    private final CuriousCasesService curiousCasesService;

    @Autowired
    public CuriousCasesController(CuriousCasesService curiousCasesService)
    {
        this.curiousCasesService = curiousCasesService;
    }

    @ApiOperation(value = "Retrieves Curious Cases info by a genomic location",
        nickname = "fetchCuriousCasesGET")
    @RequestMapping(value = "curious_cases/{genomicLocation}",
        method = RequestMethod.GET,
        produces = "application/json")
    public CuriousCases fetchCuriousCasesGET(
        @ApiParam(value = "Genomic location, for example: 7,116411883,116411905,TTCTTTCTCTCTGTTTTAAGATC,-",
            required = true,
            allowMultiple = false)
        @PathVariable String genomicLocation) throws CuriousCasesNotFoundException, CuriousCasesWebServiceException
    {
        return curiousCasesService.getCuriousCases(genomicLocation);
    }
}