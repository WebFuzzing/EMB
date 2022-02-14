package nl.knaw.huygens.timbuctoo.crud;

import nl.knaw.huygens.timbuctoo.model.vre.vres.VresBuilder;
import org.junit.Test;

public class JsonCrudServiceTest {

  @Test(expected = InvalidCollectionException.class)
  public void createThrowsAnInvalidCollectionExceptionWhenTheCollectionIsUnknown() throws Exception {
    JsonCrudService instance = createInstanceWithoutKnownCollections();

    instance.create("unknown_collection", null, null);
  }

  @Test(expected = InvalidCollectionException.class)
  public void deleteThrowsAnInvalidCollectionExceptionWhenTheCollectionIsUnknown() throws Exception {
    JsonCrudService instance = createInstanceWithoutKnownCollections();

    instance.delete("unknown_collection", null, null);
  }

  @Test(expected = InvalidCollectionException.class)
  public void getThrowsAnInvalidCollectionExceptionWhenTheCollectionIsUnknown() throws Exception {
    JsonCrudService instance = createInstanceWithoutKnownCollections();

    instance.get("unknown_collection", null);
  }

  @Test(expected = InvalidCollectionException.class)
  public void replaceThrowsAnInvalidCollectionExceptionWhenTheCollectionIsUnknown() throws Exception {
    JsonCrudService instance = createInstanceWithoutKnownCollections();

    instance.replace("unknown_collection", null, null, null);
  }

  private JsonCrudService createInstanceWithoutKnownCollections() {
    return new JsonCrudService(new VresBuilder().build(), null, null, null);
  }
}
