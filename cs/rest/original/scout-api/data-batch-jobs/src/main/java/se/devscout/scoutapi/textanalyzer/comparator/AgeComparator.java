package se.devscout.scoutapi.textanalyzer.comparator;

import se.devscout.scoutapi.textanalyzer.ActivityMetadata;

public class AgeComparator extends AbstractRangeComparator {
    @Override
    public double compare(ActivityMetadata rev1, ActivityMetadata rev2) {
        return compare(rev1.ageMin, rev1.ageMax, rev2.ageMin, rev2.ageMax, 8, 25);
    }

    @Override
    public String toString() {
        return "age";
    }
}
