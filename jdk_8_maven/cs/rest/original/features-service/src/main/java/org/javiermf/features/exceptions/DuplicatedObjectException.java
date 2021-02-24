package org.javiermf.features.exceptions;

public class DuplicatedObjectException extends RuntimeException {
    public DuplicatedObjectException(String name) {
        super(String.format("Object with id %s already exists", name));
    }
}
