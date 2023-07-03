package org.cbioportal.genome_nexus.util;

public class IsoformOverrideSource {
    public static final String ENSEMBL = "ensembl";
    public static final String GENOME_NEXUS = "genome_nexus";
    public static final String UNIPROT = "uniprot";
    public static final String MSKCC = "mskcc";

    public static String getOrDefault(String isoformOverrideSource) {
        String normalizedSource = isoformOverrideSource == null ?
            UNIPROT : isoformOverrideSource.trim().toLowerCase();

        return (
            normalizedSource.equals(ENSEMBL) ||
            normalizedSource.equals(GENOME_NEXUS) ||
            normalizedSource.equals(MSKCC)
        ) ? normalizedSource : UNIPROT;
    }
}
