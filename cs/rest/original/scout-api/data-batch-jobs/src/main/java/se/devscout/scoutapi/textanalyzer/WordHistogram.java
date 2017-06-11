package se.devscout.scoutapi.textanalyzer;

import java.util.*;

public class WordHistogram {
    private final Map<String, Integer> wordCounts = new HashMap<>();
    private Map<String, Double> percentages;

    public void countWord(String word) {
        if (!wordCounts.containsKey(word)) {
            wordCounts.put(word, 1);
        } else {
            wordCounts.put(word, wordCounts.get(word) + 1);
        }
        percentages = null;
    }

    Map<String, Double> getPercentages() {
        if (percentages == null) {
            percentages = new HashMap<>();
            int sum = 0;
            for (Integer count : wordCounts.values()) {
                sum += count;
            }
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                percentages.put(entry.getKey(), 1.0 * entry.getValue() / sum);
            }
        }
        return percentages;
    }

    public double compare(WordHistogram that) {
        double sum = 0;
        Map<String, Double> thisPercentages = getPercentages();
        Map<String, Double> thatPercentages = that.getPercentages();
        for (Map.Entry<String, Double> entry : thisPercentages.entrySet()) {
            Double thisPercent = entry.getValue();
            Double thatPercent = thatPercentages.get(entry.getKey());
            if (thatPercent != null) {
                sum += (thisPercent + thatPercent) / 2;
            }
        }
        return sum;
    }

    public Map<String, Integer> getTop(int limit) {
        ArrayList<Map.Entry<String, Integer>> entries = new ArrayList<>(wordCounts.entrySet());
        Collections.sort(entries, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        Map<String, Integer> topWords = new TreeMap<>();
        for (int i = 0; i < Math.min(entries.size(), limit); i++) {
            Map.Entry<String, Integer> entry = entries.get(i);
            topWords.put(entry.getKey(), entry.getValue());
        }
        return topWords;
    }
}
