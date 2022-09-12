package org.cbioportal.genome_nexus.model;

public class VEPInfo {

    private final String NA_STRING = "NA";
    private final String COMMENT = "VEP annotations are currently externally sourced from ENSEMBL. Results are subject to change without notice.";

    // server corresponds to genome nexus VEP code release
    // cache corresponds to downloaded FASTA cache
    // comment is an additional disclaimer in the event we are using ENSEMBL VEP
    public Version server;
    public Version cache;
    public String comment;

    public VEPInfo(String vepServerVersion, String vepCacheVersion, String vepRegionURL) {
        if (vepRegionURL != null && !vepRegionURL.isEmpty()) {
            this.server = new Version(vepServerVersion, true);
            this.cache = new Version(vepCacheVersion, true);
        } else {
            this.server = new Version(NA_STRING, false);
            this.cache = new Version(NA_STRING, false);
            this.comment = COMMENT;
        }
    }
}
