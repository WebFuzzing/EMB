package org.cbioportal.genome_nexus.service.config;

import java.io.IOException;
import java.util.*;
import org.cbioportal.genome_nexus.model.EnsemblCanonical;
import org.cbioportal.genome_nexus.persistence.EnsemblRepository;
import org.cbioportal.genome_nexus.service.internal.EnsemblServiceImpl;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 *
 * @author ochoaa
 */
@Configuration
public class EntrezGeneResolverIntegrationConfig {
    private final String CANONICAL_TRANSCRIPTS_COLLECTION = "ensembl.canonical_transcript_per_hgnc";

    public MongoTemplate mongoTemplate() throws IOException {
        MongoTemplate mongoTemplate = Mockito.mock(MongoTemplate.class);
        Mockito.when(mongoTemplate.findAll(EnsemblCanonical.class, CANONICAL_TRANSCRIPTS_COLLECTION)).thenReturn(mockEnsemblCanonicalTranscriptsList());
        return mongoTemplate;
    }

    @Bean
    public EnsemblRepository ensemblRepository() throws IOException {
        EnsemblRepository ensemblRepository = Mockito.mock(EnsemblRepository.class);

        Map<String, String> hugoToEntrezIdMap = mockEnsemblHugoSymbolEntrezIdMap();
        for (Map.Entry<String, String> entry : hugoToEntrezIdMap.entrySet()) {
            Mockito.when(ensemblRepository.findEntrezGeneIdByHugoSymbol(entry.getKey())).thenReturn(entry.getValue());
        }
        return ensemblRepository;
    }

    @Bean
    public EnsemblServiceImpl ensemblService() throws IOException {
        return new EnsemblServiceImpl(ensemblRepository());
    }

    private List<EnsemblCanonical>  mockEnsemblCanonicalTranscriptsList() throws IOException {
        List<EnsemblCanonical> transcripts = new ArrayList<>();
        EnsemblCanonical transcript1 = new EnsemblCanonical();
        transcript1.setHugoSymbol("MGAM");
        transcript1.setEntrezGeneId("8972");
        transcript1.setEnsemblCanonicalGeneId("ENSG00000257335");
        transcript1.setEnsemblCanonicalTranscriptId("ENST00000549489");
        transcript1.setPreviousSymbols("");
        transcript1.setSynonyms("MGA");
        transcripts.add(transcript1);

        EnsemblCanonical transcript2 = new EnsemblCanonical();
        transcripts.add(transcript2);
        transcript2.setHugoSymbol("MGA");
        transcript2.setEntrezGeneId("23269");
        transcript2.setEnsemblCanonicalGeneId("ENSG00000174197");
        transcript2.setEnsemblCanonicalTranscriptId("ENST00000219905");
        transcript2.setPreviousSymbols("");
        transcript2.setSynonyms("KIAA0518, MAD5, MXD5, FLJ12634");
        transcripts.add(transcript2);

        return transcripts;
    }

    private Map<String, String> mockEnsemblHugoSymbolEntrezIdMap() throws IOException {
        Map<String, String> map = new HashMap<>();
        for (EnsemblCanonical t : mockEnsemblCanonicalTranscriptsList()) {
            map.put(t.getHugoSymbol(), t.getEntrezGeneId());
        }
        return map;
    }

}
