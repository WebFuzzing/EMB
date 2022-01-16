package nl.knaw.huygens.timbuctoo.v5.datastores.schemastore.dto;


import com.google.common.collect.Sets;
import org.hamcrest.Matchers;
import org.hamcrest.core.IsCollectionContaining;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class ExplicitTypeTest {
  @Test
  public void convertToTypeConvertsExplicitTypeWithoutFieldsToType() {
    ExplicitType explicitType = new ExplicitType("TestType", null);

    Type type = explicitType.convertToType();

    assertThat(type.getName(), is("TestType"));
  }

  @Test
  public void convertToTypeConvertsExplicitTypeWithFieldsToType() {
    ExplicitField title = new ExplicitField("title",  false,
      null,null);
    List<ExplicitField> fieldList = new ArrayList<>();
    fieldList.add(title);
    ExplicitType explicitType = new ExplicitType("TestType", fieldList);

    Type type = explicitType.convertToType();

    assertThat(type.getName(), is("TestType"));
  }

  @Test(expected = IllegalArgumentException.class)
  public void mergeThrowsExceptionIfUrisDontMatch() {
    ExplicitField explicitField1 = new ExplicitField("test:test", false, null, null);
    ExplicitField explicitField2 = new ExplicitField("test:test2", false, null, null);

    ExplicitField mergedExplicitField = explicitField1.mergeWith(explicitField2);
  }

  @Test
  public void mergeMaintainsIsListProperty() {
    ExplicitField explicitField1 = new ExplicitField("test:test", false, null, null);
    ExplicitField explicitField2 = new ExplicitField("test:test", true, null, null);

    ExplicitField mergedField = explicitField1.mergeWith(explicitField2);

    assertThat(mergedField.isList(), Matchers.is(true));
  }

  @Test
  public void mergeCombinesValuesList() {
    ExplicitField explicitField1 = new ExplicitField("test:test", false, Sets.newHashSet("String"), null);
    ExplicitField explicitField2 = new ExplicitField("test:test", false, Sets.newHashSet("Integer"), null);

    ExplicitField mergedExplicitField = explicitField1.mergeWith(explicitField2);

    assertThat(mergedExplicitField.getValues(), Matchers.is(IsCollectionContaining.hasItems("String", "Integer")));
  }

  @Test
  public void mergeCombinesReferencesList() {
    ExplicitField explicitField1 = new ExplicitField("test:test", false, null, Sets.newHashSet("Integer", "String"));
    ExplicitField explicitField2 = new ExplicitField("test:test", false, null, Sets.newHashSet("String"));

    ExplicitField mergedExplicitField = explicitField1.mergeWith(explicitField2);

    assertThat(mergedExplicitField.getReferences(), Matchers.is(IsCollectionContaining.hasItems("Integer", "String")));
  }
}

