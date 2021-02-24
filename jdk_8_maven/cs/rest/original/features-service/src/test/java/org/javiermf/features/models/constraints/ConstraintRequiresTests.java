package org.javiermf.features.models.constraints;

import org.javiermf.features.models.Product;
import org.javiermf.features.models.ProductConfiguration;
import org.javiermf.features.models.evaluation.EvaluationResult;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ConstraintRequiresTests {

    @Test
    public void shouldBeEmptyIfNoAddedFeatures() throws Exception {
        String SOURCE_FEATURE = "SOURCE_FEATURE";
        String REQUIRED_FEATURE = "REQUIRED_FEATURE";

        ConstraintRequires requires = new ConstraintRequires(SOURCE_FEATURE, REQUIRED_FEATURE);

        Product product = Product.buildWithFeatures(SOURCE_FEATURE, REQUIRED_FEATURE);
        product.addFeatureConstraint(requires);

        ProductConfiguration configuration = new ProductConfiguration();
        configuration.setProduct(product);
        configuration.active(SOURCE_FEATURE);

        EvaluationResult result = new EvaluationResult();
        requires.evaluateConfiguration(result, configuration);

        assertThat(result.isValid, is(true));
        assertThat(result.derivedFeatures, hasSize(1));
        assertThat(result.derivedFeatures, contains(REQUIRED_FEATURE));

    }
}
