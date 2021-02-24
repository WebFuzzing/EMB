package org.javiermf.features.services.rest;


import io.swagger.annotations.Api;
import org.javiermf.features.models.Product;
import org.javiermf.features.services.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


@Component
@Path("/products")
@Produces("application/json")
@Api
public class ProductsResource {

    @Autowired
    ProductsService productsService;

    @Autowired
    ProductsConfigurationResource productsConfigurationResource;

    @Autowired
    ProductsFeaturesResource productsFeaturesResource;

    @Autowired
    ProductsConstraintsResource productsConstraintsResource;


    @GET
    public List<String> getAllProducts() {
        return productsService.getAllProductNames();
    }

    @Path("{productName}")
    @GET
    public Product getProductByName(@PathParam("productName") String productName) {
        return productsService.findByName(productName);
    }

    @Path("{productName}")
    @DELETE
    public Response deleteProductByName(@PathParam("productName") String productName) {
        productsService.deleteByName(productName);
        return Response.noContent().build();
    }

    @Path("{productName}")
    @POST
    public Response addProduct(@PathParam("productName") String productName) throws URISyntaxException {
        productsService.add(productName);
        return Response.created(new URI("/products/" + productName)).build();
    }


    @Path("{productName}/configurations")
    public ProductsConfigurationResource productsConfigurationResource() {
        return productsConfigurationResource;
    }

    @Path("{productName}/features")
    public ProductsFeaturesResource productsFeaturesResource() {
        return productsFeaturesResource;
    }

    @Path("{productName}/constraints")
    public ProductsConstraintsResource productsConstraintsResource() {
        return productsConstraintsResource;
    }

}
