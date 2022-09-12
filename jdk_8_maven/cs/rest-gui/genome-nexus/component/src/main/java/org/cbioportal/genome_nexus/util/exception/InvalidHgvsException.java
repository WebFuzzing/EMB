package org.cbioportal.genome_nexus.util.exception;

public class InvalidHgvsException extends IllegalArgumentException {

    public InvalidHgvsException() {
        super("hgvs is invalid");
    }

    public InvalidHgvsException(String msg) {
        super(msg);
    }

    public InvalidHgvsException(Throwable e) {
        super(e);
    }
}
