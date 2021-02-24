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

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Sql({"/empty-db.sql", "/data-test.sql"})
@WebIntegrationTest
public class ConstraintRequiresIntegrationTests {

    @Autowired
    ProductsConfigurationsDAO productsConfigurationsDAO;


    @Test
    public void requiresConstraintAddRequiredFeature() throws Exception {
        given().
                when().
                post("/products/Product_1/configurations/Product_1_Configuration_2/features/Feature_3").
                then().
                statusCode(HttpStatus.SC_CREATED);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_1", "Product_1_Configuration_2");
        assertThat(configuration.activedFeatures(), hasSize(3));
        assertThat(configuration.isValid(), is(true));
    }
}
