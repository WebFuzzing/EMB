package nl.knaw.huygens.timbuctoo.v5.util;

import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;

import java.util.UUID;

import static javax.ws.rs.core.UriBuilder.fromUri;

public class RdfConstants {

  private static final String TIM = "http://timbuctoo.huygens.knaw.nl/static/v5/";
  public static final String TIM_VOCAB = TIM + "vocabulary#"; // use for timbuctoo internal vocabulary
  public static final String TIM_PROP_DESC = TIM + "propertyDescription/";
  public static final String TIM_PROVENANCE_ENTITIES = TIM + "provenanceEntities/";
  public static final String TIM_TABULAR_FILE = TIM + "types#tabularFile";
  public static final String TIM_TABULAR_COLLECTION = TIM + "types#tabularCollection";
  public static final String TIM_SKOLEMIZE = TIM + "skolemized/";
  public static final String TIM_USERS = TIM + "users/";

  // prefixes for datasets
  public static final String TIM_COL = TIM + "collection/";
  public static final String TIM_PRED = TIM + "predicate/";
  public static final String TIM_TYPE = TIM + "datatype/";

  public static final String TIM_JSONLD_UPLOAD_CONTEXT = TIM + "jsonLdUploadContext.json";
  // Vocabulary internal
  public static final String TIM_COLLECTION = TIM_VOCAB + "collection";
  public static final String TIM_LATEST_REVISION = TIM_VOCAB + "latestRevision";
  public static final String TIM_SPECIALIZATION_OF = TIM_VOCAB + "specialization";
  public static final String TIMBUCTOO_NEXT = TIM_VOCAB + "next";
  public static final String TIM_HASCOLLECTION = TIM_VOCAB + "hasCollection";
  public static final String UNKNOWN = TIM_VOCAB + "unknown";
  public static final String TIM_PROP_NAME = TIM_VOCAB + "timpropname";
  public static final String TIM_HAS_ROW = TIM_VOCAB + "timhasrow";
  public static final String TIM_HAS_PROPERTY = TIM_VOCAB + "hasProperty";
  public static final String TIM_PROP_ID = TIM_VOCAB + "propertyId";
  public static final String TIM_MIMETYPE = TIM_VOCAB + "mimetype";
  public static final String TIM_HASCOLOR = TIM_VOCAB + "hasColor";
  public static final String TIM_EDITOR = TIM_VOCAB + "editor";
  public static final String TIM_HASINDEXERCONFIG = TIM_VOCAB + "hasIndexConfig";
  public static final String TIM_HASFULLTEXTSEARCH = TIM_VOCAB + "hasFullTextSearch";
  public static final String TIM_HASFACETPATH = TIM_VOCAB + "hasFacetPath";
  public static final String TIM_NEXTFACET = TIM_VOCAB + "nextFacet";
  public static final String TIM_SUMMARYTITLEPREDICATE = TIM_VOCAB + "summaryTitlePredicate";
  public static final String TIM_SUMMARYDESCRIPTIONPREDICATE = TIM_VOCAB + "summaryDescriptionPredicate";
  public static final String TIM_SUMMARYIMAGEPREDICATE = TIM_VOCAB + "summaryImagePredicate";
  public static final String HAS_EDIT_CONFIG = TIM_VOCAB + "hasEditConfig";
  public static final String HAS_VIEW_CONFIG = TIM_VOCAB + "hasViewConfig";

  public static final String PROV_DERIVED_FROM = "http://www.w3.org/ns/prov#wasDerivedFrom";
  private static final String PROV_BASE = "http://www.w3.org/ns/prov#";
  public static final String PROV_ATTIME = PROV_BASE + "atTime";

  public static final String RDF_TYPE = "http://www.w3.org/1999/02/22-rdf-syntax-ns#type";
  public static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
  public static final String LANGSTRING = "http://www.w3.org/1999/02/22-rdf-syntax-ns#langString";
  public static final String STRING = "http://www.w3.org/2001/XMLSchema#string";
  public static final String XSD_DATETIMESTAMP = "http://www.w3.org/2001/XMLSchema#dateTimeStamp";
  public static final String XSD_BOOLEAN = "http://www.w3.org/2001/XMLSchema#boolean";
  public static final String INTEGER = "http://www.w3.org/2001/XMLSchema#integer";
  public static final String URI = "http://www.w3.org/2001/XMLSchema#anyURI";
  public static final String MARKDOWN = "https://daringfireball.net/projects/markdown/syntax";


  // helper methods

  public static String timPredicate(String name) {
    return TIM_PRED + name;
  }

  public static String timType(String name) {
    return TIM_VOCAB + name;
  }

  public static String dataSetObjectUri(DataSet dataSet, String typeName) {
    return fromUri(dataSet.getMetadata().getBaseUri()).path(typeName).path(UUID.randomUUID().toString()).toString();
  }

  public static boolean isProvenance(String iri) {
    return iri.startsWith(PROV_BASE);
  }
}
