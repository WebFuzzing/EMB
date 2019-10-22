package org.devgateway.ocds.persistence.mongo.flags.preconditions;

import java.util.function.Predicate;

/**
 * A {@link Predicate}that also has a descriptive name
 * 
 * @author mpostelnicu
 *
 * @param <T>
 */
public class NamedPredicate<T> implements Predicate<T> {
    private final String name;
    private final Predicate<T> predicate;

    public NamedPredicate(String name, Predicate<T> predicate) {
        this.name = name;
        this.predicate = predicate;
    }

    @Override
    public boolean test(T t) {
        return predicate.test(t);
    }

    @Override
    public String toString() {
        return name;
    }


    @Override
    public NamedPredicate<T> or(Predicate<? super T> other) {
        return new NamedPredicate<T>(this.toString() + " OR " + other.toString(), Predicate.super.or(other));
    }

    @Override
    public NamedPredicate<T> and(Predicate<? super T> other) {
        return new NamedPredicate<T>(this.toString() + " AND " + other.toString(), Predicate.super.and(other));
    }
}