package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.SignalQuery;
import org.cbioportal.genome_nexus.service.SignalQueryService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "signal-query-controller", description = "Signal Query Controller")
public class SignalQueryController
{
    private final SignalQueryService signalQueryService;

    @Autowired
    public SignalQueryController(SignalQueryService signalQueryService)
    {
        this.signalQueryService = signalQueryService;
    }

    @ApiOperation(value = "Performs search by gene, protein change, variant or region.")
    @RequestMapping(value = "/signal/search",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<SignalQuery> searchSignalByKeywordGET(
        @ApiParam(
            value="keyword. For example BRCA; BRAF V600; 13:32906640-32906640; 13:g.32890665G>A",
            required = true)
        @RequestParam String keyword,
        @ApiParam(value="Max number of matching results to return")
        @RequestParam(required = false) Integer limit
    ) {
        return this.signalQueryService.search(keyword, limit);
    }
}
