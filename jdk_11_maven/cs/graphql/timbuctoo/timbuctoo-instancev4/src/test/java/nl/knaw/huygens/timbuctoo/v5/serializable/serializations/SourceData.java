package nl.knaw.huygens.timbuctoo.v5.serializable.serializations;

import nl.knaw.huygens.timbuctoo.v5.datastores.quadstore.dto.Direction;
import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Value;

import java.io.IOException;

import static com.google.common.collect.ImmutableMap.of;
import static com.google.common.collect.Lists.newArrayList;
import static nl.knaw.huygens.timbuctoo.v5.serializable.dto.Entity.entity;
import static nl.knaw.huygens.timbuctoo.v5.serializable.dto.PredicateInfo.predicateInfo;
import static nl.knaw.huygens.timbuctoo.v5.serializable.dto.QueryContainer.queryContainer;
import static nl.knaw.huygens.timbuctoo.v5.serializable.dto.SerializableList.serializableList;
import static nl.knaw.huygens.timbuctoo.v5.serializable.dto.Value.fromRawJavaType;

public class SourceData {
  public static SerializableResult simpleResult() throws IOException {
    return new SerializableResult(queryContainer(of(
      "Persons", serializableList(null, null,
        newArrayList(
          entity("http://example.com/1", "http://example.com/Person", of(
            predicateInfo("a", "http://example.org/b"), fromRawJavaType(1), //Note: predicate and mapped name differ!
            predicateInfo("b", "http://example.org/b", Direction.IN), serializableList("next", null,
              entity("http://example.com/11", "http://example.com/SubItem", of(
                predicateInfo("c", "http://example.org/c"), fromRawJavaType("2"),
                predicateInfo("d", "http://example.org/d"),
                  serializableList(null, null, fromRawJavaType("3"), fromRawJavaType("4"), null) //note: Null!
              )),
              entity("http://example.com/12", "http://example.com/SubItem", of(
                predicateInfo("c", "http://example.org/c"), fromRawJavaType("5"),
                predicateInfo("d", "http://example.org/d"),
                  serializableList(null, null, fromRawJavaType("6"), fromRawJavaType("7"))
              ))
            )
          )),
          entity("http://example.com/2", "http://example.com/Person", of(
            predicateInfo("a", "http://example.org/a"), fromRawJavaType("8"),
            predicateInfo("b", "http://example.org/b"), serializableList(null, null,
              entity("http://example.com/11", "http://example.com/SubItem", of(
                predicateInfo("c", "http://example.org/c"), Value.fromRawJavaType("9"),
                predicateInfo("d", "http://example.org/d"),
                  serializableList(null, null, fromRawJavaType(10), fromRawJavaType("11"))
              )),
              entity("http://example.com/12", "http://example.com/SubItem", of(
                predicateInfo("c", "http://example.org/c"), fromRawJavaType("12"),
                predicateInfo("d", "http://example.org/d"),
                  serializableList(null, null, fromRawJavaType(13.0), fromRawJavaType("14")) //note: double!
              )),
              entity("http://example.com/13", "http://example.com/SubItem", of(
                predicateInfo("c", "http://example.org/c"), Value.fromRawJavaType("15"),
                predicateInfo("d", "http://example.org/d"),
                  serializableList(null, null, fromRawJavaType("16"), fromRawJavaType("17"), fromRawJavaType("18"))
              ))
            )
          )),
          entity("http://example.com/3", "http://example.com/Person", of(
            predicateInfo("a", "http://example.org/a"), fromRawJavaType("19"),
            predicateInfo("b", "http://example.org/b"), entity("http://example.com/21", "http://example.com/OtherSubItem", of(
              predicateInfo("e", "http://example.org/e"), fromRawJavaType("20"),
              predicateInfo("f", "http://example.org/f"), fromRawJavaType("21")
            ))
          ))
        )
      )
    )));
  }

}
