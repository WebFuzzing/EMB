package se.devscout.scoutapi.textanalyzer;

import se.devscout.scoutapi.textanalyzer.simplify.SimplifyRule;

import javax.validation.Valid;
import java.io.File;
import java.util.regex.Pattern;

public class Configuration {
    public int maxRelated = 5;

    public boolean simplifyVocabulary = true;
    public SimplifyRule[] simplifyRules = new SimplifyRule[]{
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})(ing|ingen|ings|ingens|ingar|ingarna)")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})[lt](iga|igt)")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})(nare|nskt|nens|nens)")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})(nar|are|ade|skt|ens|nen)")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})(na|re)")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})[aeis][nrtsk]")),
            new SimplifyRule(1, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})[aens]")),
            new SimplifyRule(0, Pattern.compile("([a-zåäöA-ZÅÄÖ]{4,})"))
    };
    public int minimumWordGroupSize = 2;

    public double comparatorFactorAllText = 1.0;
    public double comparatorFactorName = 0.5;
    public double comparatorFactorMaterials = 1.0;
    public double comparatorFactorIntroduction = 2.0;
    public double comparatorFactorCategories = 0.5;
    public double comparatorFactorAge = 0.1;
    public double comparatorFactorParticipantCount = 0.1;
    public double comparatorFactorTime = 0.1;

    @Valid
    public File reportFolder;
    private String relationOwner;

    /**
     * Number of reports to keep. Set to 0 in order to disable reporting. Logging will be done regardless.
     */
    public int reportFilesRetentionLimit = 10;

    public String getRelationOwner() {
        return relationOwner;
    }

    public void setRelationOwner(String relationOwner) {
        this.relationOwner = relationOwner;
    }
}
