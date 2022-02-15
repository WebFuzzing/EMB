package nl.knaw.huygens.timbuctoo.search.description;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.DocumentType;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortDescription;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortFieldDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;

public class CharterDocumentSearchDescription extends AbstractSearchDescription {

  private final List<String> sortableFields;
  private final List<String> fullTextSearchFields;
  private final PropertyDescriptor displayNameDescriptor;
  private final PropertyDescriptor idDescriptor;
  private final List<FacetDescription> facetDescriptions;
  private final Map<String, PropertyDescriptor> dataPropertyDescriptors;
  private final List<FullTextSearchDescription> fullTextSearchDescriptions;
  private final PropertyParserFactory propertyParserFactory;

  private List<SortFieldDescription> sortFieldDescriptions;
  
  private static final Logger LOG = LoggerFactory.getLogger(CharterDocumentSearchDescription.class);
  
  public CharterDocumentSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                   FacetDescriptionFactory facetDescriptionFactory) {
    propertyParserFactory = new PropertyParserFactory();
    sortableFields = Lists.newArrayList(
      "dynamic_sort_title",
      "dynamic_k_date",
      "dynamic_sort_creator"
      );
    fullTextSearchFields = Lists.newArrayList();

    displayNameDescriptor = propertyDescriptorFactory.getLocal("charterdocument_inventaristekst",
        new PropertyParserFactory().getJoinedListParser(" ")); 
    idDescriptor = propertyDescriptorFactory
      .getLocal(SearchDescription.ID_DB_PROP, String.class);

    facetDescriptions = createFacetDescriptions(facetDescriptionFactory);
    dataPropertyDescriptors = createDataPropertyDescriptions(propertyDescriptorFactory);
    fullTextSearchDescriptions = createFullTextSearchDescriptions();

    sortFieldDescriptions = createSortFieldDescriptions();
  }

  protected ArrayList<SortFieldDescription> createSortFieldDescriptions() {
    PropertyParserFactory ppf = new PropertyParserFactory();
    return Lists.newArrayList(
      newSortFieldDescription()
        .withName("dynamic_k_date")
        .withDefaultValue(0)
        .withProperty(localProperty()
          .withName("charterdocument_date"))
        .build(),
        newSortFieldDescription()
        .withName("dynamic_sort_title")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("charterdocument_title"))
        .build());
  }

  private ArrayList<FullTextSearchDescription> createFullTextSearchDescriptions() {
    return Lists.newArrayList();
  }

  private List<FacetDescription> createFacetDescriptions(FacetDescriptionFactory facetDescriptionFactory) {
    return Lists.newArrayList(
      facetDescriptionFactory.createListFacetDescription("dynamic_s_archief", String.class, "charterdocument_archief"),
      facetDescriptionFactory.createCharterFondsFacetDescription("dynamic_s_fonds"),
      facetDescriptionFactory.createListFacetDescription("dynamic_s_editions", String.class,
          "charterdocument_descriptionOfEditions"),
      facetDescriptionFactory.createDatableRangeFacetDescription("dynamic_i_date", "charterdocument_date")
      );
  }

  private Map<String, PropertyDescriptor> createDataPropertyDescriptions(
    PropertyDescriptorFactory propertyDescriptorFactory) {
    Map<String, PropertyDescriptor> dataPropertyDescriptors = Maps.newHashMap();
    dataPropertyDescriptors.put("date", propertyDescriptorFactory.getLocal("charterdocument_date", Datable.class));
    return dataPropertyDescriptors;
  }

  @Override
  protected List<FacetDescription> getFacetDescriptions() {
    return facetDescriptions;
  }

  @Override
  protected Map<String, PropertyDescriptor> getDataPropertyDescriptors() {
    return dataPropertyDescriptors;
  }

  @Override
  protected PropertyDescriptor getDisplayNameDescriptor() {
    return displayNameDescriptor;
  }

  @Override
  protected PropertyDescriptor getIdDescriptor() {
    return idDescriptor;
  }

  @Override
  public String getType() {
    return "charterdocument";
  }

  @Override
  public List<FullTextSearchDescription> getFullTextSearchDescriptions() {
    return fullTextSearchDescriptions;
  }

  @Override
  public List<String> getSortableFields() {
    return sortableFields;
  }

  @Override
  public List<String> getFullTextSearchFields() {
    return fullTextSearchFields;
  }

  @Override
  protected SortDescription getSortDescription() {

    return new SortDescription(sortFieldDescriptions);
  }
}
