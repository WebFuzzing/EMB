package org.devgateway.ocds.persistence.mongo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface Identifiable {

    @JsonIgnore
    Serializable getIdProperty();
}
