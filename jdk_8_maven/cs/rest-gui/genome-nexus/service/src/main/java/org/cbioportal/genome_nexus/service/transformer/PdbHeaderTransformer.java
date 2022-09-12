package org.cbioportal.genome_nexus.service.transformer;

import com.mongodb.DBObject;
import org.cbioportal.genome_nexus.model.PdbHeader;
import org.cbioportal.genome_nexus.service.ResourceTransformer;
import org.cbioportal.genome_nexus.service.exception.ResourceMappingException;
import org.cbioportal.genome_nexus.util.PdbHeaderParser;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class PdbHeaderTransformer implements ResourceTransformer<PdbHeader>
{
    @Autowired
    private PdbHeaderParser parser;

    @Override
    public List<PdbHeader> transform(DBObject rawJson, Class<PdbHeader> type) throws ResourceMappingException
    {
        List<PdbHeader> list = null;
        String pdbTextContent = this.extractPdbTextContent(rawJson);
        PdbHeader pdbHeader = this.mapToInstance(pdbTextContent);

        if (pdbHeader != null) {
            list = new ArrayList<>(1);
            list.add(pdbHeader);
        }

        return list;
    }

    @Override
    public List<DBObject> transform(DBObject rawValue) {
        // TODO implement if needed
        return null;
    }

    private PdbHeader mapToInstance(String pdbTextContent) throws ResourceMappingException
    {
        PdbHeader pdbHeader = null;

        try {
            pdbHeader = this.parser.convertToInstance(pdbTextContent);
        } catch (Exception e) {
            throw new ResourceMappingException(e.getMessage());
        }

        return pdbHeader;
    }

    /**
     * Assuming that provided raw JSON is a simple map with a single key - value (pdb id - pdb header text) pair,
     * extracts the text content by simply returning the first value.
     */
    @Nullable
    private String extractPdbTextContent(DBObject rawJson)
    {
        Collection<?> values = rawJson.toMap().values();

        if (!values.isEmpty()) {
            return values.iterator().next().toString();
        }
        else {
            return null;
        }
    }
}
