package org.cbioportal.genome_nexus.persistence;

import com.mongodb.DBObject;

import java.util.List;

public interface GenericMongoRepository
{
    void saveDBObject(String collection, String key, DBObject value);
    void saveDBObjects(String collection, List<DBObject> dbObjects);
}
