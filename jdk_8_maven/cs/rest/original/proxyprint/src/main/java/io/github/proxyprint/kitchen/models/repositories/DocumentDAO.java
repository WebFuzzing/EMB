package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.printrequest.Document;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by daniel on 13-05-2016.
 */
public interface DocumentDAO extends CrudRepository<Document,Long> {
}
