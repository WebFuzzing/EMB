package org.devgateway.ocds.persistence.mongo.flags;

import java.util.Set;

public class Flag {

    private Set<FlagType> types;

    private Boolean value;

    private String rationale;

    public Flag(Boolean value, String rationale, Set<FlagType> types) {
        this.value = value;
        this.rationale = rationale;
        this.types = types;
    }

    public Boolean getValue() {
        return value;
    }

    public void setValue(Boolean value) {
        this.value = value;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public Set<FlagType> getTypes() {
        return types;
    }

    public void setTypes(Set<FlagType> types) {
        this.types = types;
    }
}
