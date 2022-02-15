package nl.knaw.huygens.timbuctoo.model.vre;

import com.google.common.collect.Maps;

public class VreStubs {
  public static Vre withName(String vreName) {
    return new Vre(vreName, Maps.newHashMap());
  }

}
