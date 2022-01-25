package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import com.google.common.base.Charsets;
import graphql.schema.DataFetchingEnvironment;
import nl.knaw.huygens.timbuctoo.v5.graphql.collectionfilter.CollectionFilter;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.ConfiguredFilter;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;

import java.util.Base64;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class PaginationArgumentsHelper {
  public static final int DEFAULT_COUNT = 20;
  private final Map<String, CollectionFilter> collectionFilters;
  private static final Base64.Decoder DECODER = Base64.getDecoder();

  public PaginationArgumentsHelper(Map<String, CollectionFilter> collectionFilters) {
    this.collectionFilters = collectionFilters;
  }

  public PaginationArguments getPaginationArguments(DataFetchingEnvironment environment) {
    String cursor = "";
    int count = DEFAULT_COUNT;

    if (environment.containsArgument("cursor") && (environment.getArgument("cursor") instanceof String)) {
      cursor = new String(DECODER.decode((String) environment.getArgument("cursor")), Charsets.UTF_8);
    }

    if (environment.containsArgument("count")) {
      count = environment.getArgument("count");
    }

    ConfiguredFilter filter = null;
    for (Map.Entry<String, CollectionFilter> entry : collectionFilters.entrySet()) {
      if (environment.containsArgument(entry.getKey())) {
        String searchQuery = environment.getArgument(entry.getKey());
        String cursorArg = cursor;
        int countArg = count;
        String dataSetId = ((DatabaseResult) environment.getSource()).getDataSet().getMetadata().getCombinedId();
        String fieldName = environment.getFieldDefinition().getName();

        filter = () -> entry.getValue().query(dataSetId, fieldName, searchQuery, cursorArg, countArg);
        break;
      }
    }

    return PaginationArguments.create(count, cursor, Optional.ofNullable(filter));
  }

  public String makeListName(String outputTypeName) {
    return outputTypeName + "_List";
  }

  public String makeListField(String fieldName, String outputTypeName) {
    //FIXME: how do you add descriptions to arguments?
    //    .name("cursor")
    //    .description("Use either the empty string to start from the beginning, the string LAST to start from the " +
    //      "end, or a cursor returned by a previous call")
    //  )
    //    .name("count")
    //    .description("The amount of items to request. You might get less items then requested.")
    //  );
    return fieldName + "(cursor: ID, count: Int): " + makeListName(outputTypeName);
  }

  public String makePaginatedListDefinition(String outputType) {
    return "type " + makeListName(outputType) + " {\n" +
      "  prevCursor: ID\n" +
      "  nextCursor: ID\n" +
      "  items: [" + outputType + "!]!\n" +
      "}\n\n";
  }

  public String makeCollectionListName(String outputTypeName) {
    return outputTypeName + "_CollectionList";
  }

  public String makeCollectionListField(String fieldName, String outputTypeName) {
    String customFilters = String.join(
      ", ",
      collectionFilters.keySet().stream().map(k -> k + ": String").collect(Collectors.toList())
    );
    if (!customFilters.isEmpty()) {
      customFilters = ", " + customFilters;
    }
    return fieldName + "(cursor: ID, count: Int" + customFilters + "): " + makeCollectionListName(outputTypeName);
  }


  public String makeCollectionListDefinition(String outputType) {
    return "type " + makeCollectionListName(outputType) + " {\n" +
      "  total: Int\n" +
      "  prevCursor: ID\n" +
      "  nextCursor: ID\n" +
      "  facets: [Facet!]!\n" +
      "  items: [" + outputType + "!]!\n" +
      "}\n\n";
  }

}
