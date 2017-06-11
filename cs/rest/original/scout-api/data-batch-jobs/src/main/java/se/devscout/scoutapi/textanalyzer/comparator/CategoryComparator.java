package se.devscout.scoutapi.textanalyzer.comparator;

import com.google.common.collect.Sets;
import org.apache.commons.lang3.ArrayUtils;
import se.devscout.scoutapi.textanalyzer.ActivityMetadata;

import java.util.stream.Collectors;

public class CategoryComparator implements ActivityMetadataComparator {
    @Override
    public double compare(ActivityMetadata rev1, ActivityMetadata rev2) {
        if (rev1.tagIds.length > 0 && rev2.tagIds.length > 0) {
            int max = rev1.tagIds.length + rev2.tagIds.length;
            int sharedTagsCount = 0;
            for (int i = 0; i < rev1.tagIds.length; i++) {
                long id1 = rev1.tagIds[i];
                for (int j = 0; j < rev2.tagIds.length; j++) {
                    long id2 = rev2.tagIds[j];
                    if (id1 == id2) {
                        sharedTagsCount++;
                    }
                }
            }
            return 1.0 * (sharedTagsCount * 2) / max;
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        return "labels";
    }
}
