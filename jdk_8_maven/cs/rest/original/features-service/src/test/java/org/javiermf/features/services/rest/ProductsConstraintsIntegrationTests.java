package org.javiermf.features.services.rest;

import org.apache.http.HttpStatus;
import org.javiermf.features.Application;
import org.javiermf.features.daos.ProductsDAO;
import org.javiermf.features.models.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Sql({"/empty-db.sql", "/data-test.sql"})
@WebIntegrationTest
public class ProductsConstraintsIntegrationTests {

    @Autowired
    ProductsDAO productsDAO;


    @Test
    public void canAddRequiresConstraint() throws Exception {
        given().
                formParam("sourceFeature", "Feature_1").
                formParam("requiredFeature", "Feature_2").
                when().
                post("/products/Product_1/constraints/requires").
                then().
                statusCode(HttpStatus.SC_CREATED);

        Product product = productsDAO.findByName("Product_1");

        assertThat(product.getProductFeatureConstraints(), hasSize(2));

    }

    @Test
    public void canAddExcludesConstraint() throws Exception {
        given().
                formParam("sourceFeature", "Feature_1").
                formParam("excluded", "Feature_2").
                when().
                post("/products/Product_1/constraints/excludes").
                then().
                statusCode(HttpStatus.SC_CREATED);

        Product product = productsDAO.findByName("Product_1");

        assertThat(product.getProductFeatureConstraints(), hasSize(2));

    }

    @Test
    public void canDeleteAProductConstraint() throws Exception {
        when().
                delete("/products/Product_1/constraints/1").
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);


        Product product = productsDAO.findByName("Product_1");
        assertThat(product.getProductFeatureConstraints(), hasSize(0));
    }

}
