package org.devgateway.ocds.web.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by mpostelnicu on 2/15/17.
 */
@Service
public class TranslationService {

    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();


    private void ensureLanguageLoaded(String language) {
        if (translations.containsKey(language)) {
            return;
        }

        try {
            InputStream stream = this.getClass().getResourceAsStream("/public/languages/" + language + ".json");
            Map<String, String> map = objectMapper.readValue(stream, ConcurrentHashMap.class);


            translations.put(language, map);



        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the translation registered for the given language and key.
     * The language uses RFC5646
     *
     * @param language
     * @param key
     * @return
     */
    public String getValue(String language, String key) {
        ensureLanguageLoaded(language);
        return translations.get(language).get(key);
    }


}
