package nl.knaw.huygens.timbuctoo.crud;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;

@FunctionalInterface
public interface UrlGenerator {
  URI apply(@NotNull String collection, @NotNull UUID id, @Nullable Integer rev);
}
