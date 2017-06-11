package se.devscout.scoutapi.textanalyzer.simplify;

import java.util.regex.Pattern;

public class SimplifyRule {
    public String patternExpr;

    private Pattern pattern;

    public int matchGroupIndex;

    public SimplifyRule() {
    }

    public SimplifyRule(int matchGroupIndex, Pattern pattern) {
        this.matchGroupIndex = matchGroupIndex;
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        if (pattern == null) {
            pattern = Pattern.compile(patternExpr);
        }
        return pattern;
    }
}
