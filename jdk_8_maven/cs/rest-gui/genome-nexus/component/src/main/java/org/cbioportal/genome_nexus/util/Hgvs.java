package org.cbioportal.genome_nexus.util;


public class Hgvs
{
    /**
     * Add "chr" prefix to hgvs
     *
     * @param hgvs
     * @return
     */
    public static String addChrPrefix(String hgvs) {
        StringBuilder variant = new StringBuilder(hgvs);
        if(variant.toString().contains("g.") && !variant.toString().startsWith("chr")) {
            variant.insert(0,"chr");
        }
        return variant.toString();
    }

    /**
     * Normalize "del" and "delins" as per the Hgvs specification
     *
     * @param hgvs
     * @return
     */
    public static String removeDeletedBases(String hgvs) {
        StringBuilder variant = new StringBuilder(hgvs);
        // if contains "del", remove the deleted nucleotides
        if (variant.toString().contains("del")) {
            int start = variant.lastIndexOf("del") + 3;
            int end = variant.length();
            // If contains "del" and "ins", remove characters between "del" to "ins"
            if (variant.toString().contains("ins")) {
                end = variant.lastIndexOf("ins");
            }
            variant.delete(start, end);
        }
        return variant.toString();
    }

}
