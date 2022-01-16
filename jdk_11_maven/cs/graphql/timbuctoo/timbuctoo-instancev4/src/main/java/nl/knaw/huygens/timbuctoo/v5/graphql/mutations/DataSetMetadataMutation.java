package nl.knaw.huygens.timbuctoo.v5.graphql.mutations;

import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.filestorage.exceptions.LogStorageFailedException;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ContextData;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DataSetWithDatabase;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation;
import nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.MutationOperation;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;

import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.MutationHelpers.addMutation;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.getOrCreate;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.replace;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.subject;
import static nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto.PredicateMutation.value;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.MARKDOWN;

public class DataSetMetadataMutation extends Mutation {
  private final DataSetRepository dataSetRepository;

  public DataSetMetadataMutation(Runnable schemaUpdater, DataSetRepository dataSetRepository) {
    super(schemaUpdater);
    this.dataSetRepository = dataSetRepository;
  }

  @Override
  public Object executeAction(DataFetchingEnvironment env) {
    DataSet dataSet = MutationHelpers.getDataSet(env, dataSetRepository::getDataSet);
    MutationHelpers.checkPermission(env, dataSet.getMetadata(),Permission.EDIT_DATASET_METADATA);

    try {
      Map md = env.getArgument("metadata");
      final String baseUri = dataSet.getMetadata().getBaseUri().endsWith("/") ?
        dataSet.getMetadata().getBaseUri() :
        dataSet.getMetadata().getBaseUri() + "/";
      addMutation(
        dataSet,
        new PredicateMutation()
          .entity(
            baseUri,
            this.<String>parseProp(md, "title", v -> replace("http://purl.org/dc/terms/title", value(v))),
            this.<String>parseProp(md, "description", v -> replace("http://purl.org/dc/terms/description", value(v,
              MARKDOWN))),
            this.<String>parseProp(md, "imageUrl", v -> replace("http://xmlns.com/foaf/0.1/depiction", value(v))),
            this.<Map>parseProp(md, "license", v -> this.<String>parseProp(v, "uri",
              uri -> replace("http://purl.org/dc/terms/license", subject(uri))
            )),
            this.<Map>parseProp(md, "owner", owner -> getOrCreate(
              "http://purl.org/dc/terms/rightsHolder",
              baseUri + "rightsHolder",
              this.<String>parseProp(owner, "name", v -> replace("http://schema.org/name", value(v))),
              this.<String>parseProp(owner, "email", v -> replace("http://schema.org/email", value(v)))
            )),
            this.<Map>parseProp(md, "contact", owner -> getOrCreate(
              "http://schema.org/ContactPoint",
              baseUri + "ContactPoint",
              this.<String>parseProp(owner, "name", v -> replace("http://schema.org/name", value(v))),
              this.<String>parseProp(owner, "email", v -> replace("http://schema.org/email", value(v)))
            )),
            this.<Map>parseProp(md, "provenanceInfo", owner -> getOrCreate(
              "http://purl.org/dc/terms/provenance",
              baseUri + "Provenance",
              this.<String>parseProp(owner, "title", v -> replace("http://purl.org/dc/terms/title", value(v))),
              this.<String>parseProp(owner, "body", v -> replace("http://purl.org/dc/terms/description", value(v,
                MARKDOWN)))
            ))
          )
      );

      return new DataSetWithDatabase(dataSet, env.<ContextData>getContext().getUserPermissionCheck());
    } catch (LogStorageFailedException | InterruptedException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  private <T> MutationOperation parseProp(Map metadata, String property, Function<T, MutationOperation> builder) {
    if (metadata.containsKey(property)) {
      T value = (T) metadata.get(property); // you might want to catch and log the class cast exception
      return builder.apply(value);
    } else {
      return null;
    }
  }

}


