package nl.knaw.huygens.timbuctoo.model.vre;

import com.google.common.collect.Lists;
import nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static nl.knaw.huygens.timbuctoo.core.dto.dataset.CollectionBuilder.timbuctooCollection;

public class VreBuilder {
  private final String vreName;
  private final String defaultPrefix;
  private List<CollectionBuilder> collections = Lists.newArrayList();
  private String relationCollection;
  private Map<String, String> keywordTypes;

  private VreBuilder(String name, String defaultPrefix) {
    this.vreName = name;
    this.defaultPrefix = defaultPrefix;
  }

  public static VreBuilder vre(String name, String defaultPrefix) {
    return new VreBuilder(name, defaultPrefix);
  }

  public VreBuilder withCollection(String name, Consumer<CollectionBuilder> configurator) {
    CollectionBuilder config = timbuctooCollection(name, defaultPrefix);
    configurator.accept(config);
    this.collections.add(config);
    return this;
  }

  public VreBuilder withCollection(String name) {
    this.collections.add(timbuctooCollection(name, defaultPrefix));
    return this;
  }

  public VreBuilder withKeywordTypes(Map<String, String> keywordTypes) {
    this.keywordTypes = keywordTypes;
    return this;
  }

  public Vre build() {
    Vre vre;
    if (keywordTypes != null) {
      vre = new Vre(vreName, keywordTypes);
    } else {
      vre = new Vre(vreName);
    }
    collections.forEach(x -> x.build(vre));
    return vre;
  }
}
