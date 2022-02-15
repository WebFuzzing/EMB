package nl.knaw.huygens.timbuctoo.v5.graphql.rootquery.dataproviders;

import org.immutables.value.Value;

import java.util.List;
import java.util.Optional;

@Value.Immutable
public interface CollectionMetadataList {

  Optional<String> getPrevCursor();

  Optional<String> getNextCursor();

  List<CollectionMetadata> getItems();

}
