package nl.knaw.huygens.timbuctoo.v5.datastores.prefixstore;

import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;

import java.util.Map;
import java.util.Optional;

public interface TypeNameStore extends AutoCloseable {
  //I think that a fully reversable shortened version looks ugly. And usually this is not needed
  //So I shorten by throwing away information and use a HashMap to be able to revert the process
  //and prevent collisions.
  String makeGraphQlname(String uri);

  String makeGraphQlnameForPredicate(String uri, Direction direction, boolean isList);

  //Does the same thing, but a type may be used as a value type or an entity type and the graphql names shouldn't clash
  //so this method prefixes the name with value_
  String makeGraphQlValuename(String uri);

  String makeUri(String graphQlName);

  Optional<Tuple<String, Direction>> makeUriForPredicate(String graphQlName);

  String shorten(String uri);

  /**
   * @return a map with prefix and full uri
   */
  Map<String, String> getMappings();

  boolean isClean();
}
