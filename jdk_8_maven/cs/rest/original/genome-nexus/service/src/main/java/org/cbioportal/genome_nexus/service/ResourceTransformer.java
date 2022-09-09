package org.cbioportal.genome_nexus.service;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;

import java.util.List;

public interface ResourceTransformer<T>
{
    List<T> transform(DBObject value, Class<T> type) throws ResourceMappingException;
    List<DBObject> transform(DBObject value);
}
