package nl.knaw.huygens.timbuctoo.v5.graphql.customschema;

import nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto.ExplicitField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MergeExplicitSchemas {
  public MergeExplicitSchemas() {

  }

  public Map<String, List<ExplicitField>> mergeExplicitSchemas(Map<String, List<ExplicitField>> explicitSchema1,
                                                               Map<String, List<ExplicitField>> explicitSchema2) {
    Map<String, List<ExplicitField>> mergedExplicitSchema = new HashMap<>();

    for (Map.Entry<String, List<ExplicitField>> entry : explicitSchema1.entrySet()) {
      mergedExplicitSchema.put(entry.getKey(),entry.getValue());
    }

    explicitSchema2.forEach((collection, values) -> {
      if (mergedExplicitSchema.containsKey(collection)) {
        List<ExplicitField> mergedValues = new ArrayList<>();

        for (ExplicitField value : values) {
          mergedValues.add(findAndMergeExplicitFields(collection, value, mergedExplicitSchema));
        }

        for (ExplicitField value : mergedExplicitSchema.get(collection)) {
          boolean addValue = true;
          for (ExplicitField mergedValue : mergedValues) {
            if (mergedValue.getUri().equals(value.getUri())) {
              addValue = false;
              break;
            }
          }

          if (addValue) {
            mergedValues.add(value);
          }
        }

        mergedExplicitSchema.put(collection, mergedValues);
      } else {
        mergedExplicitSchema.put(collection, values);
      }

    });


    return mergedExplicitSchema;
  }

  private ExplicitField findAndMergeExplicitFields(String collection, ExplicitField explicitField1,
                                                   Map<String, List<ExplicitField>> explicitSchema) {
    List<ExplicitField> explicitFields = explicitSchema.get(collection);
    ExplicitField explicitField2 = null;
    ExplicitField mergedField;

    for (ExplicitField explicitField : explicitFields) {
      if (explicitField.getUri().equals(explicitField1.getUri())) {
        explicitField2 = explicitField;
        break;
      }
    }

    if (explicitField2 != null && explicitField1.getUri().equals(explicitField2.getUri())) {
      mergedField = explicitField1.mergeWith(explicitField2);
    } else {
      mergedField = explicitField1;
    }

    return mergedField;
  }
}
