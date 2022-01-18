package nl.knaw.huygens.timbuctoo.v5.graphql.datafetchers.dto;

import java.util.Set;

public interface SubjectReference extends DatabaseResult {

  String getSubjectUri();

  Set<String> getTypes();

}
