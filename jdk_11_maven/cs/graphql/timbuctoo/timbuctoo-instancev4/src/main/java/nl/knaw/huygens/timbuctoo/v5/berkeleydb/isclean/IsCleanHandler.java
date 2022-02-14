package nl.knaw.huygens.timbuctoo.v5.berkeleydb.isclean;

public interface IsCleanHandler<TKey, TValue> {
  TKey getKey();

  TValue getValue();
}
