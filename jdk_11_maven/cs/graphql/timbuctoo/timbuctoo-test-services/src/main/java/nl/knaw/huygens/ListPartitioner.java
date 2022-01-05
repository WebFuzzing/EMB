package nl.knaw.huygens;

import com.google.common.collect.Lists;

import java.util.List;

public class ListPartitioner {
  public static <T> List<List<List<T>>> partition(List<T> input) {
    if (input.size() <= 1) {
      List<List<List<T>>> result = Lists.newArrayList();
      List<List<T>> partition = createPartition(input);
      result.add(partition);
      return result;
    }
    else {
      T head = input.get(0);
      List<List<List<T>>> result = Lists.newArrayList();
      List<List<List<T>>> subPartitions = partition(input.subList(1, input.size()));
      for (List<List<T>> subPartition : subPartitions ) {
        List<List<T>> subResult = Lists.newArrayList();
        subResult.add(Lists.newArrayList(head));
        subResult.addAll(subPartition);
        result.add(subResult);

        List<T> subList = Lists.newArrayList(head);
        subList.addAll(subPartition.get(0));
        List<List<T>> subResult2 = Lists.newArrayList();
        subResult2.add(subList);
        subResult2.addAll(subPartition.subList(1, subPartition.size()));
        result.add(subResult2);
      }
      return result;
    }


  }

  private static <T> List<List<T>> createPartition(List<T> input) {
    List<List<T>> partition = Lists.newArrayList();
    partition.add(input);
    return partition;
  }
}
