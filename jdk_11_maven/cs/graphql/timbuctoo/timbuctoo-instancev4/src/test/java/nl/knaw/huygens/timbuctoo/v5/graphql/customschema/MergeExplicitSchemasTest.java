package nl.knaw.huygens.timbuctoo.v5.graphql.customschema;


import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb.ExplicitFieldMatcher;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;
import nl.knaw.huygens.timbuctoo.v5.graphql.customschema.MergeExplicitSchemas;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;


public class MergeExplicitSchemasTest {
  @Test
  public void mergeeExplicitSchemaMergesSchemasWithDifferentCollections() throws Exception {
    Map<String, List<ExplicitField>> explicitSchema1 = new HashMap<>();
    ExplicitField explicitField1 = new ExplicitField("test:test1", false, Sets.newHashSet("String"), null);
    explicitSchema1.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places", Lists.newArrayList(explicitField1));
    Map<String, List<ExplicitField>> explicitSchema2 = new HashMap<>();
    ExplicitField explicitField2 = new ExplicitField("test:test2", false, null, Sets.newHashSet("String"));
    explicitSchema2.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons", Lists.newArrayList(explicitField2));
    MergeExplicitSchemas mergeExplicitSchemas = new MergeExplicitSchemas();

    Map<String, List<ExplicitField>> mergedExplicitSchema = mergeExplicitSchemas.mergeExplicitSchemas(
      explicitSchema1,
      explicitSchema2
    );

    Map<String, List<ExplicitField>> expectedMergedSchema = new HashMap<>();
    expectedMergedSchema.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places",
      Lists.newArrayList(explicitField1));
    expectedMergedSchema.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Persons",
      Lists.newArrayList(explicitField2));

    assertThat(mergedExplicitSchema, is(expectedMergedSchema));
  }

  @Test
  public void mergeExplicitSchemaMergesFieldsForSameCollection() throws Exception {
    Map<String, List<ExplicitField>> explicitSchema1 = new HashMap<>();
    ExplicitField explicitField1 = new ExplicitField("test:test1", false, Sets.newHashSet("String"), null);
    explicitSchema1.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places", Lists.newArrayList(explicitField1));
    Map<String, List<ExplicitField>> explicitSchema2 = new HashMap<>();
    ExplicitField explicitField2 = new ExplicitField("test:test2", false, null, Sets.newHashSet("String"));
    explicitSchema2.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places", Lists.newArrayList(explicitField2));
    MergeExplicitSchemas mergeExplicitSchemas = new MergeExplicitSchemas();

    Map<String, List<ExplicitField>> mergedExplicitSchema = mergeExplicitSchemas.mergeExplicitSchemas(
      explicitSchema1,
      explicitSchema2
    );

    assertThat(mergedExplicitSchema, hasKey("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places"));
    assertThat(mergedExplicitSchema.get("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places"),
      containsInAnyOrder(explicitField1, explicitField2));
  }

  @Test
  public void mergeExplicitSchemaMergesFieldWithSameUriForSameCollection() throws Exception {
    Map<String, List<ExplicitField>> explicitSchema1 = new HashMap<>();
    ExplicitField explicitField1 = new ExplicitField("test:test1", false, Sets.newHashSet("String"), null);
    explicitSchema1.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places", Lists.newArrayList(explicitField1));
    Map<String, List<ExplicitField>> explicitSchema2 = new HashMap<>();
    ExplicitField explicitField2 = new ExplicitField("test:test1", false, Sets.newHashSet("Integer"), null);
    explicitSchema2.put("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places", Lists.newArrayList(explicitField2));
    MergeExplicitSchemas mergeExplicitSchemas = new MergeExplicitSchemas();

    Map<String, List<ExplicitField>> mergedExplicitSchema = mergeExplicitSchemas.mergeExplicitSchemas(
      explicitSchema1,
      explicitSchema2
    );

    assertThat(mergedExplicitSchema, hasKey("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places"));
    MatcherAssert.assertThat(mergedExplicitSchema.get("http://timbuctoo.huygens.knaw.nl/datasets/clusius/Places"),
      Matchers.contains(ExplicitFieldMatcher.explicitField().withValues(Sets.newHashSet("Integer", "String"))));
  }
}
