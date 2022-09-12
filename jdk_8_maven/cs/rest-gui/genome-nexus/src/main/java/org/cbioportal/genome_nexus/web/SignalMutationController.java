package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.service.SignalMutationService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.cbioportal.genome_nexus.web.param.SignalMutationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "signal-mutation-controller", description = "Signal Mutation Controller")
public class SignalMutationController
{
    private final SignalMutationService signalMutationService;

    @Autowired
    public SignalMutationController(SignalMutationService signalMutationService)
    {
        this.signalMutationService = signalMutationService;
    }

    @ApiOperation(value = "Retrieves SignalDB mutations by Mutation Filter")
    @RequestMapping(value = "/signal/mutation",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<SignalMutation> fetchSignalMutationsByMutationFilterPOST(
        @ApiParam(
            value = "List of Hugo Gene Symbols. For example [\"TP53\", \"PIK3CA\", \"BRCA1\"]",
            required = true)
        @RequestBody SignalMutationFilter mutationFilter)
    {
        return this.signalMutationService.getSignalMutations(mutationFilter.getHugoSymbols());
    }

    @ApiOperation(value = "Retrieves SignalDB mutations by Hugo Gene Symbol")
    @RequestMapping(value = "/signal/mutation",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<SignalMutation> fetchSignalMutationsByHugoSymbolGET(
        @ApiParam(
            value="Hugo Symbol. For example BRCA1",
            required = false)
        @RequestParam(required = false) String hugoGeneSymbol)
    {
        return this.signalMutationService.getSignalMutations(hugoGeneSymbol);
    }

    @ApiOperation(value = "Retrieves SignalDB mutations by hgvgs variant")
    @RequestMapping(value = "/signal/mutation/hgvs/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<SignalMutation> fetchSignalMutationsByHgvsgGET(
        @ApiParam(
            value="A variant. For example 13:g.32890665G>A",
            required = true)
        @PathVariable String variant
    )
    {
        return this.signalMutationService.getSignalMutationsByHgvsg(variant);
    }
}
