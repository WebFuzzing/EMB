package org.cbioportal.genome_nexus.service.mock;

import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.service.MockData;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class VariantAnnotationMockData implements MockData<VariantAnnotation>
{
    private final JsonToObjectMapper objectMapper;

    public VariantAnnotationMockData()
    {
        this.objectMapper = new JsonToObjectMapper();
    }

    @Override
    public Map<String, VariantAnnotation> generateData() throws IOException
    {
        Map<String, VariantAnnotation> mockData = new LinkedHashMap<>();

        mockData.put("1:g.65325832_65325833insG",
            this.objectMapper.readVariantAnnotation("1_g.65325832_65325833insG.json"));
        mockData.put("3:g.14106026_14106037del",
            this.objectMapper.readVariantAnnotation("3_g.14106026_14106037del.json"));
        mockData.put("3:g.14940279_14940280insCAT",
            this.objectMapper.readVariantAnnotation("3_g.14940279_14940280insCAT.json"));
        mockData.put("3:g.114058003del",
            this.objectMapper.readVariantAnnotation("3_g.114058003del.json"));
        mockData.put("4:g.9784947_9784948insAGA",
            this.objectMapper.readVariantAnnotation("4_g.9784947_9784948insAGA.json"));
        mockData.put("4:g.77675978_77675979insC",
            this.objectMapper.readVariantAnnotation("4_g.77675978_77675979insC.json"));
        mockData.put("6:g.137519505_137519506delinsA",
            this.objectMapper.readVariantAnnotation("6_g.137519505_137519506delinsA.json"));
        mockData.put("6:g.137519505_137519506del",
            this.objectMapper.readVariantAnnotation("6_g.137519505_137519506del.json"));
        mockData.put("7:g.55220240G>T",
            this.objectMapper.readVariantAnnotation("7_g.55220240G_T.json"));
        mockData.put("7:g.55241617G>A",
            this.objectMapper.readVariantAnnotation("7_g.55241617G_A.json"));
        mockData.put("7:g.140453136A>T",
            this.objectMapper.readVariantAnnotation("7_g.140453136A_T.json"));
        mockData.put("8:g.37696499_37696500insG",
            this.objectMapper.readVariantAnnotation("8_g.37696499_37696500insG.json"));
        mockData.put("9:g.135797242delinsAT",
            this.objectMapper.readVariantAnnotation("9_g.135797242delinsAT.json"));
        mockData.put("10:g.101953779del",
            this.objectMapper.readVariantAnnotation("10_g.101953779del.json"));
        mockData.put("11:g.62393546_62393547delinsAA",
            this.objectMapper.readVariantAnnotation("11_g.62393546_62393547delinsAA.json"));
        mockData.put("12:g.25398285C>A",
            this.objectMapper.readVariantAnnotation("12_g.25398285C_A.json"));
        mockData.put("13:g.28608258_28608275del",
            this.objectMapper.readVariantAnnotation("13_g.28608258_28608275del.json"));
        mockData.put("13:g.32914438del",
            this.objectMapper.readVariantAnnotation("13_g.32914438del.json"));
        mockData.put("16:g.9057113_9057114insCTG",
            this.objectMapper.readVariantAnnotation("16_g.9057113_9057114insCTG.json"));
        mockData.put("17:g.41276045_41276046del",
            this.objectMapper.readVariantAnnotation("17_g.41276045_41276046del.json"));
        mockData.put("17:g.41276046_41276047insG",
            this.objectMapper.readVariantAnnotation("17_g.41276046_41276047insG.json"));
        mockData.put("19:g.46141892_46141893delinsAA",
            this.objectMapper.readVariantAnnotation("19_g.46141892_46141893delinsAA.json"));
        mockData.put("22:g.29091840_29091841delinsCA",
            this.objectMapper.readVariantAnnotation("22_g.29091840_29091841delinsCA.json"));
        mockData.put("22:g.36689419_36689421del",
            this.objectMapper.readVariantAnnotation("22_g.36689419_36689421del.json"));
        mockData.put("X:g.41242962_41242963insGA",
            this.objectMapper.readVariantAnnotation("X_g.41242962_41242963insGA.json"));
        mockData.put("Y:g.41242962_41242963insGA",
            this.objectMapper.readVariantAnnotation("Y_g.41242962_41242963insGA.json"));
        mockData.put("4:g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT",
            this.objectMapper.readVariantAnnotation("4_g.55593656_55593657insCAACTTCCTTATGATCACAAATGGGAGTTTCCCAGAAACAGGCTGAGTTTTGGT.json"));
        mockData.put("13:g.48954120_48954122delinsAAA",
            this.objectMapper.readVariantAnnotation("13_g.48954120_48954122delinsAAA.json"));
        mockData.put("10:g.8100129_8100132delinsAACT",
            this.objectMapper.readVariantAnnotation("10_g.8100129_8100132delinsAACT.json"));
        return mockData;
    }
}
