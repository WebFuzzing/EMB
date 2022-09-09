package org.cbioportal.genome_nexus.web;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.service.EnsemblService;
import org.cbioportal.genome_nexus.service.GeneXrefService;
import org.cbioportal.genome_nexus.service.exception.EnsemblTranscriptNotFoundException;
import org.cbioportal.genome_nexus.service.exception.EnsemblWebServiceException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForHugoSymbolException;
import org.cbioportal.genome_nexus.service.exception.NoEnsemblGeneIdForEntrezGeneIdException;
import org.cbioportal.genome_nexus.web.config.PublicApi;
import org.cbioportal.genome_nexus.web.param.EnsemblFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController // shorthand for @Controller, @ResponseBody
@CrossOrigin(origins="*") // allow all cross-domain requests
@RequestMapping(value= "/")
@Api(tags = "ensembl-controller", description = "Ensembl Controller")
@PublicApi
public class EnsemblController
{
    private final EnsemblService ensemblService;
    private final GeneXrefService geneXrefService;

    @Autowired
    public EnsemblController(EnsemblService ensemblService, GeneXrefService geneXrefService)
    {
        this.ensemblService = ensemblService;
        this.geneXrefService = geneXrefService;
    }

    @ApiOperation(value = "Retrieves canonical Ensembl Gene ID by Hugo Symbols",
        nickname = "fetchCanonicalEnsemblGeneIdByHugoSymbolsPOST")
    @RequestMapping(value = "/ensembl/canonical-gene/hgnc",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblGene> fetchCanonicalEnsemblGeneIdByHugoSymbolPOST(
        @ApiParam(value = "List of Hugo Symbols. For example [\"TP53\",\"PIK3CA\",\"BRCA1\"]",
            required = true)
        @RequestBody List<String> hugoSymbols)
    {
        return this.ensemblService.getCanonicalEnsemblGeneIdByHugoSymbols(hugoSymbols);
    }

    @ApiOperation(value = "Retrieves Ensembl canonical gene id by Hugo Symbol",
        nickname = "fetchCanonicalEnsemblGeneIdByHugoSymbolGET")
    @RequestMapping(value = "/ensembl/canonical-gene/hgnc/{hugoSymbol}",
        method = RequestMethod.GET,
        produces = "application/json")
    public EnsemblGene fetchCanonicalEnsemblGeneIdByHugoSymbolGET(
        @ApiParam(value = "A Hugo Symbol. For example TP53",
            required = true)
        @PathVariable String hugoSymbol) throws NoEnsemblGeneIdForHugoSymbolException
    {
        return this.ensemblService.getCanonicalEnsemblGeneIdByHugoSymbol(hugoSymbol);
    }

    @ApiOperation(value = "Retrieves canonical Ensembl Gene ID by Entrez Gene Ids",
        nickname = "fetchCanonicalEnsemblGeneIdByEntrezGeneIdsPOST")
    @RequestMapping(value = "/ensembl/canonical-gene/entrez",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblGene> fetchCanonicalEnsemblGeneIdByEntrezGeneIdPOST(
        @ApiParam(value = "List of Entrez Gene Ids. For example [\"23140\",\"26009\",\"100131879\"]",
            required = true)
        @RequestBody List<String> entrezGeneIds)
    {
        return this.ensemblService.getCanonicalEnsemblGeneIdByEntrezGeneIds(entrezGeneIds);
    }

    @ApiOperation(value = "Retrieves Ensembl canonical gene id by Entrez Gene Id",
        nickname = "fetchCanonicalEnsemblGeneIdByEntrezGeneIdGET")
    @RequestMapping(value = "/ensembl/canonical-gene/entrez/{entrezGeneId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public EnsemblGene fetchCanonicalEnsemblGeneIdByEntrezGeneIdGET(
        @ApiParam(value = "An Entrez Gene Id. For example 23140",
            required = true)
        @PathVariable String entrezGeneId) throws NoEnsemblGeneIdForEntrezGeneIdException
    {
        return this.ensemblService.getCanonicalEnsemblGeneIdByEntrezGeneId(entrezGeneId);
    }

    @ApiOperation(value = "Retrieves Ensembl Transcripts by protein ID, and gene ID. " +
        "Retrieves all transcripts in case no query parameter provided",
        nickname = "fetchEnsemblTranscriptsGET")
    @RequestMapping(value = "/ensembl/transcript",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsGET(
        @ApiParam(value = "An Ensembl gene ID. For example ENSG00000136999")
        @RequestParam(required = false) String geneId,
        @ApiParam(value = "An Ensembl protein ID. For example ENSP00000439985")
        @RequestParam(required = false) String proteinId,
        @ApiParam(value = "A Hugo Symbol For example ARF5")
        @RequestParam(required = false) String hugoSymbol)
    {
        return ensemblService.getEnsemblTranscripts(geneId, proteinId, hugoSymbol);
    }

    @ApiOperation(value = "Retrieves Ensembl Transcripts by Ensembl transcript IDs, hugo Symbols, protein IDs, or gene IDs",
        nickname = "fetchEnsemblTranscriptsByEnsemblFilterPOST")
    @RequestMapping(value = "/ensembl/transcript",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchEnsemblTranscriptsByEnsemblFilterPOST(
        @ApiParam(
            value = EnsemblFilter.TRANSCRIPT_ID_DESC + "<br>OR<br>" +
                EnsemblFilter.HUGO_SYMBOL_DESC + "<br>OR<br>" +
                EnsemblFilter.PROTEIN_ID_DESC + "<br>OR<br>" +
                EnsemblFilter.GENE_ID_DESC,
            required = true)
        @RequestBody EnsemblFilter ensemblFilter)
    {
        return this.ensemblService.getEnsemblTranscripts(
            ensemblFilter.getTranscriptIds(), ensemblFilter.getGeneIds(), ensemblFilter.getProteinIds(), ensemblFilter.getHugoSymbols());
    }

    @ApiOperation(value = "Retrieves the transcript by an Ensembl transcript ID",
        nickname = "fetchEnsemblTranscriptByTranscriptIdGET")
    @RequestMapping(value = "/ensembl/transcript/{transcriptId}",
        method = RequestMethod.GET,
        produces = "application/json")
    public EnsemblTranscript fetchEnsemblTranscriptByTranscriptIdGET(
        @ApiParam(value = "An Ensembl transcript ID. For example ENST00000361390",
            required = true)
        @PathVariable String transcriptId) throws EnsemblTranscriptNotFoundException
    {
        return this.ensemblService.getEnsemblTranscriptsByTranscriptId(transcriptId);
    }

    @ApiOperation(value = "Retrieves Ensembl canonical transcripts by Hugo Symbols",
        nickname = "fetchCanonicalEnsemblTranscriptsByHugoSymbolsPOST")
    @RequestMapping(value = "/ensembl/canonical-transcript/hgnc",
        method = RequestMethod.POST,
        produces = "application/json")
    public List<EnsemblTranscript> fetchCanonicalEnsemblTranscriptsByHugoSymbolPOST(
        @ApiParam(value = "List of Hugo Symbols. For example [\"TP53\",\"PIK3CA\",\"BRCA1\"]",
            required = true)
        @RequestBody List<String> hugoSymbols,
        @ApiParam(value="Isoform override source. For example uniprot",
            defaultValue="uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource)
    {
        return this.ensemblService.getCanonicalEnsemblTranscriptsByHugoSymbols(hugoSymbols, isoformOverrideSource);
    }

    @ApiOperation(value = "Retrieves Ensembl canonical transcript by Hugo Symbol",
        nickname = "fetchCanonicalEnsemblTranscriptByHugoSymbolGET")
    @RequestMapping(value = "/ensembl/canonical-transcript/hgnc/{hugoSymbol}",
        method = RequestMethod.GET,
        produces = "application/json")
    public EnsemblTranscript fetchCanonicalEnsemblTranscriptByHugoSymbolGET(
        @ApiParam(value = "A Hugo Symbol. For example TP53",
            required = true)
        @PathVariable String hugoSymbol,
        @ApiParam(value="Isoform override source. For example uniprot",
            defaultValue="uniprot",
            required = false)
        @RequestParam(required = false) String isoformOverrideSource) throws EnsemblTranscriptNotFoundException
    {
        return this.ensemblService.getCanonicalEnsemblTranscriptByHugoSymbol(hugoSymbol, isoformOverrideSource);
    }

    @ApiOperation(value = "Perform lookups of Ensembl identifiers and retrieve their external references in other databases",
        nickname = "fetchGeneXrefsGET")
    @RequestMapping(value = "/ensembl/xrefs",
        method = RequestMethod.GET,
        produces = "application/json")
    public List<GeneXref> fetchGeneXrefsGET(
        @ApiParam(value="Ensembl gene accession. For example ENSG00000169083",
            required = true)
        @RequestParam String accession) throws EnsemblWebServiceException
    {
        return geneXrefService.getGeneXrefs(accession);
    }
}
