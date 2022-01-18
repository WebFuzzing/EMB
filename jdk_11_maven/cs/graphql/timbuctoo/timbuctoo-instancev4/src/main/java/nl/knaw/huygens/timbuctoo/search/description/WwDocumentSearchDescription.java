package nl.knaw.huygens.timbuctoo.search.description;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.DocumentType;
import nl.knaw.huygens.timbuctoo.model.Gender;
import nl.knaw.huygens.timbuctoo.model.LocationNames;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.property.WwDocumentAuthorDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.property.WwDocumentDisplayNameDescriptor;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortDescription;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortFieldDescription;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription.createDerivedFullTextSearchDescriptionWithBackupProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription.createLocalSimpleFullTextSearchDescription;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;

public class WwDocumentSearchDescription extends AbstractSearchDescription implements SearchDescription {
  private static final List<String> SORTABLE_FIELDS = Lists.newArrayList(
          "dynamic_sort_title",
          "dynamic_k_modified",
          "dynamic_sort_creator");

  private static final List<String> FULL_TEXT_SEARCH_FIELDS = Lists.newArrayList(
          "dynamic_t_author_name",
          "dynamic_t_title",
          "dynamic_t_notes");

  private static final String DYNAMIC_S_AUTHOR_MARITAL_STATUS = "dynamic_s_author_marital_status";
  private static final String DYNAMIC_S_AUTHOR_EDUCATION = "dynamic_s_author_education";
  private static final String DYNAMIC_S_AUTHOR_SOCIAL_CLASS = "dynamic_s_author_social_class";
  private static final String DYNAMIC_S_AUTHOR_RELIGION = "dynamic_s_author_religion";
  private static final String DYNAMIC_S_AUTHOR_FINANCIALS = "dynamic_s_author_financials";
  private static final String DYNAMIC_S_AUTHOR_PROFESSION = "dynamic_s_author_profession";
  private static final String DYNAMIC_S_AUTHOR_COLLECTIVE = "dynamic_s_author_collective";
  private static final String DYNAMIC_S_AUTHOR_DEATHPLACE = "dynamic_s_author_deathplace";
  private static final String DYNAMIC_S_AUTHOR_BIRTHPLACE = "dynamic_s_author_birthplace";
  private static final String DYNAMIC_S_AUTHOR_RESIDENCE = "dynamic_s_author_residence";
  private static final String DYNAMIC_S_AUTHOR_RELATED_LOCATIONS = "dynamic_s_author_relatedLocations";
  private static final String DYNAMIC_S_AUTHOR_GENDER = "dynamic_s_author_gender";
  private static final String DYNAMIC_I_AUTHOR_DEATH_DATE = "dynamic_i_author_deathDate";
  private static final String DYNAMIC_I_AUTHOR_BIRTH_DATE = "dynamic_i_author_birthDate";
  private static final String DYNAMIC_S_AUTHOR_CHILDREN = "dynamic_s_author_children";
  private static final String DYNAMIC_S_AUTHOR_TYPES = "dynamic_s_author_types";

  private static final List<String> ONLY_FILTER_FACET_LIST = Lists.newArrayList(
    DYNAMIC_S_AUTHOR_MARITAL_STATUS ,
    DYNAMIC_S_AUTHOR_EDUCATION ,
    DYNAMIC_S_AUTHOR_SOCIAL_CLASS ,
    DYNAMIC_S_AUTHOR_RELIGION ,
    DYNAMIC_S_AUTHOR_FINANCIALS ,
    DYNAMIC_S_AUTHOR_PROFESSION ,
    DYNAMIC_S_AUTHOR_COLLECTIVE ,
    DYNAMIC_S_AUTHOR_DEATHPLACE ,
    DYNAMIC_S_AUTHOR_BIRTHPLACE ,
    DYNAMIC_S_AUTHOR_RESIDENCE ,
    DYNAMIC_S_AUTHOR_RELATED_LOCATIONS ,
    DYNAMIC_I_AUTHOR_DEATH_DATE ,
    DYNAMIC_I_AUTHOR_BIRTH_DATE ,
    DYNAMIC_S_AUTHOR_CHILDREN ,
    DYNAMIC_S_AUTHOR_TYPES
  );

  private final PropertyParserFactory propertyParserFactory;
  private final PropertyDescriptorFactory propertyDescriptorFactory;

