package nl.knaw.huygens.timbuctoo.remote.rs.xml;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

public class CapabilityTest {

  @Test
  public void forString() {
    for (Capability capa : Capability.values()) {
      assertThat(capa, equalTo(Capability.forString(capa.xmlValue)));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void forWrongString() {
    Capability.forString("bar");
  }

  @Test
  public void upRelation() {
    assertThat(Capability.DESCRIPTION.getUpRelation(), nullValue());
    assertThat(Capability.CAPABILITYLIST.getUpRelation(), equalTo(Capability.DESCRIPTION));
    assertThat(Capability.RESOURCELIST.getUpRelation(), equalTo(Capability.CAPABILITYLIST));
  }

  @Test
  public void indexRelation() {
    assertThat(Capability.DESCRIPTION.getIndexRelation(), equalTo(Capability.DESCRIPTION));
    assertThat(Capability.RESOURCEDUMP_MANIFEST.getIndexRelation(), nullValue());
    assertThat(Capability.CHANGEDUMP_MANIFEST.getIndexRelation(), nullValue());
  }

  @Test
  public void verifyUpRelation() {

    assertThat(Capability.DESCRIPTION.verifyUpRelation(null), is(true));
    assertThat(Capability.DESCRIPTION.verifyUpRelation(Capability.CAPABILITYLIST), is(false));

    assertThat(Capability.CAPABILITYLIST.verifyUpRelation(null), is(false));
    assertThat(Capability.CAPABILITYLIST.verifyUpRelation(Capability.CAPABILITYLIST), is(false));
    assertThat(Capability.CAPABILITYLIST.verifyUpRelation(Capability.DESCRIPTION), is(true));

    assertThat(Capability.RESOURCELIST.verifyUpRelation(null), is(false));
    assertThat(Capability.RESOURCELIST.verifyUpRelation(Capability.DESCRIPTION), is(false));
    assertThat(Capability.RESOURCELIST.verifyUpRelation(Capability.CAPABILITYLIST), is(true));

  }

  @Test
  public void verifyIndexRelation() {

    assertThat(Capability.RESOURCELIST.verifyIndexRelation(null), is(false));
    assertThat(Capability.RESOURCELIST.verifyIndexRelation(Capability.CAPABILITYLIST), is(false));
    assertThat(Capability.RESOURCELIST.verifyIndexRelation(Capability.RESOURCELIST), is(true));

    assertThat(Capability.RESOURCEDUMP_MANIFEST.verifyIndexRelation(null), is(true));
    assertThat(Capability.RESOURCEDUMP_MANIFEST.verifyIndexRelation(Capability.RESOURCEDUMP), is(false));
    assertThat(Capability.RESOURCEDUMP_MANIFEST.verifyIndexRelation(Capability.RESOURCEDUMP_MANIFEST), is(false));
  }

  @Test
  public void verifyChildRelation() {

    assertThat(Capability.DESCRIPTION.verifyChildRelation(null), is(false));
    assertThat(Capability.DESCRIPTION.verifyChildRelation(Capability.DESCRIPTION), is(true));
    assertThat(Capability.DESCRIPTION.verifyChildRelation(Capability.CAPABILITYLIST), is(true));
    assertThat(Capability.DESCRIPTION.verifyChildRelation(Capability.RESOURCELIST), is(false));

    assertThat(Capability.CAPABILITYLIST.verifyChildRelation(null), is(false));
    assertThat(Capability.CAPABILITYLIST.verifyChildRelation(Capability.DESCRIPTION), is(false));
    assertThat(Capability.CAPABILITYLIST.verifyChildRelation(Capability.CAPABILITYLIST), is(true));
    assertThat(Capability.CAPABILITYLIST.verifyChildRelation(Capability.RESOURCELIST), is(true));

    assertThat(Capability.RESOURCELIST.verifyChildRelation(null), is(false));
    assertThat(Capability.RESOURCELIST.verifyChildRelation(Capability.RESOURCELIST), is(true));

    assertThat(Capability.RESOURCEDUMP_MANIFEST.verifyChildRelation(null), is(true));
    assertThat(Capability.RESOURCEDUMP_MANIFEST.verifyChildRelation(Capability.RESOURCEDUMP_MANIFEST), is(false));
  }


}
