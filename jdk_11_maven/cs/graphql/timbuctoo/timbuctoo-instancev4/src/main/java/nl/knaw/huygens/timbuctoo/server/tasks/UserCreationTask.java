package nl.knaw.huygens.timbuctoo.server.tasks;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.dropwizard.servlets.tasks.Task;
import nl.knaw.huygens.timbuctoo.server.security.LocalUserCreator;
import nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.USER_NAME;
import static nl.knaw.huygens.timbuctoo.server.security.UserInfoKeys.USER_PID;

public class UserCreationTask extends Task {
  public static final Logger LOG = LoggerFactory.getLogger(UserCreationTask.class);
  private final LocalUserCreator localUserCreator;

  public UserCreationTask(LocalUserCreator localUserCreator) {
    super("addusers");
    this.localUserCreator = localUserCreator;
  }

  @Override
  public void execute(Map<String, List<String>> immutableMultimap, PrintWriter printWriter) throws Exception {
    LOG.debug("Input {}", immutableMultimap);

    Sets.SetView<String> missingKeys = Sets.difference(UserInfoKeys.all, immutableMultimap.keySet());
    if (!missingKeys.isEmpty()) {
      printWriter.write("Not importing users, because the map missing keys: " + missingKeys);
      return;
    }


    List<String> duplicates = getDuplicateKeys(immutableMultimap);
    if (!duplicates.isEmpty()) {
      printWriter.write("Not importing users, because the map contains duplicate keys: " + duplicates);
      return;
    }

    Map<String, String> userInfo = Maps.newHashMap();
    // get the first value of each entry
    immutableMultimap.keySet().stream()
                     .filter(key -> UserInfoKeys.contains(key))
                     .forEach(key -> userInfo.put(key, immutableMultimap.get(key).iterator().next()));

    localUserCreator.create(userInfo);

    printWriter.write(
      String.format("User created with pid '%s' and user name '%s'", userInfo.get(USER_PID), userInfo.get(USER_NAME)));
  }

  private List<String> getDuplicateKeys(Map<String, List<String>> map) {
    LOG.debug("keys: {}", map.keySet());
    return map.entrySet().stream()
              .filter(entry -> entry.getValue().size() > 1)
              .map(Map.Entry::getKey).distinct()
              .collect(toList());
  }
}
