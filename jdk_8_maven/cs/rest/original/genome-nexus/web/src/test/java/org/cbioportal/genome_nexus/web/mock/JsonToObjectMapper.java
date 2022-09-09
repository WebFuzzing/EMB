package org.cbioportal.genome_nexus.web.mock;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.PostTranslationalModification;
import org.cbioportal.genome_nexus.model.SignalMutation;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

public class JsonToObjectMapper
{
    private final TestResourceObjectMapper objectMapper;

    public JsonToObjectMapper()
    {
        this.objectMapper = new TestResourceObjectMapper();
    }

    public List<Hotspot> readHotspots(String resourceName) throws IOException
    {
        return this.objectMapper.readValue(
            new ClassPathResource("hotspot/" + resourceName).getInputStream(),
            this.objectMapper.getTypeFactory().constructCollectionType(List.class, Hotspot.class)
        );
    }

    public List<PostTranslationalModification> readPtms(String resourceName) throws IOException
    {
        return this.objectMapper.readValue(
            new ClassPathResource("ptm/" + resourceName).getInputStream(),
            this.objectMapper.getTypeFactory().constructCollectionType(List.class, PostTranslationalModification.class)
        );
    }

    public List<SignalMutation> readSignalMutations(String resourceName) throws IOException
    {
        return this.objectMapper.readValue(
            new ClassPathResource("signal/" + resourceName).getInputStream(),
            this.objectMapper.getTypeFactory().constructCollectionType(List.class, SignalMutation.class)
        );
    }

    public List<DBObject> readRawDbObjects(String resourceName) throws IOException
    {
        return this.objectMapper.readValue(
            new ClassPathResource("raw/" + resourceName).getInputStream(),
            this.objectMapper.getTypeFactory().constructCollectionType(List.class, BasicDBObject.class)
        );
    }
}
