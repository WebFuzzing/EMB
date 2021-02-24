package se.devscout.scoutapi.textanalyzer.comparator;

import se.devscout.scoutapi.textanalyzer.ActivityMetadata;

public class TimeComparator extends AbstractRangeComparator {
    @Override
    public double compare(ActivityMetadata rev1, ActivityMetadata rev2) {
        return compare(rev1.timeMin, rev1.timeMax, rev2.timeMin, rev2.timeMax, 1, 120);
    }

    @Override
    public String toString() {
        return "time";
    }
}
