package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.cbioportal.genome_nexus.model.IndexSearch;
import org.cbioportal.genome_nexus.service.IndexSearchService;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController
@CrossOrigin(origins="*")
@RequestMapping(value= "/")
@Api(tags = "search-controller", description = "Search Controller")
public class SearchController {
    private final IndexSearchService indexSearchService;

    @Autowired
    public SearchController(IndexSearchService indexSearchService)
    {
        this.indexSearchService = indexSearchService;
    }

    @ApiOperation(value = "Performs index search.")
    @RequestMapping(value = "/search",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<IndexSearch> searchAnnotationByKeywordGET(
        @ApiParam(
            value="keyword. For example 13:g.32890665G>A, TP53 p.R273C, BRAF c.1799T>A",
            required = true)
        @RequestParam String keyword,
        @ApiParam(value="Max number of matching results to return")
        @RequestParam(required = false) Integer limit
    ) {
        return this.indexSearchService.search(keyword, limit);
    }
}
