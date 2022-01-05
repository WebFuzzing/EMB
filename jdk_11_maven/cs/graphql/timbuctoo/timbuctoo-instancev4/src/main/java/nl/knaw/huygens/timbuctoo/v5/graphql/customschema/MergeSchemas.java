package nl.knaw.huygens.timbuctoo.v5.graphql.customschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Predicate;
import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.Type;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class MergeSchemas {

  public MergeSchemas() {
  }

  public Map<String, Type> mergeSchema(Map<String, Type> generatedSchema, Map<String, Type> customSchema) {
    Map<String, Type> mergedSchema = generatedSchema;

    Collection<Predicate> mergedPredicates;

    for (Map.Entry<String, Type> entry : customSchema.entrySet()) {
      mergedPredicates = new HashSet<>();

      if (mergedSchema.get(entry.getKey()) != null) {
        for (Predicate customPredicate : entry.getValue().getPredicates()) {
          if (mergedSchema.get(entry.getKey())
            .getPredicate(customPredicate.getName(), customPredicate.getDirection()) != null) {
            Predicate generatedPredicate = mergedSchema.get(entry.getKey())
              .getPredicate(customPredicate.getName(), customPredicate.getDirection());
            Predicate mergedPredicate = generatedPredicate.merge(customPredicate);
            mergedPredicate.setIsExplicit(true);
            mergedPredicates.add(mergedPredicate);
          } else {
            mergedPredicates.add(customPredicate);
          }
        }
        mergedSchema.get(entry.getKey()).setPredicates(mergedPredicates);
      } else {
        mergedSchema.put(entry.getKey(), entry.getValue());
      }
    }

    return mergedSchema;
  }
}
