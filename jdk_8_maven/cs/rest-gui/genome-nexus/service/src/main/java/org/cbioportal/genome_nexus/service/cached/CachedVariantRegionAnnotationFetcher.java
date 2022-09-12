
package org.cbioportal.genome_nexus.service.cached;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.persistence.VariantAnnotationRepository;
import org.cbioportal.genome_nexus.persistence.internal.VariantAnnotationRepositoryImpl;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.service.transformer.ExternalResourceTransformer;
import org.cbioportal.genome_nexus.service.remote.VEPIdDataFetcher;
import org.cbioportal.genome_nexus.service.remote.VEPRegionDataFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class CachedVariantRegionAnnotationFetcher extends BaseCachedVariantAnnotationFetcher
{
    @Autowired
    public CachedVariantRegionAnnotationFetcher(ExternalResourceTransformer<VariantAnnotation> transformer,
                                                VariantAnnotationRepository repository,
                                                VEPRegionDataFetcher fetcher,
                                                @Value("${vep.max_page_size:200}") Integer maxPageSize)
    {
        super(VariantAnnotationRepositoryImpl.COLLECTION,
            repository,
            VariantAnnotation.class,
            fetcher,
            transformer,
            maxPageSize);
    }

    @Override
    protected Boolean isValidId(String id) 
    {
        // e.g.
        // 17:36002278-36002277:1/A
        //  1:206811015-206811016:1/-
        return (
            !id.contains("N") && !id.contains("undefined") && !id.contains("g.") &&
            id.contains("-") &&
            // should have two :
            (id.length() - id.replace(":","").length() == 2) &&
            // should have one /
            (id.length() - id.replace("/","").length() == 1) &&
            // should be (digit|X|Y|MT):digit-digit:1/character(s)
            (id.split(":")[0].matches("\\d+") || id.split(":")[0].contains("X") || id.split(":")[0].contains("Y") || id.split(":")[0].contains("MT")) &&
            id.split(":")[1].split("-")[0].matches("\\d+") &&
            id.split(":")[1].split("-")[1].matches("\\d+") &&
            id.split(":")[2].startsWith("1/")
        );
    }

}
