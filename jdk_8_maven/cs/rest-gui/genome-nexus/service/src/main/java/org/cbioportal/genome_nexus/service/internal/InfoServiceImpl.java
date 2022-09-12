package org.cbioportal.genome_nexus.service.internal;

import org.cbioportal.genome_nexus.model.AggregateSourceInfo;
import org.cbioportal.genome_nexus.model.GenomeNexusInfo;
import org.cbioportal.genome_nexus.model.VEPInfo;
import org.cbioportal.genome_nexus.persistence.internal.SourceInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:/maven.properties")
@Service
public class InfoServiceImpl {

    @Autowired
    private SourceInfoRepository sourceInfoRepository;

    public AggregateSourceInfo getAggregateSourceInfo() {
        return sourceInfoRepository.getAggregateSourceInfo();
    }

}
