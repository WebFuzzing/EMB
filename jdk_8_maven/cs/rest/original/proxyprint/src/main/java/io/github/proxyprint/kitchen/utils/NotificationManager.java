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
package io.github.proxyprint.kitchen.utils;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.github.proxyprint.kitchen.WebAppConfig;
import io.github.proxyprint.kitchen.models.consumer.Consumer;
import io.github.proxyprint.kitchen.models.notifications.Notification;
import io.github.proxyprint.kitchen.models.repositories.ConsumerDAO;
import io.github.proxyprint.kitchen.models.repositories.NotificationDAO;
import java.io.IOException;
import java.util.logging.Level;
import org.apache.catalina.connector.ClientAbortException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 *
 * @author jose
 */
public class NotificationManager {
  
    private final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);
    
    @Autowired
    private ConsumerDAO consumers;
    @Autowired
    private NotificationDAO notifications;

    private final Multimap<String, SseEmitter> subscriptions;

    public NotificationManager() {
        this.subscriptions = HashMultimap.create();
    }

    public SseEmitter subscribe(String username) {
        SseEmitter sseEmitter = new SseEmitter(0l);
        sseEmitter.onCompletion(new DeleteTask(this, username, sseEmitter));
        this.subscriptions.put(username, sseEmitter);
        return sseEmitter;
    }

    public void unsubscribe(String username, SseEmitter emitter) {
        this.subscriptions.remove(username, emitter);
    }

    public void sendNotification(String username, Notification notification){
        Consumer consumer = this.consumers.findByUsername(username);
        notification = notifications.save(notification);
        consumer.addNotifications(notification);
        consumers.save(consumer);

        if (this.subscriptions.containsKey(username)) {
            for (SseEmitter sse : this.subscriptions.get(username)) {
                try {
                    sse.send(notification, MediaType.APPLICATION_JSON);
                } catch (IOException ex) {
                    this.logger.warn("Broken SSE Emitter..discarding it...");
                }
            }
        }
    }

    public void removeNotification(long notificationId) {
        notifications.delete(notificationId);
    }

    public void readNotification(long notificationId) {
        Notification n = notifications.findById(notificationId);
        n.setReadStatus(true);
        notifications.save(n);
    }
}

class DeleteTask implements Runnable {

    private final Logger logger = LoggerFactory.getLogger(WebAppConfig.class);
    private NotificationManager notifications;
    private String username;
    private SseEmitter sse;

    public DeleteTask(NotificationManager notifications, String username, SseEmitter sse) {
        this.notifications = notifications;
        this.username = username;
        this.sse = sse;
    }

    @Override
    public void run() {
        this.logger.info(username + " disconnected!");
        this.notifications.unsubscribe(username, sse);
    }

}
