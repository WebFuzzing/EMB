package org.devgateway.ocds.web.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.reflections.Reflections;
import org.reflections.scanners.ResourcesScanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * Created by mpostelnicu on 2/15/17.
 */
@Service
public class TranslationService {

    private static final String TRANSLATION_PKG_NAME = "public.languages";

    protected static Logger logger = Logger.getLogger(TranslationService.class);


    @Autowired
    private ObjectMapper objectMapper;

    private Map<String, Map<String, String>> translations = new ConcurrentHashMap<>();

    /**
     * Loads all translation files for the given language in the hashmap.
     *
     * @param language
     */
    private void ensureLanguageLoaded(String language) {
        Assert.notNull(language, "Language must not be null!");
        Assert.isTrue(language.matches("^[a-z]{2}_[A-Z]{2}$"),
                "Language string must comply with RFC5646 standard!");

        if (translations.containsKey(language)) {
            return;
        }
        Set<String> fileNames = getAllTranslationFileNamesForLanguage(language);
        Assert.notEmpty(fileNames, "No translations found for language " + language);

        fileNames.forEach(file ->
                loadLanguageFromTranslationFile(language, file));

        logger.info("Loaded translations for language " + language + " from the following files " + fileNames);
    }

    /**
     * Returns all the translation files for a specific language, sorted naturally by file name
     * The trnaslation files have to start with @param language and end with ".json"
     *
     * @param language
     * @return
     */
    private Set<String> getAllTranslationFileNamesForLanguage(String language) {
        return new TreeSet<String>(new Reflections(TRANSLATION_PKG_NAME, new ResourcesScanner()).
                getResources(Pattern.compile(language + "(.*).json")));
    }

    /**
     * Loads all translation keys from the given file. If the keys already exist, they will be overwritten.
     *
     * @param language
     * @param translationFileName
     */
    private void loadLanguageFromTranslationFile(String language, String translationFileName) {
        try {
            InputStream stream = this.getClass().getResourceAsStream("/" + translationFileName);
            Map<String, String> map = objectMapper.readValue(stream, ConcurrentHashMap.class);
            if (translations.containsKey(language)) {
                translations.get(language).putAll(map);
            } else {
                translations.put(language, map);
            }
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
        Assert.notNull(key, "Key must not be null!");
        ensureLanguageLoaded(language);
        return translations.get(language).get(key);
    }

    /**
     * Returns the entire map of key-value translations for given language
     *
     * @param language
     * @return
     */
    public Map<String, String> getAllTranslationsForLanguage(String language) {
        ensureLanguageLoaded(language);
        return translations.get(language);
    }

}
