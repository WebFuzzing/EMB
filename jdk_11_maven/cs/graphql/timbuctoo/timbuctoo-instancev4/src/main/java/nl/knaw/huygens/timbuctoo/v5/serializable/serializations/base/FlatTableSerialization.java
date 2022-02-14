package nl.knaw.huygens.timbuctoo.v5.serializable.serializations.base;

import nl.knaw.huygens.timbuctoo.util.Tuple;
import nl.knaw.huygens.timbuctoo.v5.serializable.SerializableResult;
import nl.knaw.huygens.timbuctoo.v5.serializable.Serialization;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Entity;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionObject;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.GraphqlIntrospectionValue;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.PredicateInfo;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.QueryContainer;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Serializable;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.SerializableList;
import nl.knaw.huygens.timbuctoo.v5.serializable.dto.Value;
import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.CsvSerialization;
import nl.knaw.huygens.timbuctoo.v5.serializable.serializations.Dispatcher;
import org.slf4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static nl.knaw.huygens.timbuctoo.util.Tuple.tuple;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class FlatTableSerialization implements Serialization {
  public static final String SEPARATOR = ".";
  private static final Logger LOG = getLogger(FlatTableSerialization.class);

  @Override
  public void serialize(SerializableResult data) throws IOException {
    List<Serializable> list = findListRecurser(data.getData().getContents());

    if (list != null) {
      // we found one list. We can now make the csv a bit nicer by starting from there and ignoring
      // all enclosing objects
      TocItem toc;
      toc = generateToc(list);
      initialize(getHeader(toc, ""));
      writeBody(list, toc);
      finish();
    } else {
      // we could not zoom in onto one list. Let's serialize everything as a really wide list of one record long.
      //
      // it's something ¯\_(ツ)_/¯
      TocItem toc;
      toc = generateToc(data.getData());
      initialize(getHeader(toc, ""));
      writeBody(data.getData(), toc);
      finish();
    }

  }

  /**
   * is called once at the start of the serialization
   */
  protected abstract void initialize(List<String> columnHeaders) throws IOException;

  /**
   * is called once for each row. NOTE! the list may have gaps (null values)
   */
  protected abstract void writeRow(List<Value> values) throws IOException;

  /**
   * is called once at the end of the serialization
   */
  protected abstract void finish() throws IOException;

  private List<Serializable> findListRecurser(Map<?, Serializable> data) {
    if (data.keySet().isEmpty()) {
      LOG.error("I though maps could not be empty because graphql requires you to ask for at least 1 key");
    }
    if (data.size() == 1) {
      Serializable entry = data.values().iterator().next();
      if (entry instanceof Entity) {
        return findListRecurser(((Entity) entry).getContents());
      } else if (entry instanceof SerializableList) {
        return ((SerializableList) entry).getItems();
      } else if (entry instanceof GraphqlIntrospectionList) {
        return ((GraphqlIntrospectionList) entry).getItems();
      } else if (entry instanceof GraphqlIntrospectionObject) {
        return findListRecurser(((GraphqlIntrospectionObject) entry).getContents());
      } else {
        return null;
      }
    } else {
      return null;
    }
  }

  private TocItem generateToc(QueryContainer list) throws IOException {
    TocItem toc = new TocItem();
    GenerateTocDispatcher dispatcher = new GenerateTocDispatcher();
    for (Map.Entry<String, Serializable> item : list.getContents().entrySet()) {
      TocItem subItem = toc.add(item.getKey());
      dispatcher.dispatch(item.getValue(), subItem);
    }
    return toc;
  }

  private TocItem generateToc(Collection<Serializable> list) throws IOException {
    TocItem toc = new TocItem();
    GenerateTocDispatcher dispatcher = new GenerateTocDispatcher();
    for (Serializable item : list) {
      dispatcher.dispatch(item, toc);
    }
    return toc;
  }

  private List<String> getHeader(TocItem toc, String prefix) throws IOException {
    List<String> header = new ArrayList<>();
    if (toc.contents.isEmpty()) {
      header.add(prefix.substring(0, prefix.length() - SEPARATOR.length()));
    } else {
      for (Map.Entry<String, TocItem> property : toc.contents.entrySet()) {
        header.addAll(getHeader(property.getValue(), prefix + property.getKey() + SEPARATOR));
      }
    }
    return header;
  }

  private void writeBody(Collection<Serializable> list, TocItem toc) throws IOException {
    WriteBodyDispatcher dispatcher = new WriteBodyDispatcher();
    for (Serializable item : list) {
      List<Value> result = new ArrayList<>();
      dispatcher.dispatch(item, tuple(toc, result));
      writeRow(result);
    }
  }

  private void writeBody(QueryContainer container, TocItem toc) throws IOException {
    List<Value> result = new ArrayList<>();
    WriteBodyDispatcher dispatcher = new WriteBodyDispatcher();
    for (Map.Entry<String, TocItem> tocEntry : toc.contents.entrySet()) {
      dispatcher.dispatch(container.getContents().get(tocEntry.getKey()), tuple(tocEntry.getValue(), result));
    }
    writeRow(result);
  }


  private class GenerateTocDispatcher extends Dispatcher<TocItem> {

    @Override
    public void handleEntity(Entity entity, TocItem tocItem) throws IOException {
      for (Map.Entry<PredicateInfo, Serializable> entry : entity.getContents().entrySet()) {
        TocItem subItem = tocItem.add(entry.getKey().getSafeName());
        dispatch(entry.getValue(), subItem);
      }
    }

    @Override
    public void handleNull(TocItem tocItem) throws IOException {

    }

    @Override
    public void handleList(SerializableList list, TocItem tocItem) throws IOException {
      for (int i = 0; i < list.getItems().size(); i++) {
        final Serializable item = list.getItems().get(i);
        TocItem subItem = tocItem.add(i);
        dispatch(item, subItem);
      }
    }

    @Override
    public void handleGraphqlObject(GraphqlIntrospectionObject object, TocItem tocItem) throws IOException {

    }

    @Override
    public void handleGraphqlList(GraphqlIntrospectionList list, TocItem tocItem) throws IOException {

    }

    @Override
    public void handleGraphqlValue(GraphqlIntrospectionValue object, TocItem context) throws IOException { }

    @Override
    public void handleValue(Value object, TocItem tocItem) throws IOException {

    }
  }

  private class WriteBodyDispatcher extends Dispatcher<Tuple<TocItem, List<Value>>> {

    @Override
    public void handleEntity(Entity entity, Tuple<TocItem, List<Value>> context) throws IOException {
      for (Map.Entry<String, TocItem> tocEntry : context.getLeft().contents.entrySet()) {
        dispatch(
          entity.getContentsUnderSafeName().get(tocEntry.getKey()),
          tuple(tocEntry.getValue(), context.getRight())
        );
      }
    }

    @Override
    public void handleNull(Tuple<TocItem, List<Value>> context) throws IOException {
      if (context.getLeft().contents.isEmpty()) {
        context.getRight().add(null);
      } else {
        for (Map.Entry<String, TocItem> tocEntry : context.getLeft().contents.entrySet()) {
          dispatch(
            null,
            tuple(tocEntry.getValue(), context.getRight())
          );
        }
      }
    }

    @Override
    public void handleList(SerializableList serializableList, Tuple<TocItem, List<Value>> context) throws IOException {
      List<Serializable> list = serializableList.getItems();
      for (int i = 0; i <= context.getLeft().maxCount; i++) {
        if (i < list.size()) {
          final Serializable o = list.get(i);
          dispatch(o, tuple(context.getLeft().contents.get(i + ""), context.getRight()));
        } else {
          dispatch(null, tuple(context.getLeft().contents.get(i + ""), context.getRight()));
        }
      }

    }

    @Override
    public void handleGraphqlObject(GraphqlIntrospectionObject object, Tuple<TocItem, List<Value>> context)
      throws IOException {

    }

    @Override
    public void handleGraphqlList(GraphqlIntrospectionList list, Tuple<TocItem, List<Value>> context)
      throws IOException {

    }

    @Override
    public void handleGraphqlValue(GraphqlIntrospectionValue object, Tuple<TocItem, List<Value>> context)
      throws IOException {
    }

    @Override
    public void handleValue(Value object, Tuple<TocItem, List<Value>> context) throws IOException {
      context.getRight().add(object);
    }
  }

  class TocItem {
    public final Map<String, TocItem> contents = new LinkedHashMap<>();
    public int maxCount = 0;

    public TocItem add(int key) {
      if (maxCount < key) {
        maxCount = key;
      }
      return contents.computeIfAbsent(key + "", x -> new TocItem());
    }

    public TocItem add(String key) {
      return contents.computeIfAbsent(key, x -> new TocItem());
    }
  }
}
