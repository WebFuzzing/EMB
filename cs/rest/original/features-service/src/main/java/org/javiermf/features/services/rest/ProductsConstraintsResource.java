package org.javiermf.features.services.rest;

import org.javiermf.features.models.constraints.FeatureConstraint;
import org.javiermf.features.services.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;

@Component
@Produces("application/json")
public class ProductsConstraintsResource {

    @Autowired
    ProductsService productsService;

    @POST
    @Path("requires")
    public Response addRequiresConstraintToProduct(@PathParam("productName") String productName,
                                                   @FormParam("sourceFeature") String sourceFeatureName,
                                                   @FormParam("requiredFeature") String requiredFeatureName
    ) throws URISyntaxException {
        FeatureConstraint newConstraint = productsService.addRequiresConstraintToProduct(productName, sourceFeatureName, requiredFeatureName);
        return Response.created(new URI("/products/" + productName + "/constraints/" + newConstraint.getId())).build();

    }

    @POST
    @Path("excludes")
    public Response addExcludesConstraintToProduct(@PathParam("productName") String productName,
                                                   @FormParam("sourceFeature") String sourceFeatureName,
                                                   @FormParam("excludedFeature") String excludedFeatureName
    ) throws URISyntaxException {
        FeatureConstraint newConstraint = productsService.addExcludesConstraintToProduct(productName, sourceFeatureName, excludedFeatureName);
        return Response.created(new URI("/products/" + productName + "/constraint/" + newConstraint.getId())).build();

    }

    @DELETE
    @Path("{constraintId}")
    public Response deleteConstraint(@PathParam("productName") String productName,
                                     @PathParam("constraintId") Long constraintId
    ) throws URISyntaxException {
        productsService.deleteConstraintFromProduct(productName, constraintId);
        return Response.noContent().build();

    }
}
