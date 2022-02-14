package nl.knaw.huygens;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class ListPartitionerTest {
  @Test
  public void returnsAllThePartitionsOfAList() {
    List<List<List<String>>> partitions = ListPartitioner.partition(Lists.newArrayList("A", "B", "C", "D"));

    assertThat(partitions, containsInAnyOrder(
      contains(contains("A", "B", "C", "D")),
      contains(contains("A", "B", "C"), contains("D")),
      contains(contains("A", "B"), contains("C", "D")),
      contains(contains("A"), contains("B", "C", "D")),
      contains(contains("A"), contains("B", "C"), contains("D")),
      contains(contains("A", "B"), contains("C"), contains("D")),
      contains(contains("A"), contains("B"), contains("C", "D")),
      contains(contains("A"), contains("B"), contains("C"), contains("D"))
    ));
  }

}
