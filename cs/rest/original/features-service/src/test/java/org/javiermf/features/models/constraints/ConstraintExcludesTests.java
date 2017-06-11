package org.javiermf.features.models.constraints;

import org.javiermf.features.models.Product;
import org.javiermf.features.models.ProductConfiguration;
import org.javiermf.features.models.evaluation.EvaluationResult;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

public class ConstraintExcludesTests {

    @Test
    public void shouldBeEmptyIfNoAddedFeatures() throws Exception {
        String SOURCE_FEATURE = "SOURCE_FEATURE";
        String EXCLUDED_FEATURE = "EXCLUDED_FEATURE";

        ConstraintExcludes excludes = new ConstraintExcludes(SOURCE_FEATURE, EXCLUDED_FEATURE);

        Product product = Product.buildWithFeatures(SOURCE_FEATURE, EXCLUDED_FEATURE);
        product.addFeatureConstraint(excludes);

        ProductConfiguration configuration = new ProductConfiguration();
        configuration.setProduct(product);
        configuration.active(SOURCE_FEATURE);
        configuration.active(EXCLUDED_FEATURE);

        EvaluationResult result = new EvaluationResult();
        excludes.evaluateConfiguration(result, configuration);

        assertThat(result.isValid, is(false));
        assertThat(result.evaluationErrorMessages, hasSize(1));

    }
}
