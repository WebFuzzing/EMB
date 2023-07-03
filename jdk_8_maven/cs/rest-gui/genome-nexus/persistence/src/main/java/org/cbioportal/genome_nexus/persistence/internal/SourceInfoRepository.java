package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.AggregateSourceInfo;
import org.cbioportal.genome_nexus.model.GenomeNexusInfo;
import org.cbioportal.genome_nexus.model.VEPInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Repository;

@Repository
public class SourceInfoRepository {

    @Autowired
    private GenomeNexusInfo genomeNexusInfo;

    @Autowired
    private VEPInfo vepInfo;

    @Autowired
    private AggregateSourceInfo aggregateSourceInfo;

    @Bean
    public GenomeNexusInfo genomeNexusInfo(@Value("${genomenexus.server.version:NA}") String serverVersion, @Value("${spring.mongodb.embedded.version:NA}") String dbVersion) {
        return new GenomeNexusInfo(serverVersion, dbVersion);
    }

    @Bean
    public VEPInfo vepInfo(@Value("${gn_vep.server.version:NA}") String serverVersion, @Value("${gn_vep.cache.version:NA}") String cacheVersion, @Value("${gn_vep.region.url:}") String vepRegionURL) {
        return new VEPInfo(serverVersion, cacheVersion, vepRegionURL);
    }

    @Bean
    public AggregateSourceInfo aggregateSourceInfo() {
        return new AggregateSourceInfo(genomeNexusInfo, vepInfo);
    }

    public AggregateSourceInfo getAggregateSourceInfo() {
        return aggregateSourceInfo;
    }
}
