package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.printrequest.DocumentSpec;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by daniel on 13-05-2016.
 */
public interface DocumentSpecDAO extends CrudRepository<DocumentSpec,Long> {
}