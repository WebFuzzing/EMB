package org.javiermf.features.services.rest;

import org.apache.http.HttpStatus;
import org.javiermf.features.Application;
import org.javiermf.features.daos.ProductsDAO;
import org.javiermf.features.models.Feature;
import org.javiermf.features.models.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.Map;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Sql({"/empty-db.sql", "/data-test.sql"})
@WebIntegrationTest
public class ProductsFeaturesIntegrationTests {

    @Autowired
    ProductsDAO productsDAO;

    @Test
    public void canFetchProductsFeatures() throws Exception {
        List boydResponse =
                when().
                        get("/products/Product_1/features").
                        then().
                        statusCode(HttpStatus.SC_OK).
                        and()
                        .extract().response().as(List.class);

        assertThat(boydResponse, hasSize(3));

        Map<String, String> featureObject = (Map<String, String>) boydResponse.get(0);
        assertThat(featureObject.get("name"), is(anyOf(equalTo("Feature_1"), equalTo("Feature_2"), equalTo("Feature_3"))));

    }

    @Test
    public void canAddProductsFeatures() throws Exception {
        given().
                formParam("description", "New Feature Description").
                when().
                post("/products/Product_1/features/newFeature").
                then().
                statusCode(HttpStatus.SC_CREATED);

        Product product = productsDAO.findByName("Product_1");

        assertThat(product.getProductFeatures(), hasSize(4));

        Feature newFeature = product.findProductFeatureByName("newFeature");
        assertThat(newFeature, is(notNullValue()));
        assertThat(newFeature.getDescription(), is(equalTo("New Feature Description")));
    }

    @Test
    public void canNotAddDuplicatedProductsFeatures() throws Exception {
        given().
                formParam("description", "Existing Feature Description").
                when().
                post("/products/Product_1/features/Feature_1").
                then().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        Product product = productsDAO.findByName("Product_1");

        assertThat(product.getProductFeatures(), hasSize(3));

    }

    @Test
    public void canUpdateProductsFeatures() throws Exception {
        Feature feature = given().
                formParam("description", "New Feature Description").
                when().
                put("/products/Product_1/features/Feature_1").
                then().
                statusCode(HttpStatus.SC_OK).
                and()
                .extract().response().as(Feature.class);

        assertThat(feature.getDescription(), is(equalTo("New Feature Description")));

        Product product = productsDAO.findByName("Product_1");
        Feature updatedFeature = product.findProductFeatureByName("Feature_1");
        assertThat(updatedFeature.getDescription(), is(equalTo("New Feature Description")));
    }

    @Test
    public void canDeleteAProductsFeatures() throws Exception {
        when().
                delete("/products/Product_1/features/Feature_1").
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);


        Product product = productsDAO.findByName("Product_1");
        assertThat(product.getProductFeatures(), hasSize(2));
    }

}
