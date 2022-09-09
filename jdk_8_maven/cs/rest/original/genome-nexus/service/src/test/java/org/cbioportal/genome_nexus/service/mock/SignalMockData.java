package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.SignalMutation;
import org.cbioportal.genome_nexus.service.MockData;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class SignalMockData implements MockData<SignalMutation>
{
    private final JsonToObjectMapper objectMapper;

    public SignalMockData()
    {
        this.objectMapper = new JsonToObjectMapper();
    }

    @Override
    public Map<String, SignalMutation> generateData() throws IOException
    {
        Map<String, SignalMutation> mockData = new LinkedHashMap<>();

        mockData.put("7:g.55241617G>A",
            this.objectMapper.readSignalMutation("7_g.55241617G_A.json"));
        mockData.put("7:g.140453136A>T",
            this.objectMapper.readSignalMutation("7_g.140453136A_T.json"));
        mockData.put("13:g.32914438del",
            this.objectMapper.readSignalMutation("13_g.32914438del.json"));
        mockData.put("17:g.41276045_41276046del",
            this.objectMapper.readSignalMutation("17_g.41276045_41276046del.json"));
        mockData.put("17:g.41276046_41276047insG",
            this.objectMapper.readSignalMutation("17_g.41276046_41276047insG.json"));

        return mockData;
    }
}
