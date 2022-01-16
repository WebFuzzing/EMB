package nl.knaw.huygens.timbuctoo.search.description;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
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

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription
  .createLocalSimpleFullTextSearchDescription;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;

class DcarLegislationSearchDescription extends AbstractSearchDescription {
  private final PropertyDescriptor displayNameDescriptor;
  private final PropertyDescriptor idDescriptor;
  private final List<FacetDescription> facetDescriptions;
  private final List<String> sortableFields;
  private final List<String> fullTextSearchFields;
  private final Map<String, PropertyDescriptor> dataPropertyDescriptors;
  private final List<FullTextSearchDescription> fullTextSearchDescriptions;
  private final SortDescription sortDescription;

  public DcarLegislationSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                          FacetDescriptionFactory facetDescriptionFactory) {
    displayNameDescriptor = propertyDescriptorFactory.getLocal("dcarlegislation_titleEng", String.class);
    idDescriptor = propertyDescriptorFactory.getLocal(SearchDescription.ID_DB_PROP, String.class);
    facetDescriptions = createFacetDescriptions(facetDescriptionFactory);
    sortableFields = Lists.newArrayList("dynamic_sort_title", "dynamic_k_date");
    fullTextSearchFields =
      Lists.newArrayList("dynamic_t_titleEng", "dynamic_t_text", "dynamic_t_titleNld", "dynamic_t_contents");

    dataPropertyDescriptors = createDataPropertyDescriptors(propertyDescriptorFactory);
    fullTextSearchDescriptions = createFullTextSearchDescriptions();
    sortDescription = createSortDescription();
  }

  private SortDescription createSortDescription() {
    PropertyParserFactory propertyParserFactory = new PropertyParserFactory();
    List<SortFieldDescription> sortFieldDescriptions = Lists.newArrayList(
      newSortFieldDescription()
        .withName("dynamic_sort_title")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("dcarlegislation_titleEng")
          .withParser(propertyParserFactory.getParser(String.class)))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_k_date")
        .withDefaultValue(0)
        .withProperty(localProperty()
          .withName("dcarlegislation_date1")
          .withParser(propertyParserFactory.getParser(Datable.class)))
        .build()
    );

    return new SortDescription(sortFieldDescriptions);
  }

  private ArrayList<FullTextSearchDescription> createFullTextSearchDescriptions() {

    return Lists.newArrayList(
      createLocalSimpleFullTextSearchDescription("dynamic_t_titleEng", "dcarlegislation_titleEng"),
      createLocalSimpleFullTextSearchDescription("dynamic_t_text", "dcarlegislation_reference"),
      createLocalSimpleFullTextSearchDescription("dynamic_t_titleNld", "dcarlegislation_titleNld"),
      createLocalSimpleFullTextSearchDescription("dynamic_t_contents", "dcarlegislation_contents"));
  }

  private Map<String, PropertyDescriptor> createDataPropertyDescriptors(PropertyDescriptorFactory pdf) {
    Map<String, PropertyDescriptor> propertyDescriptors = Maps.newHashMap();

    propertyDescriptors.put("_id", pdf.getLocal(ID_DB_PROP, String.class));
    propertyDescriptors.put("titleEng", pdf.getLocal("dcarlegislation_titleEng", String.class));
    propertyDescriptors.put("date1", pdf.getLocal("dcarlegislation_date1", String.class));

    return propertyDescriptors;
  }

  private List<FacetDescription> createFacetDescriptions(FacetDescriptionFactory fdf) {
    return Lists.newArrayList(
      fdf.createListFacetDescription("dynamic_s_place", String.class, "dcarkeyword_value", "has_legislation_place"),
      fdf.createListFacetDescription("dynamic_s_person", PersonNames.class, "person_names", "has_legislation_person"),
      fdf.createListFacetDescription("dynamic_s_subject", String.class, "dcarkeyword_value", "has_legislation_keyword"),
      fdf.createDatableRangeFacetDescription("dynamic_i_date", "dcarlegislation_date1")
    );
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
  public List<String> getSortableFields() {
    return sortableFields;
  }

  @Override
  public List<String> getFullTextSearchFields() {
    return fullTextSearchFields;
  }

  @Override
  public String getType() {
    return "dcarlegislation";
  }

  @Override
  protected List<FullTextSearchDescription> getFullTextSearchDescriptions() {
    return fullTextSearchDescriptions;
  }

  @Override
  protected SortDescription getSortDescription() {
    return sortDescription;
  }

}
