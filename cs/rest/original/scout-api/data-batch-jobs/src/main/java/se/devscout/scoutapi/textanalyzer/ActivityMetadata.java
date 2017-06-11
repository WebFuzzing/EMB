package se.devscout.scoutapi.textanalyzer;

import se.devscout.scoutapi.model.Activity;
import se.devscout.scoutapi.textanalyzer.simplify.Simplifier;

import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ActivityMetadata {
    public final Long[] tagIds;
    public final Integer participantsMin;
    public final Integer participantsMax;
    public final Integer timeMin;
    public final Integer timeMax;
    public final Integer ageMin;
    public final Integer ageMax;
    public final long id;
    public final String name;
    public String[] name_words;
    public String[] descr_introduction_words;
    private String[] descr_safety_words;
    private String[] descr_main_words;
    public String[] descr_material_words;
    private String[] descr_notes_words;
    private String[] descr_prepare_words;
    private String[] all_words;


    public ActivityMetadata(Activity source) {
        descr_introduction_words = getWordSequence(source.getProperties().getDescriptionIntroduction());
        name_words = getWordSequence(source.getProperties().getName());
        descr_main_words = getWordSequence(source.getProperties().getDescriptionMain());
        descr_material_words = getWordSequence(source.getProperties().getDescriptionMaterial());
        descr_notes_words = getWordSequence(source.getProperties().getDescriptionNotes());
        descr_prepare_words = getWordSequence(source.getProperties().getDescriptionPrepare());
        descr_safety_words = getWordSequence(source.getProperties().getDescriptionSafety());
        tagIds = source.getProperties().getTags().stream().map(tag -> tag.getId()).distinct().toArray(value -> new Long[value]);
        participantsMax = source.getProperties().getParticipantsMax();
        participantsMin = source.getProperties().getParticipantsMin();
        timeMax = source.getProperties().getTimeMax();
        timeMin = source.getProperties().getTimeMin();
        ageMax = source.getProperties().getAgeMax();
        ageMin = source.getProperties().getAgeMin();
        id = source.getId();
        name = source.getProperties().getName();
    }

    private String[] getWordSequence(String text) {
        if (text != null) {
            return text.toLowerCase().split("[^\\p{IsAlphabetic}]+");
        } else {
            return new String[0];
        }
    }

    public void simplifyVocabulary(Simplifier simplifier) {
        all_words = null;
//        for (int i = 0; i < name_words().length; i++) {
//            name_words[i] = simplifier.simplify(name_words[i]);
//        }
        for (int i = 0; i < descr_introduction_words.length; i++) {
            descr_introduction_words[i] = simplifier.simplify(descr_introduction_words[i]);
        }
        for (int i = 0; i < descr_safety_words.length; i++) {
            descr_safety_words[i] = simplifier.simplify(descr_safety_words[i]);
        }
        for (int i = 0; i < descr_main_words.length; i++) {
            descr_main_words[i] = simplifier.simplify(descr_main_words[i]);
        }
        for (int i = 0; i < descr_material_words.length; i++) {
            descr_material_words[i] = simplifier.simplify(descr_material_words[i]);
        }
        for (int i = 0; i < descr_notes_words.length; i++) {
            descr_notes_words[i] = simplifier.simplify(descr_notes_words[i]);
        }
        for (int i = 0; i < descr_prepare_words.length; i++) {
            descr_prepare_words[i] = simplifier.simplify(descr_prepare_words[i]);
        }
        all_words = null;
    }

    public String[] getAllWords() {
        if (all_words == null) {
            all_words = Stream.of(name_words,
                    descr_introduction_words,
                    descr_main_words,
                    descr_material_words,
                    descr_notes_words,
                    descr_prepare_words,
                    descr_safety_words).flatMap(strings -> Arrays.stream(strings)).toArray(size -> new String[size]);
        }
        return all_words;
    }
}
