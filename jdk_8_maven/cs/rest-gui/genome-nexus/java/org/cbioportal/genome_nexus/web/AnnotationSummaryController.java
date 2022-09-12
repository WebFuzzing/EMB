package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cbioportal.genome_nexus.model.VariantAnnotationSummary;
import org.cbioportal.genome_nexus.service.VariantAnnotationSummaryService;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationNotFoundException;
import org.cbioportal.genome_nexus.service.exception.VariantAnnotationWebServiceException;
import org.cbioportal.genome_nexus.web.config.InternalApi;
import org.cbioportal.genome_nexus.web.param.TranscriptSummaryProjection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@InternalApi
@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
public class AnnotationSummaryController
{
    private final VariantAnnotationSummaryService variantAnnotationSummaryService;

    @Autowired
    public AnnotationSummaryController(VariantAnnotationSummaryService variantAnnotationSummaryService)
    {
        this.variantAnnotationSummaryService = variantAnnotationSummaryService;
    }

    @ApiOperation(value = "Retrieves VEP annotation summary for the provided list of variants",
        nickname = "fetchVariantAnnotationSummaryPOST")
    @RequestMapping(value = "/annotation/summary",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<VariantAnnotationSummary> fetchVariantAnnotationSummaryPOST(
        @ApiParam(value="List of variants. For example [\"X:g.66937331T>A\",\"17:g.41242962_41242963insGA\"] (GRCh37) " +
            "or [\"1:g.182712A>C\", \"2:g.265023C>T\", \"3:g.319781del\", \"19:g.110753dup\", " +
            "\"1:g.1385015_1387562del\"] (GRCh38)",
            required = true)
        @RequestBody List<String> variants,
        @ApiParam(value="Isoform override source. For example uniprot")
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Indicates whether to return summary for all transcripts or only for canonical transcript")
        @RequestParam(defaultValue = "ALL") TranscriptSummaryProjection projection)
    {
        if (projection == TranscriptSummaryProjection.CANONICAL) {
            return this.variantAnnotationSummaryService.getAnnotationSummaryForCanonical(variants, isoformOverrideSource);
        }
        else {
            return this.variantAnnotationSummaryService.getAnnotationSummary(variants, isoformOverrideSource);
        }
    }

    @ApiOperation(value = "Retrieves VEP annotation summary for the provided variant",
        nickname = "fetchVariantAnnotationSummaryGET")
    @RequestMapping(value = "/annotation/summary/{variant:.+}",
        method = RequestMethod.GET,
        produces = "application/json")
    public VariantAnnotationSummary fetchVariantAnnotationSummaryGET(
        @ApiParam(value="Variant. For example 17:g.41242962_41242963insGA",
            required = true)
        @PathVariable String variant,
        @ApiParam(value="Isoform override source. For example uniprot")
        @RequestParam(required = false) String isoformOverrideSource,
        @ApiParam(value="Indicates whether to return summary for all transcripts or only for canonical transcript")
        @RequestParam(defaultValue = "ALL") TranscriptSummaryProjection projection)
        throws VariantAnnotationNotFoundException, VariantAnnotationWebServiceException
    {
        if (projection == TranscriptSummaryProjection.CANONICAL) {
            // Default projection returns both the canonical and all transcript
            // consequence summaries. This returns just the summary for the
            // canonical transcript
            return this.variantAnnotationSummaryService.getAnnotationSummaryForCanonical(variant, isoformOverrideSource);
        }
        else {
            return this.variantAnnotationSummaryService.getAnnotationSummary(variant, isoformOverrideSource);
        }

    }
}
