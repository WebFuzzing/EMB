package org.devgateway.ocds.persistence.mongo.flags;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public final class FlagsConstants {

    public static final String I038_VALUE = "flags.i038.value";
    public static final String I003_VALUE = "flags.i003.value";
    public static final String I007_VALUE = "flags.i007.value";
    public static final String I004_VALUE = "flags.i004.value";
    public static final String I019_VALUE = "flags.i019.value";
    public static final String I077_VALUE = "flags.i077.value";
    public static final String I180_VALUE = "flags.i180.value";
    public static final String I002_VALUE = "flags.i002.value";
    public static final String I085_VALUE = "flags.i085.value";
    public static final String I171_VALUE = "flags.i171.value";

    public static final List<String> FLAGS_LIST = Collections.unmodifiableList(
            Arrays.asList(new String[]{I038_VALUE, I004_VALUE, I007_VALUE, I019_VALUE,
                    I077_VALUE, I180_VALUE, I002_VALUE, I085_VALUE, I171_VALUE, I003_VALUE}));

    private FlagsConstants() {
    }

}
