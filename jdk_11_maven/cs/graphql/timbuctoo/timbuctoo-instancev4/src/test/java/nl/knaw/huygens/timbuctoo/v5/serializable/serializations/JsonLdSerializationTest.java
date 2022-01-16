package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import org.junit.Test;

import java.io.ByteArrayOutputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class JsonLdSerializationTest {

  @Test
  public void performSerialization() throws Exception {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    JsonLdSerialization cs = new JsonLdSerialization(out);

    cs.serialize(SourceData.simpleResult());

    assertThat(out.toString(), is("{\n" +
      "  \"data\" : {\n" +
      "    \"Persons\" : {\n" +
      "      \"items\" : [ {\n" +
      "        \"@id\" : \"http://example.com/1\",\n" +
      "        \"@type\" : \"http://example.com/Person\",\n" +
      "        \"a\" : {\n" +
      "          \"type\" : \"http://www.w3.org/2001/XMLSchema#int\",\n" +
      "          \"value\" : \"1\"\n" +
      "        },\n" +
      "        \"b\" : {\n" +
      "          \"prevCursor\" : \"next\",\n" +
      "          \"items\" : [ {\n" +
      "            \"@id\" : \"http://example.com/11\",\n" +
      "            \"@type\" : \"http://example.com/SubItem\",\n" +
      "            \"c\" : {\n" +
      "              \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "              \"value\" : \"2\"\n" +
      "            },\n" +
      "            \"d\" : {\n" +
      "              \"items\" : [ {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"3\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"4\"\n" +
      "              }, null ]\n" +
      "            }\n" +
      "          }, {\n" +
      "            \"@id\" : \"http://example.com/12\",\n" +
      "            \"@type\" : \"http://example.com/SubItem\",\n" +
      "            \"c\" : {\n" +
      "              \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "              \"value\" : \"5\"\n" +
      "            },\n" +
      "            \"d\" : {\n" +
      "              \"items\" : [ {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"6\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"7\"\n" +
      "              } ]\n" +
      "            }\n" +
      "          } ]\n" +
      "        }\n" +
      "      }, {\n" +
      "        \"@id\" : \"http://example.com/2\",\n" +
      "        \"@type\" : \"http://example.com/Person\",\n" +
      "        \"a\" : {\n" +
      "          \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "          \"value\" : \"8\"\n" +
      "        },\n" +
      "        \"b\" : {\n" +
      "          \"items\" : [ {\n" +
      "            \"@id\" : \"http://example.com/11\",\n" +
      "            \"@type\" : \"http://example.com/SubItem\",\n" +
      "            \"c\" : {\n" +
      "              \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "              \"value\" : \"9\"\n" +
      "            },\n" +
      "            \"d\" : {\n" +
      "              \"items\" : [ {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#int\",\n" +
      "                \"value\" : \"10\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"11\"\n" +
      "              } ]\n" +
      "            }\n" +
      "          }, {\n" +
      "            \"@id\" : \"http://example.com/12\",\n" +
      "            \"@type\" : \"http://example.com/SubItem\",\n" +
      "            \"c\" : {\n" +
      "              \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "              \"value\" : \"12\"\n" +
      "            },\n" +
      "            \"d\" : {\n" +
      "              \"items\" : [ {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#double\",\n" +
      "                \"value\" : \"13.0\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"14\"\n" +
      "              } ]\n" +
      "            }\n" +
      "          }, {\n" +
      "            \"@id\" : \"http://example.com/13\",\n" +
      "            \"@type\" : \"http://example.com/SubItem\",\n" +
      "            \"c\" : {\n" +
      "              \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "              \"value\" : \"15\"\n" +
      "            },\n" +
      "            \"d\" : {\n" +
      "              \"items\" : [ {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"16\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"17\"\n" +
      "              }, {\n" +
      "                \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "                \"value\" : \"18\"\n" +
      "              } ]\n" +
      "            }\n" +
      "          } ]\n" +
      "        }\n" +
      "      }, {\n" +
      "        \"@id\" : \"http://example.com/3\",\n" +
      "        \"@type\" : \"http://example.com/Person\",\n" +
      "        \"a\" : {\n" +
      "          \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "          \"value\" : \"19\"\n" +
      "        },\n" +
      "        \"b\" : {\n" +
      "          \"@id\" : \"http://example.com/21\",\n" +
      "          \"@type\" : \"http://example.com/OtherSubItem\",\n" +
      "          \"e\" : {\n" +
      "            \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "            \"value\" : \"20\"\n" +
      "          },\n" +
      "          \"f\" : {\n" +
      "            \"type\" : \"http://www.w3.org/2001/XMLSchema#string\",\n" +
      "            \"value\" : \"21\"\n" +
      "          }\n" +
      "        }\n" +
      "      } ]\n" +
      "    }\n" +
      "  },\n" +
      "  \"@context\" : {\n" +
      "    \"data\" : {\n" +
      "      \"@id\" : \"@graph\",\n" +
      "      \"@container\" : \"@index\"\n" +
      "    },\n" +
      "    \"value\" : \"@value\",\n" +
      "    \"type\" : \"@type\",\n" +
      "    \"a\" : \"http://example.org/b\",\n" +
      "    \"b\" : {\n" +
      "      \"@reverse\" : \"http://example.org/b\"\n" +
      "    },\n" +
      "    \"c\" : \"http://example.org/c\",\n" +
      "    \"d\" : \"http://example.org/d\",\n" +
      "    \"a\" : \"http://example.org/a\",\n" +
      "    \"b\" : \"http://example.org/b\",\n" +
      "    \"e\" : \"http://example.org/e\",\n" +
      "    \"f\" : \"http://example.org/f\"\n" +
      "  }\n" +
      "}"));
  }

}
