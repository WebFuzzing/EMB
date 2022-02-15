package nl.knaw.huygens.timbuctoo.search.description;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import nl.knaw.huygens.timbuctoo.model.Change;
import nl.knaw.huygens.timbuctoo.model.Datable;
import nl.knaw.huygens.timbuctoo.model.Gender;
import nl.knaw.huygens.timbuctoo.model.PersonNames;
import nl.knaw.huygens.timbuctoo.model.TempName;
import nl.knaw.huygens.timbuctoo.search.EntityRef;
import nl.knaw.huygens.timbuctoo.search.SearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.facet.FacetDescriptionFactory;
import nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription;
import nl.knaw.huygens.timbuctoo.search.description.property.PropertyDescriptorFactory;
import nl.knaw.huygens.timbuctoo.search.description.propertyparser.PropertyParserFactory;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortDescription;
import nl.knaw.huygens.timbuctoo.search.description.sort.SortFieldDescription;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.search.description.Property.localProperty;
import static nl.knaw.huygens.timbuctoo.search.description.fulltext.FullTextSearchDescription
  .createLocalSimpleFullTextSearchDescription;
import static nl.knaw.huygens.timbuctoo.search.description.sort.BuildableSortFieldDescription.newSortFieldDescription;

public class CnwPersonSearchDescription extends AbstractSearchDescription {
  private static final Logger LOG = LoggerFactory.getLogger(CnwPersonSearchDescription.class);

  private final List<String> sortableFields;
  private final List<String> fullTextSearchFields;
  private final PropertyDescriptor displayNameDescriptor;
  private final PropertyDescriptor idDescriptor;
  private final List<FacetDescription> facetDescriptions;
  private final Map<String, PropertyDescriptor> dataPropertyDescriptors;
  private final List<FullTextSearchDescription> fullTextSearchDescriptions;
  private List<SortFieldDescription> sortFieldDescriptions;

  public CnwPersonSearchDescription(PropertyDescriptorFactory propertyDescriptorFactory,
                                    FacetDescriptionFactory facetDescriptionFactory) {
    sortableFields = Lists.newArrayList(
      "dynamic_sort_gender",
      "dynamic_k_birthDate",
      "dynamic_k_deathDate",
      "dynamic_sort_combineddomain",
      "dynamic_sort_characteristic",
      "dynamic_sort_networkdomain",
      "dynamic_sort_name"
    );
    fullTextSearchFields = Lists.newArrayList("dynamic_t_name");

    displayNameDescriptor = propertyDescriptorFactory.getLocal("cnwperson_names", PersonNames.class);
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
        .withName("dynamic_sort_gender")
        .withDefaultValue(null)
        .withProperty(localProperty()
          .withName("cnwperson_gender"))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_k_birthDate")
        .withDefaultValue(0)
        .withProperty(localProperty()
          .withName("cnwperson_birthDate")
          .withParser(ppf.getParser(Datable.class)))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_k_deathDate")
        .withDefaultValue(0)
        .withProperty(localProperty()
          .withName("cnwperson_deathDate")
          .withParser(ppf.getParser(Datable.class)))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_sort_combineddomain")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("cnwperson_combineddomain")
          .withParser(ppf.getJoinedListParser(";")))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_sort_characteristic")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("cnwperson_characteristic")
          .withParser(ppf.getJoinedListParser(";")))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_sort_networkdomain")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("cnwperson_networkdomain")
          .withParser(ppf.getJoinedListParser(" en ")))
        .build(),
      newSortFieldDescription()
        .withName("dynamic_sort_name")
        .withDefaultValue("")
        .withProperty(localProperty()
          .withName("cnwperson_names")
          .withParser(ppf.getParser(PersonNames.class)))
        .build());
  }

  private ArrayList<FullTextSearchDescription> createFullTextSearchDescriptions() {
    return Lists.newArrayList(createLocalSimpleFullTextSearchDescription("dynamic_t_name", "cnwperson_names"));
  }

  private List<FacetDescription> createFacetDescriptions(FacetDescriptionFactory facetDescriptionFactory) {
    return Lists.newArrayList(
      facetDescriptionFactory.createListFacetDescription("dynamic_s_gender", Gender.class, "cnwperson_gender"),
      facetDescriptionFactory.createListFacetDescription("dynamic_s_koppelnaam", String.class, "cnwperson_koppelnaam"),
      facetDescriptionFactory.createDatableRangeFacetDescription("dynamic_i_birthyear", "cnwperson_cnwBirthYear"),
      facetDescriptionFactory.createDatableRangeFacetDescription("dynamic_i_deathyear", "cnwperson_cnwDeathYear"),
      facetDescriptionFactory.createAltNameFacetDescription("dynamic_s_altname", "cnwperson_altNames"),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_periodical", "cnwperson_periodicals"),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_membership", "cnwperson_memberships"),
      facetDescriptionFactory.createJoinedListFacetDescription("dynamic_s_networkdomain",
        "cnwperson_networkDomains", " en "),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_combineddomain",
        "cnwperson_combinedDomains"),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_characteristic",
        "cnwperson_characteristics"),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_subdomain", "cnwperson_subdomains"),
      facetDescriptionFactory.createMultiValueListFacetDescription("dynamic_s_domain", "cnwperson_domains")
    );
  }

  private Map<String, PropertyDescriptor> createDataPropertyDescriptions(
    PropertyDescriptorFactory propertyDescriptorFactory) {
    Map<String, PropertyDescriptor> dataPropertyDescriptors = Maps.newHashMap();
    dataPropertyDescriptors.put("birthDate", propertyDescriptorFactory.getLocal("cnwperson_birthDate", Datable.class));
    dataPropertyDescriptors.put("deathDate", propertyDescriptorFactory.getLocal("cnwperson_deathDate", Datable.class));
    dataPropertyDescriptors.put("gender", propertyDescriptorFactory.getLocal("cnwperson_gender", Gender.class));
    dataPropertyDescriptors.put("modified_date", propertyDescriptorFactory.getLocal("modified", Change.class));
    dataPropertyDescriptors.put("name", propertyDescriptorFactory.getLocal("cnwperson_names", PersonNames.class));
    dataPropertyDescriptors.put("koppelnaam", propertyDescriptorFactory.getLocal("cnwperson_koppelnaam", String.class));


    dataPropertyDescriptors
      .put("_id", propertyDescriptorFactory.getLocal("tim_id", String.class));
    return dataPropertyDescriptors;
  }

  @Override
  public EntityRef createRef(Vertex vertex) {
    final ObjectMapper objectMapper = new ObjectMapper();
    EntityRef entityRef = super.createRef(vertex);

    Map<String, Object> data = entityRef.getData();

    try {
      final TypeReference<List<String>> listTypeReference = new TypeReference<List<String>>() {
      };
      data.put("networkDomains", objectMapper.readValue((String) vertex.value("cnwperson_networkDomains"),
        listTypeReference));
      data.put("characteristics", objectMapper.readValue((String) vertex.value("cnwperson_characteristics"),
        listTypeReference));
      data.put("combinedDomains", objectMapper.readValue((String) vertex.value("cnwperson_combinedDomains"),
        listTypeReference));
      data.put("memberships", objectMapper.readValue((String) vertex.value("cnwperson_memberships"),
        listTypeReference));
      data.put("periodicals", objectMapper.readValue((String) vertex.value("cnwperson_periodicals"),
        listTypeReference));

    } catch (IOException e) {
      LOG.error("Cannot read value for cnwperson with tim_id: {}", vertex.value("tim_id"), e);
    }

    entityRef.setData(data);
    return entityRef;
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
    return "cnwperson";
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
