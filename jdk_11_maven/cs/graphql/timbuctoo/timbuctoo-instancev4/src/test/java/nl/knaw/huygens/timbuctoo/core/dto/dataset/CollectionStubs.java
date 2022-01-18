package nl.knaw.huygens.timbuctoo.core.dto.dataset;

import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.properties.ReadableProperty;
import nl.knaw.huygens.timbuctoo.model.vre.Vre;

import java.util.LinkedHashMap;

import static org.mockito.Mockito.mock;

public class CollectionStubs {

  public static Collection forEntitytypeName(String vrename, String entitytypename) {
    return new Collection(
      vrename + entitytypename,
      "",
      mock(ReadableProperty.class),
      new LinkedHashMap<>(),
      vrename + entitytypename + "s",
      mock(Vre.class),
      vrename + entitytypename,
      false,
      false,
      null);
  }

  public static Collection collWithCollectionName(String collectionName) {
    // TODO find a better way to create a collection for a test
    return new Collection(null, collectionName, null, Maps.newLinkedHashMap(), collectionName, null, null, false,
      false, null);
  }

  public static Collection keywordCollWithCollectionName(String collectionName) {
    return new Collection(null, "keyword", null, Maps.newLinkedHashMap(), collectionName, null, null, false, false,
      null);
  }
}
