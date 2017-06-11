package org.devgateway.ocds.persistence.mongo.spring.json;

import org.devgateway.ocds.persistence.mongo.Identifiable;
import org.devgateway.ocds.persistence.mongo.spring.ImportService;

import java.io.IOException;
import java.util.Collection;

/**
 * @author idobre
 * @since 6/1/16
 */
public interface JsonImportPackage<T, S extends Identifiable> extends ImportService {
        /**
         * Imports a Package (Release/Record) from a JSON and returns the imported collection
         * after was saved/updated into database (it should contain an id/_id)
         *
         * @return Collection<S>
         */
        Collection<S> importObjects() throws IOException;
}
