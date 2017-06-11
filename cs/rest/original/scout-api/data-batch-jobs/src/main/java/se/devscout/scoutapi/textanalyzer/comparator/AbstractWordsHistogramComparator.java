package se.devscout.scoutapi.textanalyzer.comparator;

import se.devscout.scoutapi.textanalyzer.ActivityMetadata;
import se.devscout.scoutapi.textanalyzer.WordHistogram;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

abstract class AbstractWordsHistogramComparator implements ActivityMetadataComparator {
    public AbstractWordsHistogramComparator(Function<ActivityMetadata, String[]> wordExtractor) {
        this.wordExtractor = wordExtractor;
    }

    private final Map<Long, WordHistogram> histograms = new HashMap<>();

    WordHistogram getHistogram(ActivityMetadata rev) {
        long key = rev.id;
        if (!histograms.containsKey(key)) {
            String[] words = wordExtractor.apply(rev);
            histograms.put(key, getHistogram(words));
        }
        return histograms.get(key);
    }

    Function<ActivityMetadata, String[]> wordExtractor;

    @Override
    public double compare(ActivityMetadata rev1, ActivityMetadata rev2) {
        WordHistogram rev1Histogram = getHistogram(rev1);
        WordHistogram rev2Histogram = getHistogram(rev2);
        return rev1Histogram.compare(rev2Histogram);
    }

    private WordHistogram getHistogram(String[] words) {
        WordHistogram histogram = new WordHistogram();

        for (String word : words) {
            if (word != null && word.length() > 0) {
                histogram.countWord(word);
            }
        }
        return histogram;
    }
}
