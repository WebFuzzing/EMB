package nl.knaw.huygens.timbuctoo.core;

import nl.knaw.huygens.timbuctoo.model.vre.Vre;
import nl.knaw.huygens.timbuctoo.model.vre.VreStubs;
import org.junit.Test;

import static nl.knaw.huygens.timbuctoo.core.CollectionNameHelper.RDF_URI_PREFIX;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;

public class CollectionNameHelperTest {

  public static final String ADMIN_VRE = "Admin";
  public static final String UNPREFIXED_ENTITY_NAME = "person";
  private static final String VRE_NAME = "vreName";

  @Test
  public void collectionNamePrefixesTheCollectionNameWithTheVreName() {
    Vre vre = VreStubs.withName(VRE_NAME);

    String collectionName = CollectionNameHelper.collectionName(UNPREFIXED_ENTITY_NAME, vre);

    assertThat(collectionName, allOf(startsWith(VRE_NAME), containsString(UNPREFIXED_ENTITY_NAME)));
  }

  @Test
  public void collectionNameDoesNotPrefixForTheAdminVre() {
    Vre vre = VreStubs.withName(ADMIN_VRE);

    String collectionName = CollectionNameHelper.collectionName(UNPREFIXED_ENTITY_NAME, vre);

    assertThat(collectionName, not(startsWith(ADMIN_VRE)));
  }

  @Test
  public void collectionNameDoesPostfixesTheNameWithAnS() {
    Vre vre = VreStubs.withName(ADMIN_VRE);

    String collectionName = CollectionNameHelper.collectionName(UNPREFIXED_ENTITY_NAME, vre);

    assertThat(collectionName, endsWith("s"));
  }

  @Test
  public void entityTypeNamePrefixesWithTheVreName() {
    Vre vre = VreStubs.withName(VRE_NAME);

    String entityTypeName = CollectionNameHelper.entityTypeName(UNPREFIXED_ENTITY_NAME, vre);

    assertThat(entityTypeName, allOf(startsWith(VRE_NAME), endsWith(UNPREFIXED_ENTITY_NAME)));
  }

  @Test
  public void entityTypeNameDoesNotPrefixForTheAdminVre() {
    Vre vre = VreStubs.withName(ADMIN_VRE);

    String entityTypeName = CollectionNameHelper.entityTypeName(UNPREFIXED_ENTITY_NAME, vre);

    assertThat(entityTypeName, not(startsWith(ADMIN_VRE)));
  }

  @Test
  public void rdfUriPrefixesTheEntityTypeNameWithTheDefaultUri() {
    Vre vre = VreStubs.withName(VRE_NAME);

    String rdfUri = CollectionNameHelper.rdfUri(UNPREFIXED_ENTITY_NAME, vre);

    String entityTypeName = CollectionNameHelper.entityTypeName(UNPREFIXED_ENTITY_NAME, vre);
    assertThat(rdfUri, allOf(startsWith(RDF_URI_PREFIX), endsWith(entityTypeName)));
  }

}
