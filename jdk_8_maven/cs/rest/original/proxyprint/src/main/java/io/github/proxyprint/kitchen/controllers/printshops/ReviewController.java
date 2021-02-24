/*
 * Copyright 2016 Pivotal Software, Inc..
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.proxyprint.kitchen.controllers.printshops;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.Review;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.ReviewDAO;
import io.swagger.annotations.ApiOperation;
import java.security.Principal;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

/**
 *
 * @author jose
 */
@RestController 
@Transactional
public class ReviewController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private PrintShopDAO printshops;
    @Autowired
    private ReviewDAO reviews;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns all printshop reviews", notes = "404 if the printshop doesn't exist.")
    @Secured({"ROLE_MANAGER", "ROLE_EMPLOYEE", "ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/reviews", method = RequestMethod.GET)
    public ResponseEntity<String> getPrintShopReviews(@PathVariable("id") long id) {
        PrintShop pShop = this.printshops.findOne(id);
        if (pShop == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else {
            Set<Review> reviews = pShop.getReviews();
            JsonObject review;
            JsonArray obj = new JsonArray();
            for (Review r : reviews){
                review =  new JsonObject();
                review.addProperty("id", r.getId());
                review.addProperty("description", r.getDescription());
                review.addProperty("rating", r.getRating());
                review.addProperty("username", r.getConsumer().getName());
                obj.add(review);
            }
            return new ResponseEntity(this.GSON.toJson(obj), HttpStatus.OK);
        }
    }

    @ApiOperation(value = "Add a review to a printshop with the given ID", notes = "404 if the printshop doesn't exist.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/printshops/{id}/reviews", method = RequestMethod.POST)
    public ResponseEntity<String> addPrintShopReview(@PathVariable("id") long id, Principal principal, @RequestBody Map<String, String> params) {
        PrintShop pShop = this.printshops.findOne(id);
        if (pShop == null) {
           return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        Consumer consumer = this.consumers.findByUsername(principal.getName());
        String reviewText = params.get("review");
        int rating = Integer.parseInt(params.get("rating"));
        Review review = reviews.save(new Review(reviewText, rating, consumer));
        pShop.addReview(review);
        pShop.updatePrintShopRating();
        this.printshops.save(pShop);
        return new ResponseEntity(this.GSON.toJson(review), HttpStatus.OK);
    }

    @ApiOperation(value = "Edit an existing printshop review", notes = "404 if the printshop or the review doesn't exist.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/printshops/{printShopId}/reviews/{reviewId}", method = RequestMethod.PUT)
    public ResponseEntity<String> editPrintShopReview(@PathVariable("printShopId") long printShopId, @PathVariable("reviewId") long reviewId, Principal principal, WebRequest request) {
        PrintShop pShop = this.printshops.findOne(printShopId);
        if (pShop == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Review review = this.reviews.findOne(reviewId);
        if (review == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (!review.getConsumer().getUsername().equals(principal.getName())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        pShop.removeReview(review);

        String reviewText = request.getParameter("review");
        int rating = Integer.parseInt(request.getParameter("rating"));

        review.setDescription(reviewText);
        review.setRating(rating);

        pShop.addReview(review);
        pShop.updatePrintShopRating();

        this.printshops.save(pShop);
        return new ResponseEntity(this.GSON.toJson(review), HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing printshop review", notes = "404 if the printshop or the review doesn't exist.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/printshops/{printShopId}/reviews/{reviewId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deletePrintShopReview(@PathVariable("printShopId") long printShopId, @PathVariable("reviewId") long reviewId, Principal principal, WebRequest request) {
        PrintShop pShop = this.printshops.findOne(printShopId);
        if (pShop == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }

        Review review = this.reviews.findOne(reviewId);
        if (review == null) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        } else if (!review.getConsumer().getUsername().equals(principal.getName())) {
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        pShop.removeReview(review);
        this.printshops.save(pShop);
        return new ResponseEntity(this.GSON.toJson(review), HttpStatus.OK);
    }
}
