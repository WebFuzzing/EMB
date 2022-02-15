package nl.knaw.huygens.timbuctoo.util;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class EvilEnvironmentVariableHacker {

  public static void setEnv(String elasticsearchHost, String elasticsearchPort, String elasticsearchUser,
                            String elasticsearchPassword, String baseUri, String authPath, String dataPath, String port,
                            String adminPort) {
    Map<String, String> newEnv = new HashMap<>();
    newEnv.put("timbuctoo_elasticsearch_host", elasticsearchHost);
    newEnv.put("timbuctoo_elasticsearch_port", elasticsearchPort);
    newEnv.put("timbuctoo_elasticsearch_user", elasticsearchUser);
    newEnv.put("timbuctoo_elasticsearch_password", elasticsearchPassword);
    newEnv.put("base_uri", baseUri);
    newEnv.put("timbuctoo_authPath", authPath);
    newEnv.put("timbuctoo_dataPath", dataPath);
    newEnv.put("timbuctoo_port", port);
    newEnv.put("timbuctoo_adminPort", adminPort);
    newEnv.put("timbuctoo_search_url", "");
    newEnv.put("timbuctoo_indexer_url", "");
    try {
      Class<?> processEnvironmentClass = Class.forName("java.lang.ProcessEnvironment");
      Field theEnvironmentField = processEnvironmentClass.getDeclaredField("theEnvironment");
      theEnvironmentField.setAccessible(true);
      Map<String, String> env = (Map<String, String>) theEnvironmentField.get(null);
      env.putAll(newEnv);
      Field theCaseInsensitiveEnvironmentField =
        processEnvironmentClass.getDeclaredField("theCaseInsensitiveEnvironment");
      theCaseInsensitiveEnvironmentField.setAccessible(true);
      Map<String, String> cienv = (Map<String, String>) theCaseInsensitiveEnvironmentField.get(null);
      cienv.putAll(newEnv);
    } catch (NoSuchFieldException e) {
      try {
        Class[] classes = Collections.class.getDeclaredClasses();
        Map<String, String> env = System.getenv();
        for (Class cl : classes) {
          if ("java.util.Collections$UnmodifiableMap".equals(cl.getName())) {
            Field field = cl.getDeclaredField("m");
            field.setAccessible(true);
            Object obj = field.get(env);
            Map<String, String> map = (Map<String, String>) obj;
            map.clear();
            map.putAll(newEnv);
          }
        }
      } catch (Exception e2) {
        e2.printStackTrace();
      }
    } catch (Exception e1) {
      e1.printStackTrace();
    }
  }
}
