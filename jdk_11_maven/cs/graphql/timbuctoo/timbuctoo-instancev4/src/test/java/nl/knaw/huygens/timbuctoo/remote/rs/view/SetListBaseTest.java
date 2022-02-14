package nl.knaw.huygens.timbuctoo.remote.rs.view;


import nl.knaw.huygens.timbuctoo.remote.rs.discover.Result;
import nl.knaw.huygens.timbuctoo.remote.rs.discover.ResultIndex;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.Capability;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.RsMd;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.UrlItem;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.Urlset;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

public class SetListBaseTest {

  @Test
  public void filterCapabilityListsReturnsEachCapabilityListOnce() {
    Urlset content1 = new Urlset(new RsMd(Capability.DESCRIPTION.xmlValue));

    content1.addItem(new UrlItem("http://example.org/rstest/capabilitylist.xml"));
    content1.addItem(new UrlItem("http://example.org/rstest2/capabilitylist.xml"));

    Urlset content2 = new Urlset(new RsMd(Capability.DESCRIPTION.xmlValue));

    content2.addItem(new UrlItem("http://example.org/rstest/capabilitylist.xml"));
    content2.addItem(new UrlItem("http://example.org/rstest2/capabilitylist.xml"));

    Map<URI, Result<?>> resultMap = new HashMap<>();


    Result<Object> result1 = new Result<>(URI.create("http://localhost:7080/v5/resourcesync/sourceDescription.xml"));
    result1.accept(content1);
    resultMap.put(URI.create("http://localhost:7080/v5/resourcesync/sourceDescription.xml"), result1);

    Result<Object> result2 = new Result<>(URI.create("http://localhost:7080/.well-known/resourcesync"));
    result2.accept(content2);
    resultMap.put(URI.create("http://localhost:7080/.well-known/resourcesync"), result2);

    ResultIndex resultIndex = mock(ResultIndex.class);

    given(resultIndex.getResultMap()).willReturn(resultMap);

    SetListBase setListBase = new SetListBase(resultIndex, new Interpreter());
    List<SetItemView> setDetails = setListBase.getSetDetails();

    assertThat(setDetails, Matchers.hasSize(2));
  }
}
