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
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.printshops.Manager;
import io.github.proxyprint.kitchen.models.printshops.PrintShop;
import io.github.proxyprint.kitchen.models.printshops.RegisterRequest;
import io.github.proxyprint.kitchen.models.repositories.ManagerDAO;
import io.github.proxyprint.kitchen.models.repositories.PrintShopDAO;
import io.github.proxyprint.kitchen.models.repositories.RegisterRequestDAO;
import io.github.proxyprint.kitchen.utils.MailBox;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author josesousa
 */
@RestController 
@Transactional
public class RegisterRequestController {

    @Autowired
    private RegisterRequestDAO registerRequests;
    @Autowired
    private PrintShopDAO printShops;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private Gson GSON;

    @ApiOperation(value = "Returns success.", notes = "This method allows to add a register request to a new printshop.")
    @RequestMapping(value = "/request/register", method = RequestMethod.POST)
    public ResponseEntity<RegisterRequest> registerRequest(@RequestBody RegisterRequest registerRequest) {
        registerRequest = registerRequests.save(registerRequest);
        return new ResponseEntity<>(registerRequest, HttpStatus.OK);
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows the admin to accept a register request from a printshop.")
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/request/accept/{id}", method = RequestMethod.POST)
    public String acceptRequest(@PathVariable(value = "id") long id) throws IOException {
        RegisterRequest registerRequest = registerRequests.findOne(id);
        JsonObject response = new JsonObject();
        if (registerRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        } else {

            // Create Manager instance
            Manager newManager = new Manager(registerRequest.getManagerUsername(),
                    registerRequest.getManagerPassword(),
                    registerRequest.getManagerName(),
                    registerRequest.getManagerEmail()
            );

            Manager savedManager = managers.save(newManager);

            if (savedManager != null) {
                // Create PrintShop instance
                PrintShop newPShop = new PrintShop(registerRequest.getpShopName(),
                        registerRequest.getpShopAddress(),
                        registerRequest.getpShopLatitude(),
                        registerRequest.getpShopLongitude(),
                        registerRequest.getpShopNIF(),
                        "path_to_logo",
                        0
                );
                PrintShop savedPShop = printShops.save(newPShop);

                if (savedPShop != null) {
                    savedManager.setPrintShop(savedPShop);
                    managers.save(savedManager);

                    // Delete request, is no longer needed
                    registerRequests.delete(registerRequest.getId());

                    // Send email
                    MailBox m = new MailBox();
                    boolean res = m.sedMailAcceptedRequest(registerRequest);

                    response.addProperty("success", true);
                    return GSON.toJson(response);
                }
            }
        }
        response.addProperty("success", true);
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows the admin to reject a register request from a printshop.")
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/request/reject/{printRequestID}", method = RequestMethod.POST)
    public String rejectRequest(@PathVariable(value = "printRequestID") long prid, Principal principal, @RequestBody String motive) throws IOException {
        RegisterRequest registerRequest = registerRequests.findOne(prid);
        JsonObject response = new JsonObject();
        if (registerRequest == null) {
            response.addProperty("success", false);
            return GSON.toJson(response);
        } else {
            // Delete request
            registerRequests.delete(prid);

            // Send email
            MailBox m = new MailBox();
            m.sedMailRejectedRegisterRequest(registerRequest, motive);

            response.addProperty("success", true);
            return GSON.toJson(response);
        }
    }

    @ApiOperation(value = "Returns a list of pending register requests.", notes = "This method allows the admin to get a list of pending register requests.")
    @Secured({"ROLE_ADMIN"})
    @RequestMapping(value = "/requests/pending", method = RequestMethod.GET)
    public ResponseEntity<List<RegisterRequest>> acceptRequest() {
        List<RegisterRequest> pendingRequests = this.registerRequests.findByAccepted(false);
        return new ResponseEntity<>(pendingRequests, HttpStatus.OK);
    }

}
