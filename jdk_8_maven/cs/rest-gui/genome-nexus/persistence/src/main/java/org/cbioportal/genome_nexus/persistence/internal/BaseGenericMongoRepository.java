package org.cbioportal.genome_nexus.persistence.internal;

import com.mongodb.DBObject;

import org.cbioportal.genome_nexus.persistence.GenericMongoRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

public abstract class BaseGenericMongoRepository implements GenericMongoRepository
{
    protected final MongoTemplate mongoTemplate;

    public BaseGenericMongoRepository(MongoTemplate mongoTemplate)
    {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveDBObjects(String collection, List<DBObject> dbObjects)
    {
        // TODO use bulk save instead!
        for (DBObject dbObject: dbObjects) {
            // save the object into the correct repository
            this.mongoTemplate.save(dbObject, collection);
        }
    }
}
