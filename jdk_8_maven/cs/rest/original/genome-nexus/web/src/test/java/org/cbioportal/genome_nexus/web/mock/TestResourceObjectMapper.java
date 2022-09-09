package org.cbioportal.genome_nexus.web.mock;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.cbioportal.genome_nexus.model.CountByTumorType;
import org.cbioportal.genome_nexus.model.Hotspot;
import org.cbioportal.genome_nexus.model.SignalMutation;

import java.util.HashMap;
import java.util.Map;

public class TestResourceObjectMapper extends ObjectMapper
{
    public TestResourceObjectMapper()
    {
        Map<Class<?>, Class<?>> mixinMap = new HashMap<>();

        mixinMap.put(Hotspot.class, HotspotMixin.class);

        super.setMixIns(mixinMap);
    }
}
