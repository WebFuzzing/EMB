package org.cbioportal.genome_nexus.service.mock;

import java.util.HashMap;
import java.util.Map;

import org.cbioportal.genome_nexus.model.my_variant_info_model.MyVariantInfo;
import org.cbioportal.genome_nexus.service.MockData;

public class MyVariantInfoMockData implements MockData<MyVariantInfo>
{
    @Override
    public Map<String, MyVariantInfo> generateData()
    {
        Map<String, MyVariantInfo> mockData = new HashMap<>();

        MyVariantInfo myVariantInfo;

        // mock data for variant: 
        myVariantInfo = new MyVariantInfo();
        myVariantInfo.setHgvs("7:g.140453136A>T");
        myVariantInfo.setVersion(1);
        

        mockData.put("7:g.140453136A>T", myVariantInfo);

        // mock data for variant: 
        myVariantInfo = new MyVariantInfo();
        myVariantInfo.setHgvs("12:g.25398285C>A");
        myVariantInfo.setVersion(2);

        mockData.put("12:g.25398285C>A", myVariantInfo);


        // mock data for variant: INVALID
        myVariantInfo = new MyVariantInfo();
        myVariantInfo.setHgvs(null);
        myVariantInfo.setVersion(null);

        mockData.put("INVALID", myVariantInfo);

        return mockData;
    }
}
