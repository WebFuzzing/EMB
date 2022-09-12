package org.cbioportal.genome_nexus.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.*;

import org.cbioportal.genome_nexus.util.exception.InvalidHgvsException;
import org.cbioportal.genome_nexus.util.exception.RefTypeNotSupportedException;
import org.cbioportal.genome_nexus.util.exception.TypeNotSupportedException;

public class GenomicVariantUtil {

    public static GenomicVariant fromHgvs(String hgvs) {
        GenomicVariant.RefType refType = getRefTypeFromHgvs(hgvs);
        String chr = getChrFromHgvs(hgvs);
        Integer start = getStartFromHgvs(hgvs);
        Integer end = getEndFromHgvs(hgvs, start);
        GenomicVariant.Type type = getTypeFromHgvs(hgvs);
        if (type == GenomicVariant.Type.INSERTION && end != start + 1) {
            throw new InvalidHgvsException("insertion encountered where end position did not equal start position plus 1");
        }
        String ref = getRefFromHgvs(hgvs, type);
        String alt = getAltFromHgvs(hgvs, type);
        return new GenomicVariant(chr, refType, start, end, type, ref, alt);
    }

/* The following (fromRegion) code is invalid
 * The problem is that the relative size of the start and end members
 * in a region string determine the type and appropriate alleles for
 * the GenomicVariant format. When the start is more than the end in
 * region format, the end should always be equal to (start - 1). The
 * meaning of this pattern is that an insertion is denoted with the
 * alt allele nucleotides being added in the observed allele between
 * the two nucleotide positions given in the reference allele. When
 * the start is less than the end, it means that the nucleotides
 * in the reference allele from position start to end (inclusive) are
 * no longer present in the observed allele and the alt alelle nucleotides
 * (if any) are present in that location instead.
 * see : https://m.ensembl.org/info/docs/tools/vep/vep_formats.html#input
 */

    public static GenomicVariant fromRegion(String region) {
        if (!isRegion(region)) {
            throw new IllegalArgumentException("region is invalid");
        }
        GenomicVariant.RefType refType = null; // WARNING : refType must be determined in order for other utility functions to work correctly - perhaps this should alwyas be GENOMIC unless "M" is a valid chromosome
        String chr = getPattern("^\\d{1,2}(?=:)", region);
        Integer start = Integer.parseInt(getPattern("(?<=:)\\d+(?=-)", region));
        Integer end = Integer.parseInt(getPattern("(?<=-)\\d+(?=:)", region));
        GenomicVariant.Type type = null; // WARNING : type must be determined in order for other utility functions to work correctly
        String ref = null;
        String alt = getPattern("(?<=:-?1/)[ATCG]+|-$", region);
        return new GenomicVariant(chr, refType, start, end, type, ref, alt);
    }

    public static String toRegion(GenomicVariant variant) {
        switch (variant.getType()) {
            case SUBSTITUTION:
            case INDEL:
                return variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd() + ":1/" + variant.getAlt();
            case INSERTION:
                return variant.getChromosome() + ":" + variant.getEnd() + "-" + variant.getStart() + ":1/" + variant.getAlt();
            case DELETION:
                return variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd() + ":1/-";
            default:
                throw new InvalidHgvsException(); // Never reached
        }
    }

