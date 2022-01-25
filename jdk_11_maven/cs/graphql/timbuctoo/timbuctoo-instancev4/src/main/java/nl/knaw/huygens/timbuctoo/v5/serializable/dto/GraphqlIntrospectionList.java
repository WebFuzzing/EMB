package nl.knaw.huygens.timbuctoo.v5.serializable.dto;

import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
@Value.Style(jdkOnly = true) //Needed to allow nulls in the collection
public interface GraphqlIntrospectionList extends Serializable {
  @AllowNulls
  List<Serializable> getItems();

  static GraphqlIntrospectionList graphqlIntrospectionList(Iterable<? extends Serializable> data) {
    return ImmutableGraphqlIntrospectionList.builder()
      .items(data)
      .build();
  }


}
