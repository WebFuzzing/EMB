package nl.knaw.huygens.timbuctoo.v5.graphql.customschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.PredicateMatcher.predicate;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;

public class MergeSchemasTest {
  @Test
  public void mergeSchemaAddsCustomTypeToSchema() throws Exception {
    MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> customSchema = new HashMap<>();
    customSchema.put("CustomType", new Type("TypeName"));

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(new HashMap<>(), customSchema);

    assertThat(mergedSchema, hasEntry(is("CustomType"), hasProperty("name", is("TypeName"))));
  }

  @Test
  public void mergeSchemaAddsGeneratedTypeToSchema() throws Exception {
    MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> generatedSchema = new HashMap<>();
    generatedSchema.put("GeneratedType", new Type("TypeName"));

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(generatedSchema, new HashMap<>());

    assertThat(mergedSchema, hasEntry(is("GeneratedType"), hasProperty("name", is("TypeName"))));
  }


  @Test
  public void mergeSchemaReturnsMergedSchemeWithAllPredicates() throws Exception {
    MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> generatedSchema = new HashMap<>();
    generatedSchema.put("Type", createTypeWithPredicate("generated", Direction.OUT));
    Map<String, Type> customSchema = new HashMap<>();
    customSchema.put("Type 2", createTypeWithPredicate("custom", Direction.IN));

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(generatedSchema, customSchema);

    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", contains(
      predicate().withName("generated").withDirection(Direction.OUT)
    ))));

    assertThat(mergedSchema, hasEntry(is("Type 2"), hasProperty("predicates", contains(
      predicate().withName("custom").withDirection(Direction.IN)
    ))));
  }


  @Test
  public void mergeSchemaReturnsMergedSchemaWithAllPredicatesForSingleType() throws Exception {
    MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> generatedSchema = new HashMap<>();
    generatedSchema.put("Type", createTypeWithPredicate("generated", Direction.OUT));
    Map<String, Type> customSchema = new HashMap<>();
    customSchema.put("Type", createTypeWithPredicate("custom", Direction.IN));

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(generatedSchema, customSchema);

    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", containsInAnyOrder(
      predicate().withName("generated").withDirection(Direction.OUT),
      predicate().withName("custom").withDirection(Direction.IN)
    ))));
  }

  @Test
  public void mergeSchemaMergesMatchingPredicates() throws Exception {
    final MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> generatedSchema = new HashMap<>();
    Type predType1 = createTypeWithPredicate("generated", Direction.IN);
    predType1.getPredicate("generated", Direction.IN).setHasBeenList(true);
    predType1.getPredicate("generated", Direction.IN).setOwner(new Type("testOwner"));
    generatedSchema.put("Type", predType1);
    Map<String, Type> customSchema = new HashMap<>();
    Type predType2 = createTypeWithPredicate("generated", Direction.IN);
    predType2.getPredicate("generated", Direction.IN).setHasBeenList(false);
    predType2.getPredicate("generated", Direction.IN).setOwner(new Type("testOwner"));
    customSchema.put("Type", predType2);

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(generatedSchema, customSchema);

    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", contains(
      predicate().withName("generated").withDirection(Direction.IN).withWasList(true)
    ))));
    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", not(hasItem(
      predicate().withName("generated").withDirection(Direction.IN).withWasList(false))
    ))));
  }

  @Test
  public void explicitPropertyIsMaintainedInMergedPredicates() throws Exception {
    final MergeSchemas mergeSchemas = new MergeSchemas();
    Map<String, Type> generatedSchema = new HashMap<>();
    Type predType1 = createTypeWithPredicate("generated", Direction.IN);
    predType1.getPredicate("generated", Direction.IN).setHasBeenList(true);
    predType1.getPredicate("generated", Direction.IN).setOwner(new Type("testOwner"));
    generatedSchema.put("Type", predType1);
    Map<String, Type> customSchema = new HashMap<>();
    Type predType2 = createTypeWithPredicate("generated", Direction.IN);
    predType2.getPredicate("generated", Direction.IN).setHasBeenList(false);
    predType2.getPredicate("generated", Direction.IN).setOwner(new Type("testOwner"));
    customSchema.put("Type", predType2);

    Map<String, Type> mergedSchema = mergeSchemas.mergeSchema(generatedSchema, customSchema);

    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", contains(
      predicate().withName("generated").withDirection(Direction.IN).withWasList(true).withIsExplicit(true)
    ))));
    assertThat(mergedSchema, hasEntry(is("Type"), hasProperty("predicates", not(hasItem(
      predicate().withName("generated").withDirection(Direction.IN).withWasList(false))
    ))));
  }

  private Type createTypeWithPredicate(String generated, Direction direction) {
    Type generatedType = new Type("");
    generatedType.getOrCreatePredicate(generated, direction);
    generatedType.getPredicate(generated,direction).setIsExplicit(true);
    return generatedType;
  }

}
