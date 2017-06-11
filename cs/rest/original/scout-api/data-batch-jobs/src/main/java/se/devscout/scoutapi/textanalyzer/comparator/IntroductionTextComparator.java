package se.devscout.scoutapi.textanalyzer.comparator;

public class IntroductionTextComparator extends AbstractWordsHistogramComparator {
    public IntroductionTextComparator() {
        super(activity -> activity.descr_introduction_words);
    }

    @Override
    public String toString() {
        return "intro";
    }
}
