package nl.knaw.huygens.timbuctoo.v5.datastores.implementations.bdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.berkeleydb.exceptions.DatabaseWriteException;
import nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore.TypeNameStore;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.jacksonserializers.TimbuctooCustomSerializers;
import nl.knaw.huygens.timbuctoo.v5.util.RdfConstants;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.shared.impl.PrefixMappingImpl;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class BdbTypeNameStore implements TypeNameStore {

  private static ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new Jdk8Module())
    .registerModule(new GuavaModule())
    .registerModule(new TimbuctooCustomSerializers())
    .enable(SerializationFeature.INDENT_OUTPUT);

  protected final PrefixMapping prefixMapping;
  protected final TypeNames data;
  private final DataStorage dataStore;
  private final String dataStoreRdfPrefix;

  public BdbTypeNameStore(DataStorage dataStore, String dataStoreRdfPrefix) throws IOException {
    this.dataStoreRdfPrefix = dataStoreRdfPrefix;
    prefixMapping = new PrefixMappingImpl();
    final String storedValue = dataStore.getValue();
    if (storedValue == null) {
      data = new TypeNames();
      addStandardPrefixes();
    } else {
      data = objectMapper.readValue(storedValue, new TypeReference<TypeNames>() {});
    }
    prefixMapping.setNsPrefixes(data.prefixes);
    this.dataStore = dataStore;
  }

  //I think that a fully reversable shortened version looks ugly. And usually this is not needed
  //So I shorten by throwing away information and use a HashMap to be able to revert the process
  //and prevent collisions.
  @Override
  public String makeGraphQlname(String uri) {
    return makeName(uri, "");
  }

  @Override
  public String makeGraphQlnameForPredicate(String uri, Direction direction, boolean isList) {
    return makeName(uri, direction == Direction.IN ? "_inverse_" : "") + (isList ? "List" : "");
  }

  public String makeName(String uri, String prefix) {
    //The relay spec requires that our own names are never 'PageInfo' or end with 'Connection'

    if (data.shorteneds.containsKey(prefix + "\n" + uri)) {
      return data.shorteneds.get(prefix + "\n" + uri);
    } else {
      String shortened = prefix + shorten(uri).replaceAll("[^_0-9A-Za-z]", "_");
      while (shortened.equals("PageInfo") ||
        shortened.endsWith("Connection") ||
        shortened.endsWith("Edge") ||
        data.inverse.containsKey(shortened)) {

        shortened += "_";
      }
      data.shorteneds.put(prefix + "\n" + uri, shortened);
      data.inverse.put(shortened, uri);
      return shortened;
    }
  }

  @Override
  public String makeGraphQlValuename(String uri) {
    return makeName(uri, "value_");
  }

  @Override
  public String makeUri(String graphQlName) {
    return data.inverse.get(graphQlName);
  }

  @Override
  public Optional<Tuple<String, Direction>> makeUriForPredicate(String graphQlName) {
    String uri = makeUri(graphQlName);
    // check if the graphQlName is of a List property.
    if (uri == null && graphQlName.endsWith("List")) {
      uri = makeUri(graphQlName.replace("List", ""));
    }
    if (uri == null) {
      return Optional.empty();
    } else {
      if (graphQlName.startsWith("_inverse_")) {
        return Optional.of(Tuple.tuple(uri, Direction.IN));
      } else {
        return Optional.of(Tuple.tuple(uri, Direction.OUT));
      }
    }
  }

  @Override
  public String shorten(String uri) {
    return prefixMapping.shortForm(uri);
  }

  @Override
  public Map<String, String> getMappings() {
    return prefixMapping.getNsPrefixMap();
  }

  @Override
  public boolean isClean() {
    return dataStore.isClean();
  }

  public void addPrefix(String prefix, String iri) {
    data.prefixes.put(prefix, iri);
    prefixMapping.setNsPrefix(prefix, iri); //idempotent
  }

  public void commit() throws JsonProcessingException, DatabaseWriteException {
    dataStore.setValue(objectMapper.writeValueAsString(data));
    dataStore.commit();
  }

  @Override
  public void close() throws IOException {
    try {
      commit();
      dataStore.close();
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  public void addStandardPrefixes() {
    addPrefix("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
    addPrefix("foaf", "http://xmlns.com/foaf/0.1/");
    addPrefix("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
    addPrefix("owl", "http://www.w3.org/2002/07/owl#");
    addPrefix("skos", "http://www.w3.org/2004/02/skos/core#");
    addPrefix("dcat", "http://www.w3.org/ns/dcat#");
    addPrefix("xsd", "http://www.w3.org/2001/XMLSchema#");
    addPrefix("prov", "http://www.w3.org/ns/prov#");
    addPrefix("dcterms", "http://purl.org/dc/terms/");
    addPrefix("dbpedia", "http://dbpedia.org/resource/");
    addPrefix("schema", "http://schema.org/");
    addPrefix("tim", RdfConstants.TIM_VOCAB);
    addPrefix("tim_col", RdfConstants.TIM_COL);
    addPrefix("tim_pred", RdfConstants.TIM_PRED);
    addPrefix("tim_type", RdfConstants.TIM_TYPE);
    addPrefix("local_pred", this.dataStoreRdfPrefix + "/predicate/");
    addPrefix("local_type", this.dataStoreRdfPrefix + "/datatype/");
    addPrefix("local_col", this.dataStoreRdfPrefix + "/collection/");
  }

  public void start() {
    dataStore.beginTransaction();
  }

  public void empty() {
    dataStore.empty();
  }
}

