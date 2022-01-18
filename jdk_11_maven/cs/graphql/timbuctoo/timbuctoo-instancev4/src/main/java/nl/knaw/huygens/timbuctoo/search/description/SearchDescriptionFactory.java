package nl.knaw.huygens.timbuctoo.search.description;

import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.SearchResult;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;

import java.util.Optional;

public class SearchDescriptionFactory {
  private FacetDescriptionFactory facetDescriptionFactory;
  private PropertyDescriptorFactory propertyDescriptorFactory;

  public SearchDescriptionFactory() {
    PropertyParserFactory propertyParserFactory = new PropertyParserFactory();
    facetDescriptionFactory = new FacetDescriptionFactory(propertyParserFactory);
    propertyDescriptorFactory = new PropertyDescriptorFactory(propertyParserFactory);
  }

  SearchDescriptionFactory(FacetDescriptionFactory facetDescriptionFactory,
                           PropertyDescriptorFactory propertyDescriptorFactory) {
    this.facetDescriptionFactory = facetDescriptionFactory;
    this.propertyDescriptorFactory = propertyDescriptorFactory;
  }

  public Optional<SearchDescription> create(String entityName, SearchResult otherSearch) {
    switch (entityName) {
      case "wwperson":
        return Optional.of(new WwPersonSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "wwdocument":
        return Optional.of(new WwDocumentSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "wwcollective":
        return Optional.of(new WwCollectiveSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "wwrelations":
        return Optional.of(new ReceptionSearchDescription(
          propertyDescriptorFactory, facetDescriptionFactory, otherSearch));
      case "dcararchive":
        return Optional.of(new DcarArchiveSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "dcararchiver":
        return Optional.of(new DcarArchiverSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "dcarlegislation":
        return Optional.of(new DcarLegislationSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "cnwperson":
        return Optional.of(new CnwPersonSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      case "charterdocument":
        return Optional.of(new CharterDocumentSearchDescription(propertyDescriptorFactory, facetDescriptionFactory));
      default:
        return Optional.empty();
    }

  }

  public Optional<SearchDescription> create(String entityName) {
    return create(entityName, null);
  }

}
