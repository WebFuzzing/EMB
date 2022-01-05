package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.berkeleydb.datafetchers;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.CursorQuad;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.DatabaseResult;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginatedList;
import nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto.PaginationArguments;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PaginationHelper {

  public static final int MAX_COUNT = 10_000;

  static <U extends DatabaseResult> PaginatedList<U> getPaginatedList(Stream<CursorQuad> subjectStream,
                                                                      Function<CursorQuad, U> makeItem,
                                                                      PaginationArguments arguments,
                                                                      Optional<Long> total) {

    String[] cursors = new String[3];
    int count = arguments.getCount();

    if (count < 0 || count > MAX_COUNT) {
      count = MAX_COUNT;
    }
    count += 1; //to determine if we reached the end of the list we keep track of one extra
    List<U> subjects = subjectStream
      .limit(count)
      .peek(cs -> {
        if (cursors[0] == null) {
          cursors[0] = cs.getCursor();
        }
        cursors[1] = cursors[2]; //keep track of both the cursor of the last item and the before-last item
        cursors[2] = cs.getCursor();
      })
      .map(makeItem)
      .collect(Collectors.toList());

    if (subjects.isEmpty()) {
      return PaginatedList.create(
        null,
        null,
        subjects,
        total
      );
    } else {

      String prevCursor;
      String nextCursor;
      final List<U> items;
      if (subjects.size() == count) {
        items = subjects.subList(0, count - 1); //remove the one extra
      } else {
        items = subjects;
      }
      if (arguments.getCursor().startsWith("D") || arguments.getCursor().equals("LAST")) {
        Collections.reverse(items);
        //we're iterating downwards so the cursor as retrieved from the end of the list is the prevCursor, not the
        // nextCursor
        prevCursor = subjects.size() == count ? "D\n" + cursors[1] : null;
        nextCursor = arguments.getCursor().equals("LAST") ? null : "A\n" + cursors[0];
      } else {
        prevCursor = arguments.getCursor().equals("") ? null : "D\n" + cursors[0];
        nextCursor = subjects.size() == count ? "A\n" + cursors[1] : null;
      }
      return PaginatedList.create(
        prevCursor,
        nextCursor,
        items,
        total
      );
    }
  }

}