    public static ArrayList<String> getMafs(String maf_file) {
        if (!isMafFile(maf_file)) {
            throw new RuntimeException("maf file not found");
        }
        ArrayList<String> list = null;
        try {
            list = new ArrayList<String>();
            File file = new File(maf_file);
            Scanner sc = new Scanner(file);
            while (sc.hasNextLine()) {
                String line = sc.nextLine();
                if (!line.startsWith("#")) {
                    list.add(line);
                }
            }
            sc.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return list;
    }

    // WARNING : the Genomic Variant returned by this function has an undetermined type (null). This prevents proper function of other utility functions in this class.
    public static GenomicVariant fromMaf(String maf, String[] key) {
        String[] arr = maf.split("\\s");
        // [Chromosome, Start_Position, End_Position, Reference_Allele, Tumor_Seq_Allele]
        int chr_index = 0;
        int start_index = 0;
        int end_index = 0;
        int ref_index = 0;
        int alt_index = 0;

        for (int i = 0; i < key.length; i++) {
            switch (key[i]) {
                case "Chromosome":
                    chr_index = i;
                    break;
                case "Start_Position":
                    start_index = i;
                    break;
                case "End_Position":
                    end_index = i;
                    break;
                case "Reference_Allele":
                    ref_index = i;
                    break;
                case "Tumor_Seq_Allele":
                    alt_index = i;
                    break;
            }
        }
        return new GenomicVariant(arr[chr_index],
                null,
                Integer.parseInt(arr[start_index]),
                Integer.parseInt(arr[end_index]),
                null,
                arr[ref_index],
                arr[alt_index]);
    }

    public static boolean isRegion(String variant) {
        return variant.contains("/");
        // TODO : do fuller validation of region format, such as below (however, the line below does not cover X and Y chromosomes)
        // return Pattern.matches("^\\d{1,2}:\\d+-\\d+:-?1/([ATCG]+|-)$", variant);
    }

    public static boolean isMafFile(String file) {
        return Pattern.matches("^[\\w\\-\\./]+(\\.maf)|^[\\w\\-\\./]+(\\.tsv)|^[\\w\\-\\./]+(\\.txt)$$", file);
    }

    // Hgvs formatted variants of several types are parsed for provided Reference Alleles
    // empty string is returned if no reference allele can be extracted
    public static String providedReferenceAlleleFromHgvs(String hgvs) {
        // Substitutions require a single nucleotide reference sequence:
        // 1g.123456A>G  (chromosome 1, genomic position 123456, reference sequence A, substituted with G)
        // ref: http://varnomen.hgvs.org/recommendations/DNA/variant/substitution/
        String match = getPattern("(?<=[^ATGC])[ATGC]>", hgvs);
        if (match != null && match.trim().length() > 0) {
            return match.substring(0, 1);
        }
        // Deletion-Insertions are recommended to *not* include specified deleted seqeunce/Reference Allele, but it is not prohibited.
        // Examples showing specified deleted sequence/ReferenceAllele are mentioned here : http://varnomen.hgvs.org/recommendations/DNA/variant/delins/
        // 1g.123456_123457delAAinsTT (chromosome 1, genomic positions 123456-123457, reference sequence AA deleted, sequence TT inserted in place)
        // recommended representation omits the deleted nucleotides "1g.123456_123457delinsTT"
        match = getPattern("del[ATGC]*ins", hgvs);
        if (match != null && match.trim().length() > 0) {
            return match.trim().substring(3, match.trim().length() - 3);
        }
        // Deletions are recommended to *not* include specified deleted seqeunce/Reference Allele, but it is not prohibited.
        // An example showing a specified deletion is mentioned here : http://varnomen.hgvs.org/recommendations/DNA/variant/alleles/
        // 1g.123456_123457delAA (chromosome 1, genomic positions 123456-123457, reference sequence AA deleted)
        // recommended representation omits the deleted nucleotides "1g.123456_123457del"
        match = getPattern("del[ATGC]*", hgvs);
        if (match != null && match.trim().length() > 0) {
            return match.trim().substring(3);
        }
        // Duplications are not supported by this system, but would be handled with logic similar to deletions
        // Examples showing specified deleted sequence/ReferenceAllele are mentioned here : http://varnomen.hgvs.org/recommendations/DNA/variant/duplication/
        // 1g.123456_123457dupAA (chromosome 1, genomic positions 123456-123457, reference sequence AA duplicated)
        // Hgvs repeats and inversions are not supported by this system and this method does not attempt to parse these formats
        //
        // no reference allele was found
        return "";
    }

    // postcondition: returns a substring of hgvs that matched to the regex, or null if not matched
    private static String getPattern(String regex, String hgvs) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(hgvs);
        if (m.find()) {
            return hgvs.substring(m.start(), m.end());
        }
        return null;
    }

    private static GenomicVariant.RefType getRefTypeFromHgvs(String hgvs) {
        String refTypeString = getPattern("(?<=:)[cgmnopr](?=.)", hgvs);
        if (refTypeString == null || refTypeString.trim().length() == 0) {
            throw new InvalidHgvsException();
        }
        switch (refTypeString.trim()) {
            case "g":
                return GenomicVariant.RefType.GENOMIC;
            case "m":
                // MITOCHONDRIAL ref type not yet supported
            case "c":
            case "n":
            case "o":
            case "p":
            case "r":
            default:
                // GenomicVariant representation does not accept other HGVS types (coding_dna, non_coding_dna, circular_genomic_reference, protein, rna_transcript)
                throw new RefTypeNotSupportedException();
        }
    }

