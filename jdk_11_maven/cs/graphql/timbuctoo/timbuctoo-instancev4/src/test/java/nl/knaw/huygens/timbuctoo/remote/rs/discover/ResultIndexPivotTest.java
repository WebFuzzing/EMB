package nl.knaw.huygens.timbuctoo.remote.rs.discover;

import nl.knaw.huygens.timbuctoo.remote.rs.xml.Capability;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.RsMd;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.RsRoot;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.Sitemapindex;
import nl.knaw.huygens.timbuctoo.remote.rs.xml.Urlset;
import org.junit.Test;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

/**
 * Created on 2016-09-19 16:32.
 */
public class ResultIndexPivotTest {

  @SuppressWarnings("unchecked")
  @Test
  public void testSelections() throws Exception {
    final ResultIndex index = new ResultIndex();

    Result<Urlset> result1 = new Result<>(new URI("doc1"));
    result1.accept(new Urlset(new RsMd((Capability.RESOURCELIST.xmlValue))));
    result1.addError(new RemoteResourceSyncFrameworkException("Bla1"));
    index.add(result1);

    Result<Urlset> result2 = new Result<>(new URI("doc2"));
    result2.accept(new Urlset(new RsMd((Capability.CAPABILITYLIST.xmlValue))));
    index.add(result2);

    Result<Sitemapindex> result3 = new Result<>(new URI("doc3"));
    result3.accept(new Sitemapindex(new RsMd(Capability.CHANGELIST.xmlValue)));
    index.add(result3);

    Result<Sitemapindex> result4 = new Result<>(new URI("doc4"));
    result4.addError(new RemoteResourceSyncFrameworkException("Bla4"));
    index.add(result4);

    Result<Sitemapindex> result5 = new Result<>(new URI("doc5"));
    result5.accept(new Sitemapindex(new RsMd((Capability.CAPABILITYLIST.xmlValue))));
    index.add(result5);

    ResultIndexPivot pivot = new ResultIndexPivot(index);

    List<Throwable> errorList = pivot.listErrors();
    assertThat(errorList.stream().map(Throwable::getMessage).collect(Collectors.toList()),
      containsInAnyOrder("Bla1", "Bla4"));
    assertThat(errorList.size(), equalTo(2));

    List<Result<?>> errorResultList = pivot.listErrorResults();
    assertThat(errorResultList, containsInAnyOrder(result1, result4));
    assertThat(errorResultList.size(), equalTo(2));

    List<Result<?>> resultList = pivot.listResultsWithContent();
    assertThat(resultList, containsInAnyOrder(result1, result2, result3, result5));
    assertThat(resultList.size(), equalTo(4));

    List<Result<Urlset>> setResultList = pivot.listUrlsetResults();
    assertThat(setResultList, containsInAnyOrder(result1, result2));
    assertThat(setResultList.size(), equalTo(2));

    List<Result<Sitemapindex>> indexResultList = pivot.listSitemapindexResults();
    assertThat(indexResultList.size(), equalTo(2));
    assertThat(indexResultList, containsInAnyOrder(result3, result5));

    List<Result<Urlset>> capabilityListSetResults = pivot.listUrlsetResults(Capability.CAPABILITYLIST);
    assertThat(capabilityListSetResults.size(), equalTo(1));
    assertThat(capabilityListSetResults, containsInAnyOrder(result2));

    List<Result<Sitemapindex>> capabilityListIndexResults = pivot.listSitemapindexResults(Capability.CAPABILITYLIST);
    assertThat(capabilityListIndexResults.size(), equalTo(1));
    assertThat(capabilityListIndexResults, containsInAnyOrder(result5));

    List<Result<RsRoot>> capabilityListResults = pivot.listRsRootResults(Capability.CAPABILITYLIST);
    assertThat(capabilityListResults.size(), equalTo(2));
    assertThat(capabilityListResults, containsInAnyOrder(result2, result5));

    capabilityListResults = pivot.listRsRootResultsByLevel(0);
    //capabilityListResults.stream().forEach(rsRootResult -> System.out.println(rsRootResult.getUri()));
    assertThat(capabilityListResults.size(), equalTo(0));

    capabilityListResults = pivot.listRsRootResultsByLevel(1);
    //capabilityListResults.stream().forEach(rsRootResult -> System.out.println(rsRootResult.getUri()));
    assertThat(capabilityListResults.size(), equalTo(2));
    assertThat(capabilityListResults, containsInAnyOrder(result1, result3));

    capabilityListResults = pivot.listRsRootResultsByLevel(2);
    //capabilityListResults.stream().forEach(rsRootResult -> System.out.println(rsRootResult.getUri()));
    assertThat(capabilityListResults.size(), equalTo(2));
    assertThat(capabilityListResults, containsInAnyOrder(result2, result5));

    capabilityListResults = pivot.listRsRootResultsByLevel(3);
    //capabilityListResults.stream().forEach(rsRootResult -> System.out.println(rsRootResult.getUri()));
    assertThat(capabilityListResults.size(), equalTo(0));

    capabilityListResults = pivot.listRsRootResultsByLevel(-1);
    //capabilityListResults.stream().forEach(rsRootResult -> System.out.println(rsRootResult.getUri()));
    assertThat(capabilityListResults.size(), equalTo(0));
  }


}
