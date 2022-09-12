package org.cbioportal.genome_nexus.model;

public class Version {

    private String version;
    private boolean isStatic;

    public Version(String version, boolean isStatic) {
        this.version = version;
        this.isStatic = isStatic;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public void setStatic(boolean isStatic) {
        this.isStatic = isStatic;
    }
}
