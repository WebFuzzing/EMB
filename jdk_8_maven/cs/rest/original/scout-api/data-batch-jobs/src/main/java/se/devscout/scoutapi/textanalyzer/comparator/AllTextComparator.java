package se.devscout.scoutapi.textanalyzer.comparator;

public class AllTextComparator extends AbstractWordsHistogramComparator {
    public AllTextComparator() {
        super(activity -> activity.getAllWords());
    }

    @Override
    public String toString() {
        return "texts";
    }
}
