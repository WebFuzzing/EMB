package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.EnsemblCanonical;
import org.cbioportal.genome_nexus.model.EnsemblGene;
import org.cbioportal.genome_nexus.model.EnsemblTranscript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Repository;

@Repository
public class EnsemblRepositoryImpl implements EnsemblRepositoryCustom
{
    private final MongoTemplate mongoTemplate;
    private Map<String, String> hugoSymbolToEntrezGeneIdMap = new HashMap<>();
    private Map<String, String> entrezGeneIdToHugoSymbolMap = new HashMap<>();
    private Map<String, List<String>> geneAliasToEntrezGeneIdMap = new HashMap<>();

    @Autowired
    public EnsemblRepositoryImpl(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
        initHugoSymbolToEntrezGeneIdMap();
    }

    public static final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";
    public static final String TRANSCRIPTS_COLLECTION = "ensembl.biomart_transcripts";

    private EnsemblCanonical findOneCanonicalByHugoSymbol(String hugoSymbol) {
        EnsemblCanonical ensemblCanonical;
        Query query;

        // check approved symbols
        Criteria approvedSymbolCriteria = Criteria.where("hgnc_symbol").regex("^" + hugoSymbol + "$", "i");
        query = new Query();
        query.addCriteria(approvedSymbolCriteria);
        ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        if (ensemblCanonical == null) {
            // check prev symbols
            Criteria prevSymbolsCriteria = Criteria.where("previous_symbols").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
            query = new Query();
            query.addCriteria(prevSymbolsCriteria);
            ensemblCanonical = mongoTemplate.findOne(query,
                EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

            if (ensemblCanonical == null) {
                // check synonyms
                Criteria synonymCriteria = Criteria.where("synonyms").regex("(^| |,)" + hugoSymbol + "($| |,)", "i");
                query = new Query();
                query.addCriteria(synonymCriteria);
                ensemblCanonical = mongoTemplate.findOne(query,
                    EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);
            }
        }

        return ensemblCanonical;
    }

    private EnsemblCanonical findOneCanonicalByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical;
        Query query;

        // search by entrez gene id
        Criteria criteria = Criteria.where("entrez_gene_id").is(Integer.valueOf(entrezGeneId));
        query = new Query();
        query.addCriteria(criteria);
        ensemblCanonical = mongoTemplate.findOne(query,
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        return ensemblCanonical;
    }

    @Override
    public EnsemblGene getCanonicalEnsemblGeneIdByHugoSymbol(String hugoSymbol) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            return new EnsemblGene(ensemblCanonical);
        } else {
            return null;
        }
    }

    @Override
    public EnsemblGene getCanonicalEnsemblGeneIdByEntrezGeneId(String entrezGeneId) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByEntrezGeneId(entrezGeneId);
        if (ensemblCanonical != null) {
            return new EnsemblGene(ensemblCanonical);
        } else {
            return null;
        }
    }

    @Override
    public EnsemblTranscript findOneByHugoSymbolIgnoreCase(String hugoSymbol, String isoformOverrideSource) {
        EnsemblCanonical ensemblCanonical = this.findOneCanonicalByHugoSymbol(hugoSymbol);
        if (ensemblCanonical != null) {
            Query query = new Query();
            query.addCriteria(Criteria.where(EnsemblTranscript.TRANSCRIPT_ID_FIELD_NAME).is(ensemblCanonical.getCanonicalTranscriptId(isoformOverrideSource)));

            EnsemblTranscript transcript = mongoTemplate.findOne(query, EnsemblTranscript.class, TRANSCRIPTS_COLLECTION);
            if (transcript != null) {
                return transcript;
            }
        }
        return null;
    }

    private void initHugoSymbolToEntrezGeneIdMap() {
        List<EnsemblCanonical> transcripts = mongoTemplate.findAll(EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);
        for (EnsemblCanonical transcript : transcripts) {
            String[] previousSymbols = transcript.getPreviousSymbols();
            String[] synonyms = transcript.getSynonyms();

            hugoSymbolToEntrezGeneIdMap.put(transcript.getHugoSymbol(), transcript.getEntrezGeneId());
            entrezGeneIdToHugoSymbolMap.put(transcript.getEntrezGeneId(), transcript.getHugoSymbol());

            // treat previous symbols as an alias for current entrez id
            if (previousSymbols != null) {
                for (String previousSymbol : previousSymbols) {
                    List<String> aliases = geneAliasToEntrezGeneIdMap.getOrDefault(previousSymbol, new ArrayList<>());
                    if (!aliases.contains(transcript.getEntrezGeneId())){
                        aliases.add(transcript.getEntrezGeneId());
                    }
                    geneAliasToEntrezGeneIdMap.put(previousSymbol, aliases);
                }
            }
            // add current entrez id to each of its aliases set of ids
            if (synonyms != null) {
                for (String synonym : synonyms) {
                    List<String> aliases = geneAliasToEntrezGeneIdMap.getOrDefault(synonym, new ArrayList<>());
                    if (!aliases.contains(transcript.getEntrezGeneId())){
                        aliases.add(transcript.getEntrezGeneId());
                    }
                    geneAliasToEntrezGeneIdMap.put(synonym, aliases);
                }
            }
        }
    }

    @Override
    public String findEntrezGeneIdByHugoSymbol(String hugoSymbol) {
        return hugoSymbolToEntrezGeneIdMap.get(hugoSymbol);
    }

    @Override
    public List<String> findEntrezGeneIdByHugoSymbol(String hugoSymbol, Boolean searchInAliases) {
        List<String> entrezGeneIdMatches = Arrays.asList(hugoSymbolToEntrezGeneIdMap.get(hugoSymbol));
        // if searching in aliases then also return matching entrez ids from alias map
        if (searchInAliases) {
            if (geneAliasToEntrezGeneIdMap.containsKey(hugoSymbol)) {
                entrezGeneIdMatches.addAll(geneAliasToEntrezGeneIdMap.get(hugoSymbol));
            }
        }
        return entrezGeneIdMatches;
    }

    @Override
    public String findHugoSymbolByEntrezGeneId(String entrezGeneId) {
        return entrezGeneIdToHugoSymbolMap.get(entrezGeneId);
    }

    @Override
    public Set<String> findCanonicalTranscriptIdsBySource(String isoformOverrideSource) {
        List<EnsemblCanonical> transcripts = mongoTemplate.findAll(
            EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION);

        return transcripts
            .stream()
            .map(t -> t.getCanonicalTranscriptId(isoformOverrideSource))
            .collect(Collectors.toSet());
    }
}
