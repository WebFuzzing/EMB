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
package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.notifications.Notification;
import org.springframework.data.repository.CrudRepository;

/**
 *
 * @author jcortez
 */
public interface NotificationDAO extends CrudRepository<Notification, Long> {
    
    public Notification findById (long id);
    
}
