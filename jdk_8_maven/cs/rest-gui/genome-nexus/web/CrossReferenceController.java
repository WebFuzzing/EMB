package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.*;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "cross-reference-controller", description = "Cross Reference Controller")
public class CrossReferenceController
{
    private final GeneXrefService geneXrefService;

    @Autowired
    public CrossReferenceController(GeneXrefService geneXrefService)
    {
        this.geneXrefService = geneXrefService;
    }

    @ApiOperation(value = "Perform lookups of Ensembl identifiers and retrieve their external references in other databases",
        nickname = "fetchGeneXrefsGET")
    @RequestMapping(value = "/xrefs/{accession}",
        method = RequestMethod.GET,
        produces = "application/json")
    @Deprecated
    public List<GeneXref> fetchGeneXrefsGET(
        @ApiParam(value="Ensembl gene accession. For example ENSG00000169083",
            required = true)
        @PathVariable String accession) throws EnsemblWebServiceException
    {
        return geneXrefService.getGeneXrefs(accession);
    }
}
