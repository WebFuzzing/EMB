package org.javiermf.features.models;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductTests {

    @Test
    public void shouldBeEmptyIfNoAddedFeatures() throws Exception {
        Product product = new Product();

        assertThat(product.getProductFeatures(), is(empty()));

    }

    @Test
    public void shouldHaveOneFeatureIfAdded() throws Exception {
        Product product = new Product();
        Feature feature = Feature.withName(product, "FEATURE1");
        product.addFeature(feature);

        assertThat(product.getProductFeatures(), hasSize(1));
        assertThat(product.getProductFeatures(), hasItem(feature));

    }

    @Test
    public void shouldHaveTwoFeatureIfAdded() throws Exception {
        Product product = new Product();
        Feature feature1 = Feature.withName(product, "FEATURE1");
        product.addFeature(feature1);
        Feature feature2 = Feature.withName(product, "FEATURE2");
        product.addFeature(feature2);

        assertThat(product.getProductFeatures(), hasSize(2));
        assertThat(product.getProductFeatures(), hasItem(feature1));
        assertThat(product.getProductFeatures(), hasItem(feature2));

    }

    @Test
    public void shouldHaveOneFeatureIfAddedTwice() throws Exception {
        Product product = new Product();
        Feature feature1 = Feature.withName(product, "FEATURE1");
        product.addFeature(feature1);
        product.addFeature(feature1);

        assertThat(product.getProductFeatures(), hasSize(1));
        assertThat(product.getProductFeatures(), hasItem(feature1));

    }

    @Test
    public void shouldHaveOneFeatureIfAddedTwoWithSameName() throws Exception {
        Product product = new Product();
        Feature feature1 = Feature.withName(product, "FEATURE1");
        product.addFeature(feature1);
        Feature feature2 = Feature.withName(product, "FEATURE1");
        product.addFeature(feature2);

        assertThat(product.getProductFeatures(), hasSize(1));
        assertThat(product.getProductFeatures(), hasItem(feature1));

    }

    @Test
    public void shouldRemoveFeatures() throws Exception {
        Product product = new Product();
        Feature feature1 = Feature.withName(product, "FEATURE1");
        product.addFeature(feature1);
        Feature feature2 = Feature.withName(product, "FEATURE2");
        product.addFeature(feature2);

        product.removeFeature(feature1);

        assertThat(product.getProductFeatures(), hasSize(1));
        assertThat(product.getProductFeatures(), hasItem(feature2));

    }
}
