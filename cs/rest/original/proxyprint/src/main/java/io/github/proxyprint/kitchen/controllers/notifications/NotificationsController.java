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
package io.github.proxyprint.kitchen.controllers.notifications;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.utils.NotificationManager;
import java.security.Principal;
import java.util.List;

import io.swagger.annotations.ApiOperation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author jose
 */
@RestController 
public class NotificationsController {

    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private NotificationManager notificationManager;
    @Autowired
    private Gson GSON;

    @Transactional
    @ApiOperation(value = "Returns nothing", notes = "This method sends a notification to a consumer.")
    @RequestMapping(value = "/consumer/{id}/notify", method = RequestMethod.POST)
    public void greeting(@PathVariable(value = "id") long id, WebRequest request, Principal principal) throws Exception {
        String message = request.getParameter("message");
        String name = consumers.findOne(id).getUsername();
        notificationManager.sendNotification(name, new Notification(message));
    }

    @Transactional
    @ApiOperation(value = "Returns success/insuccess.", notes = "This method allows a consumer to subscribe the SSE.")
    @RequestMapping(value = "/consumer/subscribe", produces = "text/event-stream")
    public ResponseEntity<SseEmitter> subscribe(WebRequest request){
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Consumer consumer = this.consumers.findByUsername(username);
        if (consumer == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else if (consumer.getPassword().equals(password)) {
            return new ResponseEntity<>(this.notificationManager.subscribe(username), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    @ApiOperation(value = "Returns list of notifications.", notes = "This method retrieves to consumer his list of notifications.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value = "/consumer/notifications", method = RequestMethod.GET)
    public List<Notification> getConsumerNotifications(Principal principal) {
        Consumer consumer = this.consumers.findByUsername(principal.getName());
        return consumer.getNotifications();
    }

    @Transactional
    @ApiOperation(value = "Returns success.", notes = "This method allows consumers to delete a notification.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value="/notifications/{notificationId}", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteNotification(@PathVariable(value = "notificationId") long notId, WebRequest request){
        JsonObject response = new JsonObject();
        notificationManager.removeNotification(notId);
        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }

    @Transactional
    @ApiOperation(value = "Returns success.", notes = "This method allows consumers to mark a notification as read.")
    @Secured({"ROLE_USER"})
    @RequestMapping(value="/notifications/{notificationId}", method = RequestMethod.PUT)
    public ResponseEntity<String> readNotification (@PathVariable (value="notificationId") long notId){
        JsonObject response = new JsonObject();
        notificationManager.readNotification(notId);
        response.addProperty("success", true);
        return new ResponseEntity<>(GSON.toJson(response), HttpStatus.OK);
    }
}