    private static String getChrFromHgvs(String hgvs) {
        String chr = getPattern("^.+(?=:)", hgvs);
        if (chr == null || chr.trim().length() == 0) {
            throw new InvalidHgvsException();
        }
        // valid chromosomes are "X", "Y", or any positive integer
        if (!chr.equalsIgnoreCase("X") && !chr.equalsIgnoreCase("Y")) {
            try {
                if (Integer.parseInt(chr) <= 0) {
                    throw new InvalidHgvsException("Chromosome reference is negative or zero");
                }
            } catch (NumberFormatException e) {
                throw new InvalidHgvsException(e);
            }
        }
        return chr;
    }

    private static Integer getStartFromHgvs(String hgvs) {
        String startString = getPattern("(?<=\\.)\\d+(?=[_ATGC]|del)", hgvs);
        if (startString == null || startString.trim().length() == 0) {
            throw new InvalidHgvsException("Start position could not be parsed");
        }
        try {
            return Integer.parseInt(startString);
        } catch (NumberFormatException e) {
            // position is out of range for Integer
            throw new InvalidHgvsException(e);
        }
    }

    private static Integer getEndFromHgvs(String hgvs, Integer start) {
        String endString = getPattern("(?<=_)\\d+(?=[a-z]+)", hgvs);
        if (endString == null || endString.trim().length() == 0) {
            return start;
        }
        try {
            return Integer.parseInt(endString);
        } catch (NumberFormatException e) {
            // position is out of range for Integer
            throw new InvalidHgvsException(e);
        }
    }

    private static GenomicVariant.Type getTypeFromHgvs(String hgvs) {
        String type = getPattern("(?<=\\d+[ATGC]?)[a-z>]+(?=[ATCG]*)", hgvs);
        if (type == null || type.trim().length() == 0) {
            throw new InvalidHgvsException("variant type could not be parsed");
        }
        switch(type) {
            case ">":
                return GenomicVariant.Type.SUBSTITUTION;
            case "ins":
                return GenomicVariant.Type.INSERTION;
            case "del":
                return GenomicVariant.Type.DELETION;
            case "delins":
                return GenomicVariant.Type.INDEL;
        }
        throw new TypeNotSupportedException();
    }

    private static String sameLengthUnknownNucleotideString(String s) {
        if (s == null || s.trim().length() == 0) {
            return "";
        }
        return String.join("", Collections.nCopies(s.trim().length(), "X"));
    }

    private static String getRefFromHgvs(String hgvs, GenomicVariant.Type type) {
        String s;
        switch(type) {
            case SUBSTITUTION:
                s = getPattern("(?<=\\d+)[ATCG]+(?=>)", hgvs);
                if (s == null || s.trim().length() == 0) {
                    throw new InvalidHgvsException("reference allele in substitution could not be parsed");
                }
                return s.trim();
            case INSERTION:
                return "-";
            case DELETION:
                s = getPattern("(?<=[a-z+>])[ATCG]*$", hgvs);
                if (s == null || s.trim().length() == 0) {
                    return "-";
                }
                return s.trim();
            case INDEL:
                // use a string of "X" equal to length of alt allele
                // TODO : check if this is used anywhere and drop this if not .. also check if this is needed above for DELETION
                s = getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
                return sameLengthUnknownNucleotideString(s);
            default:
                throw new InvalidHgvsException(); // Never reached
        }
    }

    private static String getAltFromHgvs(String hgvs, GenomicVariant.Type type) {
        String s;
        switch(type) {
            case SUBSTITUTION:
            case INSERTION:
            case INDEL:
                s  = getPattern("(?<=[a-z+>])[ATCG]+$", hgvs);
                if (s == null || s.trim().length() == 0) {
                    return "-";
                }
                return s.trim();
            case DELETION:
                return "-";
            default:
                throw new InvalidHgvsException(); // Never reached
        }
    }
}
