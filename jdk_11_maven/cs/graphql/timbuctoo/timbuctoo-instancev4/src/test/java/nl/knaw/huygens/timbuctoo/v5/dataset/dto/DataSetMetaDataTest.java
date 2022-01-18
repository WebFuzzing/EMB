package nl.knaw.huygens.timbuctoo.v5.dataset.dto;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.is;

public class DataSetMetaDataTest {

  @Test
  public void testRegex() throws Exception {
    List<String> ok = Lists.newArrayList(
      "foo",
      "f_oo",
      "foo_bar",
      "f_oo_bar",
      "f00",
      "f0o",
      "f_0o",
      "f0_o",
      "f_0_o"
    );
    List<String> notOk = Lists.newArrayList(
      "f",
      "3foo",
      "3_foo",
      "foo__bar",
      "foo_bar_",
      "foo-bar",
      "foo_bar ",
      "_foobar"
    );

    assertThat(
      ok.stream().filter(DataSetMetaData::isValidId).collect(Collectors.toList()),
      is(ok)
    );
    assertThat(
      notOk.stream().filter(DataSetMetaData::isValidId).collect(Collectors.toList()),
      is(emptyIterable())
    );
  }

}
