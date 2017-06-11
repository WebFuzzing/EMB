package se.devscout.scoutapi.textanalyzer.comparator;

import se.devscout.scoutapi.textanalyzer.ActivityMetadata;

public interface ActivityMetadataComparator {
    double compare(ActivityMetadata rev1, ActivityMetadata rev2);
}
