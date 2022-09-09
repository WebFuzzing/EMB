package org.cbioportal.genome_nexus.model;

public class GenomeNexusInfo {

    public Version server;
    public Version database;

    public GenomeNexusInfo(String genomeNexusServerVersion, String genomeNexusDatabaseVersion) {
        this.server = new Version(genomeNexusServerVersion, true);
        this.database = new Version(genomeNexusDatabaseVersion, true);
    }
}
