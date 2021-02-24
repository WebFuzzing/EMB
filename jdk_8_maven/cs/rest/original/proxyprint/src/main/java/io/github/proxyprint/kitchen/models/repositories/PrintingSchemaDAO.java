package io.github.proxyprint.kitchen.models.repositories;

import io.github.proxyprint.kitchen.models.consumer.PrintingSchema;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by daniel on 28-04-2016.
 */
public interface PrintingSchemaDAO extends CrudRepository<PrintingSchema,Long> {
}
