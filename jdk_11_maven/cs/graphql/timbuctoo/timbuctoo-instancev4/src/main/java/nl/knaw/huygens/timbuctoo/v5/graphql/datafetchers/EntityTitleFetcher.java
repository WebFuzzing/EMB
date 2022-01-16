package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLObjectType;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.SubjectReference;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.TypedValue;
import nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration.SummaryProp;

import java.util.List;
import java.util.Optional;

import static nl.knaw.huygens.timbuctoo.v5.graphql.DirectiveRetriever.getDirectiveArgument;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.STRING;
import static nl.knaw.huygens.timbuctoo.v5.util.RdfConstants.TIM_SUMMARYTITLEPREDICATE;

public class EntityTitleFetcher implements DataFetcher<TypedValue> {

  private final SummaryPropDataRetriever summaryPropDataRetriever;

  public EntityTitleFetcher(List<SummaryProp> defaultTitles) {
    summaryPropDataRetriever = new SummaryPropDataRetriever(TIM_SUMMARYTITLEPREDICATE, defaultTitles);
  }

  @Override
  public TypedValue get(DataFetchingEnvironment env) {
    if (env.getSource() instanceof SubjectReference) {
      SubjectReference source = env.getSource();
      DataSet dataSet = source.getDataSet();

      if (env.getParentType() instanceof GraphQLObjectType) {
        String type = getDirectiveArgument((GraphQLObjectType) env.getParentType(), "rdfType", "uri").orElse(null);

        Optional<TypedValue> summaryProperty = summaryPropDataRetriever.createSummaryProperty(source, dataSet, type);
        if (summaryProperty.isPresent()) {
          return summaryProperty.get();
        }
      }

      // fallback to the uri if no summary props can be found for the title
      return TypedValue.create(source.getSubjectUri(), STRING, dataSet);

    }
    return null;
  }

}
