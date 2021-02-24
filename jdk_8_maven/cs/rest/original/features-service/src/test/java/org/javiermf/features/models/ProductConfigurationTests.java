package org.javiermf.features.models;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

public class ProductConfigurationTests {

    @Test
    public void shouldReturnProductFeaturesAsAvailable() throws Exception {
        Product product = Product.buildWithFeatures("F1", "F2", "F3");
        ProductConfiguration productConfiguration = new ProductConfiguration();
        productConfiguration.product = product;

        assertThat(productConfiguration.availableFeatures(), hasSize(3));
        assertThat(productConfiguration.availableFeatures(), hasItem("F1"));
        assertThat(productConfiguration.availableFeatures(), hasItem("F2"));
        assertThat(productConfiguration.availableFeatures(), hasItem("F3"));
    }

    @Test
    public void shouldReturnFeatureActiveIfActivated() throws Exception {
        Product product = Product.buildWithFeatures("F1", "F2", "F3");
        ProductConfiguration productConfiguration = new ProductConfiguration();
        productConfiguration.product = product;

        productConfiguration.active(Feature.withName(product, "F1"));

        assertThat(productConfiguration.activedFeatures(), hasSize(1));
        assertThat(productConfiguration.activedFeatures(), hasItem("F1"));
    }

    @Test
    public void shouldReturnTwoFeaturesActiveIfActivated() throws Exception {
        Product product = Product.buildWithFeatures("F1", "F2", "F3");
        ProductConfiguration productConfiguration = new ProductConfiguration();
        productConfiguration.product = product;

        productConfiguration.active(Feature.withName(product, "F1"));
        productConfiguration.active(Feature.withName(product, "F2"));

        assertThat(productConfiguration.activedFeatures(), hasSize(2));
        assertThat(productConfiguration.activedFeatures(), hasItem("F1"));
        assertThat(productConfiguration.activedFeatures(), hasItem("F2"));
    }

    @Test
    public void shouldDeactiveFeatureIfPreviouslyActivated() throws Exception {
        Product product = Product.buildWithFeatures("F1", "F2", "F3");
        ProductConfiguration productConfiguration = new ProductConfiguration();
        productConfiguration.product = product;

        productConfiguration.active(Feature.withName(product, "F1"));
        productConfiguration.active(Feature.withName(product, "F2"));

        productConfiguration.deactive(Feature.withName(product, "F2"));

        assertThat(productConfiguration.activedFeatures(), hasSize(1));
        assertThat(productConfiguration.activedFeatures(), hasItem("F1"));
    }

}
