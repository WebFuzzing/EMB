package nl.knaw.huygens.timbuctoo.v5.rdfio.implementations;

import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class BasicRdfPatchSerializerTest {
  private String result;
  BasicRdfPatchSerializer basicRdfPatchSerializer = new TestBasicRdfPatchSerializer(this::stringWriter,
    "http://example.org/graph");

  public void stringWriter(String string) {
    result = string;
  }

  @Test
  public void testAddValue() throws LogStorageFailedException {
    basicRdfPatchSerializer.onValue("http://example.org/show/218", "http://www.w3.org/2000/01/rdf-schema#label",
      "That Seventies Show",
      "http://www.w3.org/2001/XMLSchema#string", null);

    assertThat(result, is("+<http://example.org/show/218> <http://www.w3.org/2000/01/rdf-schema#label>" +
      " \"That Seventies Show\"^^<http://www.w3.org/2001/XMLSchema#string> <http://example.org/graph> .\n"));

    basicRdfPatchSerializer.onValue("http://en.wikipedia.org/wiki/Helium","http://example.org/elements/specificGravity",
      "1.663E-4","http://www.w3.org/2001/XMLSchema#double","http://example.org/testgraph");

    assertThat(result, is("+<http://en.wikipedia.org/wiki/Helium> <http://example.org/elements/specificGravity>" +
      " \"1.663E-4\"^^<http://www.w3.org/2001/XMLSchema#double> <http://example.org/testgraph> .\n"));

    basicRdfPatchSerializer.onValue("http://example.org/#spiderman","http://example.org/text",
      "This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('').", "http://www.w3.org/2001/XMLSchema#string",null);

    assertThat(result, is("+<http://example.org/#spiderman> <http://example.org/text> \"This is " +
      "a multi-line\\nliteral with many quotes (\\\"\\\"\\\"\\\"\\\")\\nand two apostrophes ('').\"^^<http://www.w3.org/2001/XMLSchema#string> <http://example.org/graph> .\n"));
  }

  @Test
  public void testAddLanguageTaggedValue() throws LogStorageFailedException {
    basicRdfPatchSerializer.onLanguageTaggedString("http://example.org/show/218",
      "http://example.org/show/localName", "That Seventies Show", "en", null);

    assertThat(result, is("+<http://example.org/show/218> <http://example.org/show/localName> " +
      "\"That Seventies Show\"@en <http://example.org/graph> .\n"));
  }

  @Test
  public void testDelValue() throws LogStorageFailedException {
    basicRdfPatchSerializer.delValue("http://example.org/show/218", "http://www.w3.org/2000/01/rdf-schema#label",
      "That Seventies Show",
      "http://www.w3.org/2001/XMLSchema#string", null);

    assertThat(result, is("-<http://example.org/show/218> <http://www.w3.org/2000/01/rdf-schema#label>" +
      " \"That Seventies Show\"^^<http://www.w3.org/2001/XMLSchema#string> <http://example.org/graph> .\n"));

    basicRdfPatchSerializer.delValue("http://en.wikipedia.org/wiki/Helium","http://example.org/elements/specificGravity",
      "1.663E-4","http://www.w3.org/2001/XMLSchema#double",null);

    assertThat(result, is("-<http://en.wikipedia.org/wiki/Helium> <http://example.org/elements/specificGravity>" +
      " \"1.663E-4\"^^<http://www.w3.org/2001/XMLSchema#double> <http://example.org/graph> .\n"));

    basicRdfPatchSerializer.delValue("http://example.org/#spiderman","http://example.org/text",
      "This is a multi-line\nliteral with many quotes (\"\"\"\"\")\nand two apostrophes ('').", "http://www.w3.org/2001/XMLSchema#string",null);

    assertThat(result, is("-<http://example.org/#spiderman> <http://example.org/text> \"This is " +
      "a multi-line\\nliteral with many quotes (\\\"\\\"\\\"\\\"\\\")\\nand two apostrophes ('').\"^^<http://www.w3.org/2001/XMLSchema#string> <http://example.org/graph> .\n"));
  }

  @Test
  public void delLanguageTaggedValue() throws LogStorageFailedException {
    basicRdfPatchSerializer.delLanguageTaggedString("http://example.org/show/218",
      "http://example.org/show/localName", "That Seventies Show", "en", null);

    assertThat(result, is("-<http://example.org/show/218> <http://example.org/show/localName> " +
      "\"That Seventies Show\"@en <http://example.org/graph> .\n"));
  }
}
