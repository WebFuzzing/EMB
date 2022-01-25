package nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean;

public class StringStringIsCleanHandler implements IsCleanHandler<String, String> {
  @Override
  public String getKey() {
    return "isClean";
  }

  @Override
  public String getValue() {
    return "isClean";
  }
}
