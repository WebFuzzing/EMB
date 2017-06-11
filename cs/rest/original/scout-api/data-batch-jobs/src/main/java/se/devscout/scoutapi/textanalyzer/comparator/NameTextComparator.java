package se.devscout.scoutapi.textanalyzer.comparator;

public class NameTextComparator extends AbstractWordsHistogramComparator {
    public NameTextComparator() {
        super(activity -> activity.name_words);
    }

    @Override
    public String toString() {
        return "name";
    }
}
