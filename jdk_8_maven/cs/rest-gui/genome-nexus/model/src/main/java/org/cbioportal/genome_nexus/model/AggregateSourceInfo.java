package org.cbioportal.genome_nexus.model;

public class AggregateSourceInfo {

    private GenomeNexusInfo genomeNexus;
    private VEPInfo vep;

    public AggregateSourceInfo(GenomeNexusInfo genomeNexusInfo, VEPInfo vepInfo) {
        this.genomeNexus = genomeNexusInfo;
        this.vep = vepInfo;
    }

    public GenomeNexusInfo getGenomeNexus() {
        return genomeNexus;
    }

    public void setGenomeNexus(GenomeNexusInfo genomeNexusInfo) {
        this.genomeNexus = genomeNexusInfo;
    }

    public VEPInfo getVep() {
        return vep;
    }

    public void setVep(VEPInfo vepInfo) {
        this.vep = vepInfo;
    }
}
