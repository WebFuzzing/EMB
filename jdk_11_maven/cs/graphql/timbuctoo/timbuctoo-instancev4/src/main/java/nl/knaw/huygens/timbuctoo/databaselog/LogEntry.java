package nl.knaw.huygens.timbuctoo.databaselog;

import com.google.common.collect.Sets;

import java.util.Set;

public interface LogEntry {
  Set<String> SYSTEM_PROPERTIES = Sets.newHashSet("isLatest", "modified_sort",
    "wwperson_birthDate_sort", "wwperson_deathDate_sort", "wwperson_names_sort", "wwdocument_title",
    "wwdocument_creator_sort", "variations", "tim_id");

  void appendToLog(LogOutput dbLog);
}
