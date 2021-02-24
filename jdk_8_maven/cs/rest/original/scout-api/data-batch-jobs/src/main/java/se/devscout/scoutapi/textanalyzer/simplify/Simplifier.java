package se.devscout.scoutapi.textanalyzer.simplify;

import se.devscout.scoutapi.textanalyzer.WordHistogram;
import se.devscout.scoutapi.textanalyzer.report.StringMapXmlAdapter;

import javax.xml.bind.JAXBException;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Simplifier {

    private Metadata metadata = new Metadata();

    public Simplifier(List<String> allWords, SimplifyRule[] simplifyRules, int minimumWordGroupSize) throws JAXBException, IOException {
        String[] dictionary = new TreeSet<String>(allWords).toArray(new String[0]);
        initTranslations(dictionary, simplifyRules, minimumWordGroupSize);
        initCommonWords(allWords);
    }

    private void initTranslations(String[] dictionary, SimplifyRule[] simplifyRules, int minimumWordGroupSize) throws JAXBException, IOException {
        TreeMap<String, String> simplifications = new TreeMap<>();

        for (int i = 0; i < dictionary.length; i++) {
            String word = dictionary[i];

            for (SimplifyRule simplifyRule : simplifyRules) {
                Pattern p = simplifyRule.getPattern();
                Matcher matcher = p.matcher(word);
                if (matcher.matches()) {
                    String base = matcher.group(simplifyRule.matchGroupIndex);

                    int followers = 0;
                    while (i + followers + 1 < dictionary.length && dictionary[i + followers + 1].startsWith(base)) {
                        followers++;
                    }

                    if (followers >= minimumWordGroupSize) {
                        if (!word.equals(base)) {
                            simplifications.put(word, base);
                        }
                        for (int x = i + 1; x < i + 1 + followers; x++) {
                            simplifications.put(dictionary[x], base);
                        }
                    }
                    break;
                }
            }
        }

        Set<Map.Entry<String, String>> entries = simplifications.entrySet();
        outer:
        for (Map.Entry<String, String> entry : entries) {
            String to = entry.getValue();
            for (Map.Entry<String, String> entryOther : entries) {
                String toOther = entryOther.getValue();
                if (entry != entryOther && to.equals(toOther)) {
                    continue outer;
                }
            }
            entry.setValue(entry.getKey());
        }

        metadata.translations = simplifications;
    }

    private void initCommonWords(List<String> dictionary) throws IOException {
        Properties fixedWords = new Properties();
        fixedWords.load(getClass().getResourceAsStream("/se/devscout/scoutapi/textanalyzer/common_words.properties"));
        metadata.commonWords = new HashSet<>(fixedWords.stringPropertyNames());

        WordHistogram wordHistogram = new WordHistogram();
        for (String word : dictionary) {
            wordHistogram.countWord(word);
        }
        metadata.commonWords.addAll(wordHistogram.getTop(50).keySet());
    }

    public String simplify(String word) {
        if (metadata.commonWords.contains(word)) {
            return "";
        } else if (metadata.translations.containsKey(word)) {
            return metadata.translations.get(word);
        } else {
            return word;
        }
    }

    public Metadata getMetadata() {
        return metadata;
    }

    @XmlRootElement
    public static class Metadata {
        @XmlElement(name = "v")
        @XmlElementWrapper(name = "commonWords")
        public Set<String> commonWords;
        @XmlElement
        @XmlJavaTypeAdapter(StringMapXmlAdapter.class)
        public Map<String, String> translations;
    }
}
