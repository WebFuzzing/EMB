package org.cbioportal.genome_nexus.persistence.internal;

import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.model.SimpleCacheEntity;
import org.cbioportal.genome_nexus.util.PdbHeaderParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class PdbHeaderRepositoryImpl
    extends PlainTextMongoRepositoryImpl // this allows us to save the string value into the simple cache collection
    implements PdbHeaderRepositoryCustom<PdbHeader, String>
{
    public static final String COLLECTION = "pdb.header";

    private final PdbHeaderParser parser;

    @Autowired
    public PdbHeaderRepositoryImpl(MongoTemplate mongoTemplate, PdbHeaderParser parser)
    {
        super(mongoTemplate);
        this.parser = parser;
    }

    @Override
    public Optional<PdbHeader> findById(String id)
    {
        SimpleCacheEntity entity = this.mongoTemplate.findById(id, SimpleCacheEntity.class, COLLECTION);
        PdbHeader instance = null;

        // and then map it to a PdbHeader instance to return
        if (entity != null) {
            instance = this.parser.convertToInstance(entity.getValue());
        }

        return Optional.ofNullable(instance);
    }
}
