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
public class ConstraintExcludesIntegrationTests {

    @Autowired
    ProductsConfigurationsDAO productsConfigurationsDAO;

    @Test
    public void requiresExcludesInvalidatesConfiguration() throws Exception {
        given().
                when().
                post("/products/Product_2/configurations/Product_2_Configuration_1/features/Feature_C").
                then().
                statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR);

        ProductConfiguration configuration = productsConfigurationsDAO.findByNameAndProductName("Product_2", "Product_2_Configuration_1");
        assertThat(configuration.activedFeatures(), hasSize(3));
        assertThat(configuration.isValid(), is(false));
    }
}
