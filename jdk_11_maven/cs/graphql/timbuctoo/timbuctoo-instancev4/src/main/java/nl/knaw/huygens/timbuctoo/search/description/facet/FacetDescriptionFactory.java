package nl.knaw.huygens.timbuctoo.search.description.facet;

import nl.knaw.huygens.timbuctoo.search.description.FacetDescription;
import nl.knaw.huygens.timbuctoo.search.description.PropertyParser;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;

import java.util.List;

public class FacetDescriptionFactory {
  private final PropertyParserFactory parserFactory;

  public FacetDescriptionFactory(PropertyParserFactory parserFactory) {
    this.parserFactory = parserFactory;
  }

  public FacetDescription createListFacetDescription(String facetName, PropertyParser parser, String propertyName) {
    return new ListFacetDescription(facetName, propertyName, parser);
  }

  public FacetDescription createListFacetDescription(String facetName, Class<?> typeToParse, String propertyName) {
    return this.createListFacetDescription(facetName, parserFactory.getParser(typeToParse), propertyName);
  }

  public FacetDescription createListFacetDescription(String facetName, PropertyParser parser, String propertyName,
                                                     String... relations) {
    return new RelatedListFacetDescription(facetName, propertyName, parser, relations);
  }


  public FacetDescription createListFacetDescription(String facetName, Class<?> typeToParse, String propertyName,
                                                     String... relations) {
    return this.createListFacetDescription(facetName, parserFactory.getParser(typeToParse), propertyName, relations);
  }

  public FacetDescription createDerivedListFacetDescription(String facetName, String relationName,
                                                            Class<?> typeToParse, String propertyName,
                                                            String... relations) {
    return new DerivedListFacetDescription(facetName, propertyName, relationName,
      parserFactory.getParser(typeToParse), relations);
  }


  public FacetDescription createDerivedListFacetDescription(String facetName, List<String> relationNames,
                                                            Class<?> typeToParse, String propertyName,
                                                            String... relations) {
    return new DerivedListFacetDescription(facetName, propertyName, relationNames,
      parserFactory.getParser(typeToParse), relations);
  }

  /**
   * A convenience method, for creating a {@code ListFacetDescription} for related keywords.
   */
  public FacetDescription createKeywordDescription(String facetName, String relationName, final String projectPrefix) {
    String propertyName = String.format("%skeyword_value", projectPrefix);
    return this.createListFacetDescription(facetName, String.class, propertyName, relationName);
  }

  public FacetDescription createDerivedKeywordDescription(String facetName, String relationName,
                                                          final String projectPrefix, String... relations) {
    String propertyName = String.format("%skeyword_value", projectPrefix);
    return this.createDerivedListFacetDescription(facetName, relationName, String.class, propertyName, relations);
  }


  public FacetDescription createDatableRangeFacetDescription(String facetName, String propertyName,
                                                             String... relations) {
    return new RelatedDatableRangeFacetDescription(facetName, propertyName, relations);
  }

  public FacetDescription createDatableRangeFacetDescription(String facetName, String propertyName) {
    return new DatableRangeFacetDescription(facetName, propertyName);
  }

  public FacetDescription createChangeRangeFacetDescription(String facetName, String propertyName) {
    return new ChangeRangeFacetDescription(facetName, propertyName);
  }


  public FacetDescription createMultiValueListFacetDescription(String facetName, String propertyName) {
    return new MultiValueListFacetDescription(facetName, propertyName);
  }

  public FacetDescription createMultiValueListFacetDescription(String facetName, String propertyName,
                                                               String... relations) {
    return new RelatedMultiValueListFacetDescription(facetName, propertyName, relations);
  }


  public FacetDescription createEdgeFacetDescription(String facetName, String... relationNames) {

    return new EdgeListFacetDescription(facetName, relationNames);
  }

  public FacetDescription createDcarArchiveAndArchiverPeriodFacetDescription(String facetName, String startYear,
                                                                             String endYear) {
    return new DutchCaribbeanArchiveAndArchiverPeriodFacetDescription(facetName, startYear, endYear);
  }

  public FacetDescription createJoinedListFacetDescription(String facetName, String propertyName, String separator) {
    return new ListFacetDescription(facetName, propertyName, parserFactory.getJoinedListParser(separator));
  }

  public FacetDescription createAltNameFacetDescription(String facetName, String propertyName) {
    return new AltNameFacetDescription(facetName, propertyName);
  }

  public FacetDescription createCharterFondsFacetDescription(String facetName) {
    return new CharterPortaalFondsFacetDescription(facetName, parserFactory.getParser(String.class));
  }
}
