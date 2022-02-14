package nl.knaw.huygens.timbuctoo.rml.util;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;
import nl.knaw.huygens.timbuctoo.rml.rmldata.builders.TriplesMapBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class TopologicalSorter {
  public interface CycleConsumer {
    void handleCycle(Multiset<TriplesMapBuilder> currentChain, TriplesMapBuilder current, TriplesMapBuilder dependency);
  }

  /**
   * Sorts the list of triplesMapBuilders
   * Taken from https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
   *
   * <p>
   *   L ← Empty list that will contain the sorted nodes
   *   while there are unvisited nodes do
   *      select an unvisited node current
   *      visit(current, L)
   * </p>
   *
   * @param input the original list of triplesMapBuilders
   * @param lookup a lookup table to link URI's to their tripleMap objects
   * @param onCycle code to handle a cycle if it occurs
   * @param errorConsumer code to handle errors reported by this method (i.e. log it, throw it, do what you want)
   * @return the sorted list of builders
   */
  public static LinkedList<TriplesMapBuilder> topologicalSort(List<TriplesMapBuilder> input,
                                                              Map<String, TriplesMapBuilder> lookup,
                                                              TopologicalSorter.CycleConsumer onCycle,
                                                              Consumer<String> errorConsumer) {
    Set<TriplesMapBuilder> unvisited = input.stream().collect(Collectors.toSet()); //copy so we don't modify the input

    // L ← Empty list that will contain the sorted nodes
    LinkedList<TriplesMapBuilder> result = Lists.newLinkedList();

    // while there are unvisited nodes do
    while (!unvisited.isEmpty()) {
      // select an unvisited node n
      TriplesMapBuilder current = unvisited.iterator().next();
      // visit(n)
      sortStep(current, result, HashMultiset.create(), lookup, unvisited, onCycle, errorConsumer);
    }

    return result;
  }

  /**
   * Taken from https://en.wikipedia.org/wiki/Topological_sorting#Depth-first_search
   *
   * <p>
   *  function visit(node n, list L)
   *     mark n as being "part of the current chain"
   *     for each node m with an edge from n to m do
   *       if n is not already "part of the current chain"
   *         if m is has not been visited yet then
   *           visit(m)
   *       else
   *         //A cycle should not be possible at this point
   *     mark n as being visited
   *     n is no longer "part of the current chain"
   *     add n to tail of L
   * </p>
   * @param current the next unvisited node from input
   * @param result the sorted result
   * @param currentChain the complete list of input
   * @param lookup a lookup table to link URI's to their tripleMap objects
   * @param onCycle code to handle a cycle if it occurs
   * @param errorConsumer code to handle errors reported by this method (i.e. log it, throw it, do what you want)
   */
  private static void sortStep(TriplesMapBuilder current, LinkedList<TriplesMapBuilder> result,
                               Multiset<TriplesMapBuilder> currentChain, Map<String, TriplesMapBuilder> lookup,
                               Set<TriplesMapBuilder> unvisited, TopologicalSorter.CycleConsumer onCycle,
                               Consumer<String> errorConsumer) {
    // mark n as being "part of the current chain"
    currentChain.add(current);
    // for each node m with an edge from n to m do
    for (String uriOfDependency : current.getReferencedTriplesMaps()) {
      TriplesMapBuilder dependentBuilder = lookup.get(uriOfDependency);
      if (dependentBuilder == null) {
        errorConsumer.accept("No triplesMap with uri " + uriOfDependency + " was found");
        continue;
      }

      // if n is already "part of the current chain"
      if (currentChain.contains(dependentBuilder)) {
        onCycle.handleCycle(currentChain, current, dependentBuilder);
      } else {
        //if m is has not been visited yet then
        if (unvisited.contains(dependentBuilder)) {
          // visit(m)
          sortStep(dependentBuilder, result, currentChain, lookup, unvisited, onCycle, errorConsumer);
        }
      }
    }
    // mark n as being visited
    unvisited.remove(current);
    // n is no longer "part of the current chain"
    currentChain.remove(current);
    // add n to head of L
    result.add(current);
  }

}
