package org.javiermf.features.services.rest;

import org.apache.http.HttpStatus;
import org.javiermf.features.Application;
import org.javiermf.features.daos.ProductsConfigurationsDAO;
import org.javiermf.features.models.ProductConfiguration;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Sql({"/empty-db.sql", "/data-test.sql"})
@WebIntegrationTest
public class ProductConfigurationsIntegrationTests {

    @Autowired
    ProductsConfigurationsDAO productsConfigurationsDAO;


    @Test
    public void canFetchConfigurations() throws Exception {
        List boydResponse =
                when().
                        get("/products/Product_1/configurations").
                        then().
                        statusCode(HttpStatus.SC_OK).
                        and()
                        .extract().response().as(List.class);

        assertThat(boydResponse, hasSize(2));

    }

    @Test
    public void canFetchAConfiguration() throws Exception {
        when().
                get("/products/Product_1/configurations/Product_1_Configuration_2").
                then().
                statusCode(HttpStatus.SC_OK).
                and().
                body("name", is("Product_1_Configuration_2")).
                body("activedFeatures", hasSize(1));

    }

    @Test
    public void canAddProductsConfigurations() throws Exception {
        when().
                post("/products/Product_1/configurations/newConfig").
                then().
                statusCode(HttpStatus.SC_CREATED);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_1", "newConfig");

        assertThat(configuration, is(notNullValue()));
        assertThat(configuration.getProduct().getName(), is(equalTo("Product_1")));
        assertThat(configuration.isValid(), is(true));
    }

    @Test
    public void canDeleteAProductConfiguration() throws Exception {
        when().
                delete("/products/Product_1/configurations/Product_1_Configuration_1").
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        assertThat(productsConfigurationsDAO.findByProductName("Product_1"), hasSize(1));
    }

    @Test
    public void canAddAnActivedFeatureToAConfiguration() throws Exception {
        when().
                post("/products/Product_1/configurations/Product_1_Configuration_2/features/Feature_2").
                then().
                statusCode(HttpStatus.SC_CREATED);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_1", "Product_1_Configuration_2");

        assertThat(configuration.getActivedFeatures(), hasSize(2));
    }

    @Test
    public void canNotAddDuplicatedActivedFeatureToAConfiguration() throws Exception {
        when().
                post("/products/Product_1/configurations/Product_1_Configuration_2/features/Feature_1").
                then().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_1", "Product_1_Configuration_2");

        assertThat(configuration.getActivedFeatures(), hasSize(1));
    }

    @Test
    public void canNotAddAFeatureOfOtherProductToAConfiguration() throws Exception {
        when().
                post("/products/Product_1/configurations/Product_1_Configuration_2/features/Feature_B").
                then().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);
    }



    @Test
    public void canDeleteAActivedFeatureFromAConfiguration() throws Exception {
        when().
                delete("/products/Product_1/configurations/Product_1_Configuration_1/features/Feature_1").
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_1", "Product_1_Configuration_1");
        assertThat(configuration.getActivedFeatures(), hasSize(1));

    }

}
