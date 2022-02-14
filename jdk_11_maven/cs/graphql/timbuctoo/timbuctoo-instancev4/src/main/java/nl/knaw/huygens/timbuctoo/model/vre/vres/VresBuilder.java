package nl.knaw.huygens.timbuctoo.model.vre.vres;

import nl.knaw.huygens.timbuctoo.model.vre.VreBuilder;
import nl.knaw.huygens.timbuctoo.model.vre.Vres;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.model.vre.VreBuilder.vre;

public class VresBuilder {
  private final List<VreBuilder> vres = new ArrayList<>();

  public VresBuilder withVre(String name, String prefix, Consumer<VreBuilder> config) {
    VreBuilder vre = vre(name, prefix);
    vres.add(vre);
    config.accept(vre);
    return this;
  }

  public VresBuilder withVre(String name, String prefix) {
    vres.add(vre(name, prefix));
    return this;
  }

  public Vres build() {
    return new CodeConfiguredVres(vres.stream().map(VreBuilder::build).collect(toList()));
  }
}