  private final String type = "wwdocument";
  private final Map<String, PropertyDescriptor> dataDescriptors;
  private final List<FacetDescription> facetDescriptions;
  private final PropertyDescriptor idDescriptor;
  private final PropertyDescriptor displayNameDescriptor;
  private final ArrayList<SortFieldDescription> sortFieldDescriptions;
  private final ArrayList<FullTextSearchDescription> fullTextSearchDescriptions;

  public WwDocumentSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                     FacetDescriptionFactory facetDescriptionFactory) {
    propertyParserFactory = new PropertyParserFactory();
    this.propertyDescriptorFactory = propertyDescriptorFactory;

    dataDescriptors = createDataDescriptors();
    facetDescriptions = createFacetDescriptions(facetDescriptionFactory);

    idDescriptor = propertyDescriptorFactory.getLocal(ID_DB_PROP, String.class);

    displayNameDescriptor = createDisplayNameDescriptor();
    fullTextSearchDescriptions = createFullTextSearchDescriptions();

    sortFieldDescriptions = createSortFieldDescriptions();

  }

  private PropertyDescriptor createDisplayNameDescriptor() {
    return new WwDocumentDisplayNameDescriptor();
  }

  private ArrayList<FullTextSearchDescription> createFullTextSearchDescriptions() {
    return Lists.newArrayList(
        createLocalSimpleFullTextSearchDescription("dynamic_t_notes", "wwdocument_notes"),
        createLocalSimpleFullTextSearchDescription("dynamic_t_title", "wwdocument_title"),
        createDerivedFullTextSearchDescriptionWithBackupProperty(
            "dynamic_t_author_name", "wwperson_names", "wwperson_tempName", "isCreatedBy")
    );
  }

  protected ArrayList<SortFieldDescription> createSortFieldDescriptions() {
    return Lists.newArrayList(
        newSortFieldDescription()
            .withName("dynamic_k_modified")
            .withDefaultValue(0L)
            .withProperty(localProperty().withName("modified_sort"))
            .build(),
        newSortFieldDescription()
            .withName("dynamic_sort_title")
            .withDefaultValue("")
            .withProperty(localProperty().withName("wwdocument_title"))
            .build(),
        newSortFieldDescription()
            .withName("dynamic_sort_creator")
            .withDefaultValue("")
            .withProperty(localProperty().withName("wwdocument_creator_sort"))
            .build()
    );
  }

  @Override
  protected List<String> getOnlyFilterFacetList() {
    return ONLY_FILTER_FACET_LIST;
  }

  private List<FacetDescription> createFacetDescriptions(FacetDescriptionFactory facetDescriptionFactory) {
    return Lists.newArrayList(
            facetDescriptionFactory.createDatableRangeFacetDescription("dynamic_i_date", "wwdocument_date"),
            facetDescriptionFactory.createListFacetDescription(
                    "dynamic_s_origin", LocationNames.class, "names", "hasPublishLocation"),

            facetDescriptionFactory.createListFacetDescription(
                    "dynamic_s_language", String.class, "wwlanguage_name", "hasWorkLanguage"),

            facetDescriptionFactory.createListFacetDescription(
                    "dynamic_s_genre", String.class, "wwkeyword_value", "hasGenre"),

            facetDescriptionFactory.createListFacetDescription(
                    "dynamic_s_sources", String.class, "wwdocument_title", "hasDocumentSource"),

            facetDescriptionFactory.createChangeRangeFacetDescription("dynamic_i_modified", "modified"),

            facetDescriptionFactory.createListFacetDescription(
                    "dynamic_s_document_type", DocumentType.class, "wwdocument_documentType"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_MARITAL_STATUS, "hasMaritalStatus", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_EDUCATION, "hasEducation", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_SOCIAL_CLASS, "hasSocialClass", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_RELIGION, "hasReligion", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_FINANCIALS, "hasFinancialSituation", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedKeywordDescription(
                    DYNAMIC_S_AUTHOR_PROFESSION, "hasProfession", "ww", "isCreatedBy"),

            facetDescriptionFactory.createDerivedListFacetDescription(
                    DYNAMIC_S_AUTHOR_COLLECTIVE, "isMemberOf", String.class, "wwcollective_name", "isCreatedBy"),

            facetDescriptionFactory.createDerivedListFacetDescription(
                    DYNAMIC_S_AUTHOR_DEATHPLACE, "hasDeathPlace", LocationNames.class, "names", "isCreatedBy"),

            facetDescriptionFactory.createDerivedListFacetDescription(
                    DYNAMIC_S_AUTHOR_BIRTHPLACE, "hasBirthPlace", LocationNames.class, "names", "isCreatedBy"),

            facetDescriptionFactory.createDerivedListFacetDescription(
                    DYNAMIC_S_AUTHOR_RESIDENCE, "hasResidenceLocation", LocationNames.class, "names", "isCreatedBy"),

            facetDescriptionFactory.createDerivedListFacetDescription(
                    DYNAMIC_S_AUTHOR_RELATED_LOCATIONS,
                    Lists.newArrayList("hasBirthPlace", "hasDeathPlace", "hasResidenceLocation"),
                    LocationNames.class,
                    "names",
                    "isCreatedBy"),

            facetDescriptionFactory.createListFacetDescription(
                    DYNAMIC_S_AUTHOR_GENDER, Gender.class, "wwperson_gender", "isCreatedBy"),

            facetDescriptionFactory.createDatableRangeFacetDescription(
                    DYNAMIC_I_AUTHOR_DEATH_DATE, "wwperson_deathDate", "isCreatedBy"),

            facetDescriptionFactory.createDatableRangeFacetDescription(
                    DYNAMIC_I_AUTHOR_BIRTH_DATE, "wwperson_birthDate", "isCreatedBy"),

            facetDescriptionFactory.createListFacetDescription(
                    DYNAMIC_S_AUTHOR_CHILDREN, String.class, "wwperson_children", "isCreatedBy"),

            facetDescriptionFactory.createMultiValueListFacetDescription(
                    DYNAMIC_S_AUTHOR_TYPES, "wwperson_types", "isCreatedBy")
    );
  }

  private Map<String, PropertyDescriptor> createDataDescriptors() {
    Map<String, PropertyDescriptor> dataDescriptors = Maps.newHashMap();
    dataDescriptors.put("_id", propertyDescriptorFactory.getLocal(ID_DB_PROP, String.class));
    dataDescriptors.put("authorName", createAuthorDescriptor());
    dataDescriptors.put("title", propertyDescriptorFactory.getLocal("wwdocument_title", String.class));
    dataDescriptors.put("date", propertyDescriptorFactory.getLocal("wwdocument_date", Datable.class));
    dataDescriptors.put("authorGender", propertyDescriptorFactory.getDerived(
            "isCreatedBy",
            "wwperson_gender",
            "wwrelation_accepted",
            Gender.class));
    dataDescriptors.put("documentType", propertyDescriptorFactory
            .getLocal("wwdocument_documentType", DocumentType.class));
    dataDescriptors.put("modified_date", propertyDescriptorFactory
            .getLocal("modified", propertyParserFactory.getParser(Change.class)));
    dataDescriptors.put("genre", propertyDescriptorFactory
            .getDerived("hasGenre", "wwkeyword_value", "wwrelation_accepted", String.class));
    dataDescriptors.put("publishLocation", propertyDescriptorFactory.getDerived(
            "hasPublishLocation",
            "names",
            "wwrelation_accepted",
            LocationNames.class));
    dataDescriptors.put("language", propertyDescriptorFactory.getDerived(
            "hasWorkLanguage",
            "wwlanguage_name",
            "wwrelation_accepted",
            String.class));

    return dataDescriptors;
  }

  private PropertyDescriptor createAuthorDescriptor() {
    return new WwDocumentAuthorDescriptor();
  }

  @Override
  public List<String> getSortableFields() {
    return SORTABLE_FIELDS;
  }

  @Override
  public List<String> getFullTextSearchFields() {
    return FULL_TEXT_SEARCH_FIELDS;
  }


  @Override
  protected List<FacetDescription> getFacetDescriptions() {
    return facetDescriptions;
  }

  @Override
  protected Map<String, PropertyDescriptor> getDataPropertyDescriptors() {
    return dataDescriptors;
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
    return type;
  }


  @Override
  public List<FullTextSearchDescription> getFullTextSearchDescriptions() {
    return fullTextSearchDescriptions;
  }

  @Override
  protected SortDescription getSortDescription() {

    return new SortDescription(sortFieldDescriptions);
  }
}
