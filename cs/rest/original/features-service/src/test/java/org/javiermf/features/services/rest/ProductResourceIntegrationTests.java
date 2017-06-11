package org.javiermf.features.services.rest;

import org.apache.http.HttpStatus;
import org.javiermf.features.Application;
import org.javiermf.features.daos.ProductsDAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static com.jayway.restassured.RestAssured.when;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Sql({"/empty-db.sql", "/data-test.sql"})
@WebIntegrationTest
public class ProductResourceIntegrationTests {

    @Autowired
    ProductsDAO productsDAO;

    @Test
    public void canFetchProducts() throws Exception {
        List boydResponse =
                when().
                        get("/products").
                        then().
                        statusCode(HttpStatus.SC_OK).
                        and()
                        .extract().response().as(List.class);

        assertThat(boydResponse, hasSize(2));

    }

    @Test
    public void canFetchAProduct() throws Exception {
        when().
                get("/products/Product_1").
                then().
                statusCode(HttpStatus.SC_OK).
                and().
                body("name", is("Product_1")).
                body("features", hasSize(3)).
                body("constraints", hasSize(1))
        ;

    }

    @Test
    public void canDeleteAProduct() throws Exception {
        when().
                delete("/products/Product_1").
                then().
                statusCode(HttpStatus.SC_NO_CONTENT);

        assertThat(productsDAO.findAll(), hasSize(1));
    }

    @Test
    public void canAddAProduct() throws Exception {
        when().
                post("/products/Product_3").
                then().
                statusCode(HttpStatus.SC_CREATED);

        assertThat(productsDAO.findAll(), hasSize(3));
    }


}
