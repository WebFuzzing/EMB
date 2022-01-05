package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations;

import java.util.function.Consumer;

public class TestBasicRdfPatchSerializer extends BasicRdfPatchSerializer  {
  public TestBasicRdfPatchSerializer(Consumer<String> printWriter, String defaultGraph) {
    super(printWriter, defaultGraph);
  }
}
