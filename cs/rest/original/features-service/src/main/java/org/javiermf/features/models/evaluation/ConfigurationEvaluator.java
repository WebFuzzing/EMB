package org.javiermf.features.models.evaluation;

import org.javiermf.features.models.ProductConfiguration;
import org.javiermf.features.models.constraints.FeatureConstraint;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class ConfigurationEvaluator {


    public EvaluationResult evaluateConfiguration(ProductConfiguration configuration, Set<FeatureConstraint> featureConstraints) {
        EvaluationResult result = new EvaluationResult();
        result = addEvaluationsToResult(result, configuration, featureConstraints);

        return result;
    }

    private EvaluationResult addEvaluationsToResult(EvaluationResult result, ProductConfiguration configuration, Set<FeatureConstraint> featureConstraints) {
        for (FeatureConstraint featureConstraint : featureConstraints) {
            result = featureConstraint.evaluateConfiguration(result, configuration);
        }

        if (result.isValid && !result.derivedFeatures.isEmpty()) {
            for (String derivedFeature : result.derivedFeatures) {
                configuration.active(derivedFeature);
                result.derivedFeatures.remove(derivedFeature);
                result = this.addEvaluationsToResult(result, configuration, featureConstraints);
            }

        }
        return result;
    }
}
