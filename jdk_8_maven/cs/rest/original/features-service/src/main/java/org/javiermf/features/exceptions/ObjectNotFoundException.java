package org.javiermf.features.exceptions;

public class ObjectNotFoundException extends RuntimeException {
    public ObjectNotFoundException(String productName) {
        super(String.format("Object with id %s has not been found", productName));
    }
}
