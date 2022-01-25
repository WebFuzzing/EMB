package nl.knaw.huygens.timbuctoo.v5.rml;

import nl.knaw.huygens.timbuctoo.rml.DataSource;
import nl.knaw.huygens.timbuctoo.rml.datasource.joinhandlers.HashMapBasedJoinHandler;
import nl.knaw.huygens.timbuctoo.rml.rdfshim.RdfResource;
import nl.knaw.huygens.timbuctoo.rml.datasource.jexl.JexlRowFactory;
import nl.knaw.huygens.timbuctoo.v5.datastores.rmldatasource.RmlDataSourceStore;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class RdfDataSourceFactory {
  private static final String NS_RML = "http://semweb.mmlab.be/ns/rml#";
  private final RmlDataSourceStore rmlDataSourceStore;

  public RdfDataSourceFactory(RmlDataSourceStore rmlDataSourceStore) {
    this.rmlDataSourceStore = rmlDataSourceStore;
  }

  public Optional<DataSource> apply(RdfResource rdfResource) {
    for (RdfResource resource : rdfResource.out(NS_RML + "source")) {
      Set<RdfResource> rawCollection = resource.out("http://timbuctoo.huygens.knaw.nl/mapping#rawCollectionUri");
      Set<RdfResource> customFields = resource.out("http://timbuctoo.huygens.knaw.nl/mapping#customField");

      Map<String, String> expressions = new HashMap<>();
      for (RdfResource customField : customFields) {
        Set<RdfResource> fieldNameResource = customField.out("http://timbuctoo.huygens.knaw.nl/mapping#name");
        Set<RdfResource> fieldValueResource = customField.out("http://timbuctoo.huygens.knaw.nl/mapping#expression");
        fieldNameResource.iterator().next().asLiteral().ifPresent(fieldName -> {
          fieldValueResource.iterator().next().asLiteral().ifPresent(fieldValue -> {
            expressions.put(fieldName.getValue(), fieldValue.getValue());
          });
        });
      }


      if (rawCollection.size() == 1) {
        return rawCollection.iterator().next().asIri().map(collectionIri -> new RdfDataSource(
          rmlDataSourceStore,
          collectionIri,
          new JexlRowFactory(expressions, new HashMapBasedJoinHandler())
        ));
      }
    }
    return Optional.empty();
  }
}
