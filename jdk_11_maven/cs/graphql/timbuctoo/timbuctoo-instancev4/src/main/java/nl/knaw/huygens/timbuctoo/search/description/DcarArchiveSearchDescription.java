package nl.knaw.huygens.timbuctoo.search.description;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import nl.knaw.huygens.timbuctoo.search.description.sort.DutchCaribbeanArchiverAndArchivePeriodSortFieldDescription;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription.createLocalSimpleFullTextSearchDescription;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;

class DcarArchiveSearchDescription extends AbstractSearchDescription {
  private final PropertyDescriptor displayNameDescriptor;
  private final PropertyDescriptor idDescriptor;
  private final List<FacetDescription> facetDescriptions;
  private final List<String> sortableFields;
  private final List<String> fullTextSearchFields;
  private final Map<String, PropertyDescriptor> dataPropertyDescriptors;
  private final List<FullTextSearchDescription> fullTextSearchDescriptions;
  private final SortDescription sortDescription;

  public DcarArchiveSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                      FacetDescriptionFactory facetDescriptionFactory) {
    displayNameDescriptor = propertyDescriptorFactory.getLocal("dcararchive_titleEng", String.class);
    idDescriptor = propertyDescriptorFactory.getLocal(SearchDescription.ID_DB_PROP, String.class);
    facetDescriptions = createFacetDescriptions(facetDescriptionFactory);
    sortableFields = Lists.newArrayList("dynamic_sort_title", "dynamic_k_period");
    fullTextSearchFields = Lists.newArrayList("dynamic_t_titleNld", "dynamic_t_notes", "dynamic_t_titleEng");

    dataPropertyDescriptors = createDataPropertyDescriptors(propertyDescriptorFactory);
    fullTextSearchDescriptions = createFullTextSearchDescriptions();
    sortDescription = createSortDescription();
  }

  private SortDescription createSortDescription() {
    PropertyParserFactory propertyParserFactory = new PropertyParserFactory();
    return new SortDescription(Lists.newArrayList(
      newSortFieldDescription()
        .withName("dynamic_sort_title")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("dcararchive_titleEng")
          .withParser(propertyParserFactory.getParser(String.class))
        )
        .build(),
      new DutchCaribbeanArchiverAndArchivePeriodSortFieldDescription(
        "dynamic_k_period",
        "dcararchive_beginDate",
        "dcararchive_endDate"
      )
    ));
  }

  private ArrayList<FullTextSearchDescription> createFullTextSearchDescriptions() {
    return Lists.newArrayList(
      createLocalSimpleFullTextSearchDescription("dynamic_t_titleNld", "dcararchive_titleNld"),
      createLocalSimpleFullTextSearchDescription("dynamic_t_notes", "dcararchive_notes"),
      createLocalSimpleFullTextSearchDescription("dynamic_t_titleEng", "dcararchive_titleEng"));
  }

  private Map<String, PropertyDescriptor> createDataPropertyDescriptors(PropertyDescriptorFactory pdf) {
    Map<String, PropertyDescriptor> propertyDescriptors = Maps.newHashMap();

    propertyDescriptors.put("_id", pdf.getLocal("tim_id", String.class));
    propertyDescriptors.put("beginDate", pdf.getLocal("dcararchive_beginDate", String.class));
    propertyDescriptors.put("countries", pdf.getLocal("dcararchive_countries", " "));
    propertyDescriptors.put("endDate", pdf.getLocal("dcararchive_endDate", String.class));
    propertyDescriptors.put("itemNo", pdf.getLocal("dcararchive_itemNo", String.class));
    propertyDescriptors.put("refCode", pdf.getLocal("dcararchive_refCode", String.class));
    propertyDescriptors.put("refCodeArchive", pdf.getLocal("dcararchive_refCodeArchive", String.class));
    propertyDescriptors.put("series", pdf.getLocal("dcararchive_series", String.class));
    propertyDescriptors.put("subCode", pdf.getLocal("dcararchive_subCode", String.class));
    propertyDescriptors.put("titleEng", pdf.getLocal("dcararchive_titleEng", String.class));

    return propertyDescriptors;
  }

  private List<FacetDescription> createFacetDescriptions(FacetDescriptionFactory fdf) {
    return Lists.newArrayList(
      fdf.createListFacetDescription("dynamic_s_subject", String.class, "dcarkeyword_value", "has_archive_keyword"),
      fdf.createListFacetDescription("dynamic_s_place", String.class, "dcarkeyword_value", "has_archive_place"),
      fdf.createListFacetDescription("dynamic_s_person", PersonNames.class, "person_names", "has_archive_person"),
      fdf.createListFacetDescription("dynamic_s_refcode", String.class, "dcararchive_refCodeArchive"),
      fdf.createDcarArchiveAndArchiverPeriodFacetDescription("dynamic_i_period", "dcararchive_beginDate",
        "dcararchive_endDate")
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
    return "dcararchive";
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
