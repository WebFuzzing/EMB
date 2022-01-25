package nl.knaw.huygens.timbuctoo.v5.graphql.security;

import com.google.common.collect.Sets;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLFieldsContainer;
import nl.knaw.huygens.timbuctoo.v5.dataset.DataSetRepository;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.BasicDataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSet;
import nl.knaw.huygens.timbuctoo.v5.dataset.dto.DataSetMetaData;
import nl.knaw.huygens.timbuctoo.v5.security.dto.Permission;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class PermissionBasedFieldVisibilityTest {

  private UserPermissionCheck userPermissionCheck;

  @Before
  public void setUp() throws Exception {
    userPermissionCheck = mock(UserPermissionCheck.class);
  }

  @Test
  public void getFieldDefinitionsShowsOnlyDataSetsThatUserHasAccessTo() throws Exception {
    final DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = createDataSetWithUserPermissions(
      "user__dataSetUserHasAccessTo",
      Sets.newHashSet(Permission.READ)
    );
    DataSet dataSet2 = createDataSetWithUserPermissions(
      "user__dataSetUserDoesNotHasAccessTo",
      Sets.newHashSet()
    );
    Collection<DataSet> dataSetCollection = Sets.newHashSet(dataSet, dataSet2);
    given(dataSetRepository.getDataSets()).willReturn(dataSetCollection);
    final PermissionBasedFieldVisibility permissionBasedFieldVisibility =
      new PermissionBasedFieldVisibility(userPermissionCheck, dataSetRepository);
    final GraphQLFieldsContainer graphQlFieldsContainer = createGraphQlFieldsContainer(
      "user__dataSetUserHasAccessTo",
      "user__dataSetUserDoesNotHasAccessTo"
    );

    List<GraphQLFieldDefinition> retrievedGraphQlFieldDefinitions = permissionBasedFieldVisibility
      .getFieldDefinitions(graphQlFieldsContainer);

    assertThat(retrievedGraphQlFieldDefinitions, contains(hasProperty("name", is("user__dataSetUserHasAccessTo"))));
  }

  @Test
  public void getFieldDefinitionsShowsNonDataSetFields() throws Exception {
    final DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = createDataSetWithUserPermissions(
      "user__dataSetUserHasAccessTo",
      Sets.newHashSet(Permission.READ)
    );
    DataSet dataSet2 = createDataSetWithUserPermissions(
      "user__dataSetUserDoesNotHasAccessTo",
      Sets.newHashSet()
    );
    Collection<DataSet> dataSetCollection = Sets.newHashSet(dataSet, dataSet2);
    given(dataSetRepository.getDataSets()).willReturn(dataSetCollection);
    final PermissionBasedFieldVisibility permissionBasedFieldVisibility =
      new PermissionBasedFieldVisibility(userPermissionCheck, dataSetRepository);
    final GraphQLFieldsContainer graphQlFieldsContainer = createGraphQlFieldsContainer(
      "user__dataSetUserHasAccessTo",
      "user__dataSetUserDoesNotHasAccessTo", "nonDataSetField"
    );

    List<GraphQLFieldDefinition> retrievedGraphQlFieldDefinitions = permissionBasedFieldVisibility
      .getFieldDefinitions(graphQlFieldsContainer);

    assertThat(retrievedGraphQlFieldDefinitions,
      contains(hasProperty("name", is("user__dataSetUserHasAccessTo")),
        hasProperty("name",is("nonDataSetField"))));
  }

  private GraphQLFieldsContainer createGraphQlFieldsContainer(String... fieldNames) {
    List<GraphQLFieldDefinition> graphQlFieldDefinitions = new ArrayList<>();

    final GraphQLFieldsContainer graphQlFieldsContainer = mock(GraphQLFieldsContainer.class);


    for (String fieldName : fieldNames) {
      GraphQLFieldDefinition graphQlFieldDefinition = createGraphQlFieldDefinition(fieldName);
      graphQlFieldDefinitions.add(graphQlFieldDefinition);
      given(graphQlFieldsContainer.getFieldDefinition(fieldName)).willReturn(graphQlFieldDefinition);
    }


    given(graphQlFieldsContainer.getFieldDefinitions()).willReturn(graphQlFieldDefinitions);
    return graphQlFieldsContainer;
  }

  private GraphQLFieldDefinition createGraphQlFieldDefinition(String fieldName) {
    GraphQLFieldDefinition graphQlFieldDefinition = mock(GraphQLFieldDefinition.class);
    given(graphQlFieldDefinition.getName()).willReturn(fieldName);
    return graphQlFieldDefinition;
  }

  private DataSet createDataSetWithUserPermissions(String combinedId, Set<Permission> permissions) {
    DataSetMetaData dataSetMetaData = createDataSetMetadata(combinedId);
    for (Permission permission : permissions) {
      given(userPermissionCheck.hasPermission(dataSetMetaData, permission)).willReturn(true);
    }
    return createDataSet(dataSetMetaData);
  }

  private DataSet createDataSet(DataSetMetaData dataSetMetaData) {
    DataSet dataSet = mock(DataSet.class);
    given(dataSet.getMetadata()).willReturn(dataSetMetaData);
    return dataSet;
  }

  private DataSetMetaData createDataSetMetadata(String combinedId) {
    DataSetMetaData dataSetMetaData2 = mock(BasicDataSetMetaData.class);
    given(dataSetMetaData2.getCombinedId()).willReturn(combinedId);
    return dataSetMetaData2;
  }


  @Test
  public void getFieldDefinitionReturnsNullIfUserHasNoPermission() throws Exception {
    final DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = createDataSetWithUserPermissions(
      "user__dataSetUserHasAccessTo",
      Sets.newHashSet(Permission.READ)
    );
    DataSet dataSet2 = createDataSetWithUserPermissions(
      "user__dataSetUserDoesNotHasAccessTo",
      Sets.newHashSet()
    );
    Collection<DataSet> dataSetCollection = Sets.newHashSet(dataSet, dataSet2);
    given(dataSetRepository.getDataSets()).willReturn(dataSetCollection);
    final PermissionBasedFieldVisibility permissionBasedFieldVisibility =
      new PermissionBasedFieldVisibility(userPermissionCheck, dataSetRepository);

    final GraphQLFieldsContainer graphQlFieldsContainer = createGraphQlFieldsContainer(
      "user__dataSetUserHasAccessTo",
      "user__dataSetUserDoesNotHasAccessTo"
    );

    GraphQLFieldDefinition retrievedGraphQlFieldDefinition = permissionBasedFieldVisibility
      .getFieldDefinition(graphQlFieldsContainer,"user__dataSetUserDoesNotHasAccessTo");

    assertThat(retrievedGraphQlFieldDefinition,is(nullValue()));
  }

  @Test
  public void getFieldDefinitionReturnsFieldDefinitionIfUserHasPermission() throws Exception {
    final DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = createDataSetWithUserPermissions(
      "user__dataSetUserHasAccessTo",
      Sets.newHashSet(Permission.READ)
    );
    DataSet dataSet2 = createDataSetWithUserPermissions(
      "user__dataSetUserDoesNotHasAccessTo",
      Sets.newHashSet()
    );
    Collection<DataSet> dataSetCollection = Sets.newHashSet(dataSet, dataSet2);
    given(dataSetRepository.getDataSets()).willReturn(dataSetCollection);
    final PermissionBasedFieldVisibility permissionBasedFieldVisibility =
      new PermissionBasedFieldVisibility(userPermissionCheck, dataSetRepository);

    final GraphQLFieldsContainer graphQlFieldsContainer = createGraphQlFieldsContainer(
      "user__dataSetUserHasAccessTo",
      "user__dataSetUserDoesNotHasAccessTo"
    );

    GraphQLFieldDefinition retrievedGraphQlFieldDefinition = permissionBasedFieldVisibility
      .getFieldDefinition(graphQlFieldsContainer,"user__dataSetUserHasAccessTo");

    assertThat(retrievedGraphQlFieldDefinition,hasProperty("name", is("user__dataSetUserHasAccessTo")));
  }

  @Test
  public void getFieldDefinitionReturnsFieldDefinitionIfNotDataSetField() throws Exception {
    final DataSetRepository dataSetRepository = mock(DataSetRepository.class);
    DataSet dataSet = createDataSetWithUserPermissions(
      "user__dataSetUserHasAccessTo",
      Sets.newHashSet(Permission.READ)
    );
    DataSet dataSet2 = createDataSetWithUserPermissions(
      "user__dataSetUserDoesNotHasAccessTo",
      Sets.newHashSet()
    );
    Collection<DataSet> dataSetCollection = Sets.newHashSet(dataSet, dataSet2);
    given(dataSetRepository.getDataSets()).willReturn(dataSetCollection);
    final PermissionBasedFieldVisibility permissionBasedFieldVisibility =
      new PermissionBasedFieldVisibility(userPermissionCheck, dataSetRepository);

    final GraphQLFieldsContainer graphQlFieldsContainer = createGraphQlFieldsContainer(
      "user__dataSetUserHasAccessTo",
      "user__dataSetUserDoesNotHasAccessTo",
      "nonDataSetField"
    );

    GraphQLFieldDefinition retrievedGraphQlFieldDefinition = permissionBasedFieldVisibility
      .getFieldDefinition(graphQlFieldsContainer,new String("nonDataSetField")); //new String to make sure the
    //contents are compared not the instance.

    assertThat(retrievedGraphQlFieldDefinition,hasProperty("name", is("nonDataSetField")));
  }

}
