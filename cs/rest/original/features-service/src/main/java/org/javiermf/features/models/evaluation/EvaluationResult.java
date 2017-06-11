package org.javiermf.features.models.evaluation;

import java.util.HashSet;
import java.util.Set;


public class EvaluationResult {

    public boolean isValid = true;

    public Set<String> derivedFeatures = new HashSet<String>();

    public Set<String> evaluationErrorMessages = new HashSet<String>();


}
