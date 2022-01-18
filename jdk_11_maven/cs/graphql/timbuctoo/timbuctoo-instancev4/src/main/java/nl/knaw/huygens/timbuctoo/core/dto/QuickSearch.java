package nl.knaw.huygens.timbuctoo.core.dto;

import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

// FIXME now it only supports and search request, we have to make it support or search requests as well.
public class QuickSearch {

  private final List<String> fullMatches;
  private final List<String> partialMatches;

  private QuickSearch(List<String> fullMatches, List<String> partialMatches) {

    this.fullMatches = fullMatches;
    this.partialMatches = partialMatches;
  }

  public static QuickSearch fromQueryString(String queryString) {
    if (queryString == null) {
      queryString = "";
    }
    String cleanedQuery = StringUtils.stripStart(queryString, "*");

    List<String> partialMatches = Lists.newArrayList();
    List<String> fullMatches;
    if (queryString.isEmpty()) {
      fullMatches = Lists.newArrayList();
    } else {
      String[] splitQuery = cleanedQuery.split(" ");
      fullMatches = Lists.newArrayList(splitQuery);
      int indexOfLastItem = fullMatches.size() - 1;
      partialMatches.add(StringUtils.stripEnd(fullMatches.remove(indexOfLastItem), "*"));
    }

    return new QuickSearch(fullMatches, partialMatches);
  }

  public List<String> fullMatches() {
    return fullMatches;
  }

  public List<String> partialMatches() {
    return partialMatches;
  }

  @Override
  public boolean equals(Object obj) {
    return EqualsBuilder.reflectionEquals(this, obj);
  }

  @Override
  public int hashCode() {
    return HashCodeBuilder.reflectionHashCode(this);
  }
}

