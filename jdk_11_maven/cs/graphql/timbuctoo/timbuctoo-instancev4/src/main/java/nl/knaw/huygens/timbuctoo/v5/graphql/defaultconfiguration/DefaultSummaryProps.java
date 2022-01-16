package nl.knaw.huygens.timbuctoo.v5.graphql.defaultconfiguration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Lists;
import org.immutables.value.Value;

import java.util.List;

@Value.Immutable
public interface DefaultSummaryProps {
  @JsonCreator
  static DefaultSummaryProps create(
    @JsonProperty("defaultTitles") List<SummaryProp> defaultTitles,
    @JsonProperty("defaultDescriptions") List<SummaryProp> defaultDescriptions,
    @JsonProperty("defaultImages") List<SummaryProp> defaultImages
  ) {

    defaultTitles = defaultTitles == null ? Lists.newArrayList() : defaultTitles;
    defaultDescriptions = defaultDescriptions == null ? Lists.newArrayList() : defaultDescriptions;
    defaultImages = defaultImages == null ? Lists.newArrayList() : defaultImages;

    return ImmutableDefaultSummaryProps.builder()
                                       .defaultTitles(defaultTitles)
                                       .defaultDescriptions(defaultDescriptions)
                                       .defaultImages(defaultImages).build();
  }

  List<SummaryProp> getDefaultTitles();

  List<SummaryProp> getDefaultDescriptions();

  List<SummaryProp> getDefaultImages();
}
