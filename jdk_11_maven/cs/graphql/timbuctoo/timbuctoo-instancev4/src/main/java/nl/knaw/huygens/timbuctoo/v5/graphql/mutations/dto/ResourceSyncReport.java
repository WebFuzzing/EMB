package nl.knaw.huygens.timbuctoo.v5.graphql.mutations.dto;

import com.google.common.collect.Sets;

import java.util.Set;

public class ResourceSyncReport {
  public final Set<String> importedFiles = Sets.newTreeSet();
  public final Set<String> ignoredFiles = Sets.newTreeSet();
}
