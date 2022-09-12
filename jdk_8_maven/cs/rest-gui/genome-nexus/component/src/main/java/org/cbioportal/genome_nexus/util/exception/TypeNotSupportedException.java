package org.cbioportal.genome_nexus.util.exception;

public class TypeNotSupportedException extends IllegalArgumentException {
    public TypeNotSupportedException() {
        super("only substitutions, insertions, deletions, and indels are supported");
    }
}