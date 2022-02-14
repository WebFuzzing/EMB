package nl.knaw.huygens.timbuctoo.search.description;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;

public class Property {

  private final String name;
  private final PropertyParser parser;
  private final String[] relations;

  private Property(String name, PropertyParser parser, String... relations) {
    this.name = name;
    this.parser = parser;
    this.relations = relations;
  }

  public static PropertyNameBuilder localProperty() {
    return new Property.Builder();
  }

  public static PropertyNameBuilder derivedProperty(String... relations) {
    return new Property.Builder(relations);
  }

  public GraphTraversal<Object, Object> getTraversal() {
    GraphTraversal<Object, Object> baseTraversal = relations != null && relations.length > 0 ?
            __.bothE(relations).otherV().has(name).values(name) :
            __.has(name).values(name);

    if (parser == null) {
      return baseTraversal;
    }

    return
      baseTraversal.map(x -> parser.parseForSort("" + x.get()));
  }

  public interface PropertyNameBuilder {
    PropertyWithoutParserBuilder withName(String name);
  }

  public interface PropertyParserBuilder {
    PropertyBuilder withParser(PropertyParser parser);
  }

  public interface PropertyBuilder {
    Property build();
  }

  public interface PropertyWithoutParserBuilder extends PropertyParserBuilder, PropertyBuilder {

  }

  private static class Builder implements PropertyNameBuilder, PropertyWithoutParserBuilder {

    private final String[] relations;
    private PropertyParser parser;
    private String name;

    public Builder(String... relations) {
      this.relations = relations;
    }

    @Override
    public Property build() {
      return new Property(name, parser, relations);
    }

    @Override
    public PropertyWithoutParserBuilder withName(String name) {
      this.name = name;
      return this;
    }

    @Override
    public PropertyBuilder withParser(PropertyParser parser) {
      this.parser = parser;
      return this;
    }

  }
}
