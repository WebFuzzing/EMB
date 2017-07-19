/*
 * Copyright 2016 Jorge Caldas, José Cortez
 * José Francisco, Marcelo Gonçalves
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
package io.github.proxyprint.kitchen.controllers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.config.NgrokConfig;
import io.github.proxyprint.kitchen.models.User;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.repositories.*;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author josesousa
 */
@RestController
@Transactional
public class DefaultController {

    private final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);

    @Autowired
    private UserDAO users;
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private AdminDAO admins;
    @Autowired
    private ManagerDAO managers;
    @Autowired
    private EmployeeDAO employees;
    @Autowired
    private Gson GSON;
    @Autowired
    private Environment environment;

    @RequestMapping(method = RequestMethod.OPTIONS, value = "/*")
    @ResponseBody
    public ResponseEntity handleOptions() {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(ModelMap map) {
        JsonObject response = new JsonObject();
        response.addProperty("message", "Olá Mundo!");
        return GSON.toJson(response);
    }

    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/api/secured", method = RequestMethod.GET)
    public String secured(ModelMap map) {
        JsonObject response = new JsonObject();
        response.addProperty("message", "Se estiveres autenticado, podes ver isto!");
        return GSON.toJson(response);
    }

    @ApiOperation(value = "Returns success/insuccess.", notes = "This method validates the login data from a user.")
    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(WebRequest request) throws IOException {
        boolean auth;
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        JsonObject response = new JsonObject();
        if (username == null || password == null) {
            auth = false;
        } else {
            User user = createUser(username);
            if (this.environment.acceptsProfiles("!heroku") && user.getClass().getSimpleName().equals(Consumer.class.getSimpleName())) {
                try {
                    // Added tunnel to response
                    String tunnel = NgrokConfig.getExternalUrl();
                    response.addProperty("externalURL", tunnel);
                } catch (NullPointerException ex) {
                    logger.warn("Ngrok is not running! Please solve that!");
                }
            }
            if (user == null) {
                auth = false;
            } else {
                auth = user.getPassword().equals(password);
                if (auth == true) {
                    response.add("user", GSON.toJsonTree(user));
                }
            }
        }

        response.addProperty("success", auth);
        return GSON.toJson(response);
    }

    /**
     * Find the username in the database and create its concrete type.
     * http://stackoverflow.com/questions/25991191/spring-autowired-bean-causes-null-pointer
     *
     * @param username, the username submited in the log in process.
     * @return An user object instantiated with its concrete type.
     */
    public User createUser(String username) {

        User u = users.findByUsername(username);

        if (u != null) {
            if (u.getRolesSet().contains(User.Roles.ROLE_USER.toString())) {
                return consumers.findByUsername(username);
            } else if (u.getRolesSet().contains((User.Roles.ROLE_ADMIN.toString()))) {
                return admins.findByUsername(username);
            } else if (u.getRolesSet().contains((User.Roles.ROLE_MANAGER.toString()))) {
                return managers.findByUsername(username);
            } else if (u.getRolesSet().contains((User.Roles.ROLE_EMPLOYEE.toString()))) {
                return employees.findByUsername(username);
            }
        }

        return null;
    }
}
