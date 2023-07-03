package org.cbioportal.genome_nexus.service.config;

import org.cbioportal.genome_nexus.model.GeneXref;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.mock.JsonToObjectMapper;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ExternalResourceObjectMapperTest
{
    private final JsonToObjectMapper objectMapper;

    public ExternalResourceObjectMapperTest()
    {
        this.objectMapper = new JsonToObjectMapper();
    }

    @Test
    public void testVariantAnnotationMapping() throws IOException
    {
        VariantAnnotation annotation =
            this.objectMapper.readVariantAnnotation("1_g.65325832_65325833insG.json");

        assertEquals("1:g.65325832_65325833insG", annotation.getVariantId());
        assertEquals("GRCh37", annotation.getAssemblyName());
        assertEquals("1", annotation.getSeqRegionName());
        assertEquals((Integer)65325833, annotation.getStart());
        assertEquals((Integer)65325832, annotation.getEnd());
        assertEquals("-/G", annotation.getAlleleString());
        assertEquals((Integer)1, annotation.getStrand());
        assertEquals("frameshift_variant", annotation.getMostSevereConsequence());

        assertEquals(2, annotation.getTranscriptConsequences().size());

        assertEquals("ENST00000342505",
            annotation.getTranscriptConsequences().get(0).getTranscriptId());
        assertEquals("ENSP00000343204.4:p.Leu431ValfsTer22",
            annotation.getTranscriptConsequences().get(0).getHgvsp());
        assertEquals("ENST00000342505.4:c.1289dup",
            annotation.getTranscriptConsequences().get(0).getHgvsc());
        assertEquals((Integer)430,
            annotation.getTranscriptConsequences().get(0).getProteinStart());
        assertEquals((Integer)430,
            annotation.getTranscriptConsequences().get(0).getProteinEnd());
        assertEquals(1,
            annotation.getTranscriptConsequences().get(0).getRefseqTranscriptIds().size());
        assertEquals(1,
            annotation.getTranscriptConsequences().get(0).getConsequenceTerms().size());

        assertEquals("ENST00000494904",
            annotation.getTranscriptConsequences().get(1).getTranscriptId());
        assertEquals("G",
            annotation.getTranscriptConsequences().get(1).getVariantAllele());
        assertEquals("JAK1",
            annotation.getTranscriptConsequences().get(1).getGeneSymbol());
        assertEquals(1,
            annotation.getTranscriptConsequences().get(1).getConsequenceTerms().size());
        assertNull(
            annotation.getTranscriptConsequences().get(1).getRefseqTranscriptIds());

        assertNull(annotation.getAnnotationSummary());
    }

    @Test
    public void testSignalMutationMapping() throws IOException
    {
        SignalMutation mutation =
            this.objectMapper.readSignalMutation("7_g.55241617G_A.json");

        assertEquals("7", mutation.getChromosome());
        assertEquals((Long)55241617L, mutation.getStartPosition());
        assertEquals((Long)55241617L, mutation.getEndPosition());
        assertEquals("G", mutation.getReferenceAllele());
        assertEquals("A", mutation.getVariantAllele());
        assertEquals("EGFR", mutation.getHugoGeneSymbol());
        assertEquals("somatic", mutation.getMutationStatus());
        assertEquals(45, mutation.getCountsByTumorType().size());

        assertNull(mutation.getBiallelicCountsByTumorType());
        assertNull(mutation.getQcPassCountsByTumorType());
    }

    @Test
    public void testGeneXrefMapping() throws IOException
    {
        List<GeneXref> xrefs =
            this.objectMapper.readGeneXrefs("ENSG00000020181.json");

        assertEquals(8, xrefs.size());

        assertEquals("GPR124", xrefs.get(0).getDisplayId());
        assertEquals("GPR124", xrefs.get(0).getPrimaryId());
        assertEquals("0", xrefs.get(0).getVersion());
        assertEquals("KIAA1531", xrefs.get(0).getSynonyms().get(0));
        assertEquals("TEM5", xrefs.get(0).getSynonyms().get(1));
        assertEquals("", xrefs.get(0).getInfoText());
        assertEquals("DEPENDENT", xrefs.get(0).getInfoType());
        assertEquals("UniProtKB Gene Name", xrefs.get(0).getDbDisplayName());

        assertEquals("GPR124", xrefs.get(5).getDisplayId());
        assertEquals("25960", xrefs.get(5).getPrimaryId());
        assertEquals("EntrezGene", xrefs.get(5).getDbName());
        assertEquals("EntrezGene", xrefs.get(5).getDbDisplayName());
        assertEquals("G protein-coupled receptor 124", xrefs.get(5).getDescription());
    }

    // TODO add tests for other external resources as well
}
