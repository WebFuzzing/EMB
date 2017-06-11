package se.devscout.scoutapi.textanalyzer.comparator;

import se.devscout.scoutapi.textanalyzer.ActivityMetadata;

public class ParticipantCountComparator extends AbstractRangeComparator {
    @Override
    public double compare(ActivityMetadata rev1, ActivityMetadata rev2) {
        return compare(rev1.participantsMin, rev1.participantsMax, rev2.participantsMin, rev2.participantsMax, 1, 20);
    }

    @Override
    public String toString() {
        return "grpsz";
    }
}
