package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class TranscriptConsequencePrioritizer
{
    public static final Map<String, Integer> EFFECT_PRIORITY = initEffectPriority();

    @Nullable
    public TranscriptConsequence transcriptWithMostSevereConsequence(List<TranscriptConsequence> transcripts,
                                                                     String mostSevereConsequence)
    {
        if (transcripts == null) {
            return null;
        }

        Integer highestPriority = Integer.MAX_VALUE;
        TranscriptConsequence highestPriorityTranscript = null;

        for (TranscriptConsequence transcript : transcripts)
        {
            List<String> consequenceTerms = transcript.getConsequenceTerms();

            for (String consequenceTerm : consequenceTerms)
            {
                if (EFFECT_PRIORITY.getOrDefault(consequenceTerm.toLowerCase(), Integer.MAX_VALUE) < highestPriority)
                {
                    highestPriorityTranscript = transcript;
                    highestPriority = EFFECT_PRIORITY.getOrDefault(consequenceTerm.toLowerCase(), Integer.MAX_VALUE);
                }

                if (consequenceTerm.trim().equals(mostSevereConsequence)) {
                    return transcript;
                }
            }
        }

        // no match, pick one with the highest priority
        if (highestPriorityTranscript != null) {
            return highestPriorityTranscript;
        }

        // if for whatever reason that is null, just return the first one.
        if (transcripts.size() > 0) {
            return transcripts.get(0);
        }

        return null;
    }

    @Nullable
    public String pickHighestPriorityConsequence(List<String> consequences)
    {
        if (consequences == null) {
            return null;
        }

        String highestPriorityConsequence = null;
        Integer highestPriority = Integer.MAX_VALUE;

        for (String consequence : consequences)
        {
            if (EFFECT_PRIORITY.getOrDefault(consequence.toLowerCase(), Integer.MAX_VALUE) < highestPriority)
            {
                highestPriorityConsequence = consequence;
                highestPriority = EFFECT_PRIORITY.getOrDefault(consequence.toLowerCase(), Integer.MAX_VALUE);
            }
        }

        return highestPriorityConsequence;
    }

    /**
     * Prioritize Sequence Ontology terms in order of severity, as estimated by Ensembl.
     *
     * Ensembl: http://useast.ensembl.org/info/genome/variation/predicted_data.html#consequences
     * VCF2MAF mappings: https://github.com/mskcc/vcf2maf/blob/master/vcf2maf.pl (GetEffectPriority)
     */
    private static Map<String, Integer> initEffectPriority()
    {
        Map<String, Integer> effectPriority = new HashMap<>();

        effectPriority.put("transcript_ablation", 1); // A feature ablation whereby the deleted region includes a transcript feature
        effectPriority.put("exon_loss_variant", 1); // A sequence variant whereby an exon is lost from the transcript
        effectPriority.put("splice_donor_variant", 2); // A splice variant that changes the 2 base region at the 5" end of an intron
        effectPriority.put("splice_acceptor_variant", 2); // A splice variant that changes the 2 base region at the 3" end of an intron
        effectPriority.put("stop_gained", 3); // A sequence variant whereby at least one base of a codon is changed, resulting in a premature stop codon, leading to a shortened transcript
        effectPriority.put("frameshift_variant", 3); // A sequence variant which causes a disruption of the translational reading frame, because the number of nucleotides inserted or deleted is not a multiple of three
        effectPriority.put("stop_lost", 3); // A sequence variant where at least one base of the terminator codon (stop) is changed, resulting in an elongated transcript
        effectPriority.put("start_lost", 4); // A codon variant that changes at least one base of the canonical start codon
        effectPriority.put("initiator_codon_variant", 4); // A codon variant that changes at least one base of the first codon of a transcript
        effectPriority.put("disruptive_inframe_insertion", 5); // An inframe increase in cds length that inserts one or more codons into the coding sequence within an existing codon
        effectPriority.put("disruptive_inframe_deletion", 5); // An inframe decrease in cds length that deletes bases from the coding sequence starting within an existing codon
        effectPriority.put("inframe_insertion", 5); // An inframe non synonymous variant that inserts bases into the coding sequence
        effectPriority.put("inframe_deletion", 5); // An inframe non synonymous variant that deletes bases from the coding sequence
        effectPriority.put("protein_altering_variant", 5); // A sequence variant which is predicted to change the protein encoded in the coding sequence
        effectPriority.put("missense_variant", 6); // A sequence variant, that changes one or more bases, resulting in a different amino acid sequence but where the length is preserved
        effectPriority.put("conservative_missense_variant", 6); // A sequence variant whereby at least one base of a codon is changed resulting in a codon that encodes for a different but similar amino acid. These variants may or may not be deleterious
        effectPriority.put("rare_amino_acid_variant", 6); // A sequence variant whereby at least one base of a codon encoding a rare amino acid is changed, resulting in a different encoded amino acid
        effectPriority.put("transcript_amplification", 7); // A feature amplification of a region containing a transcript
        effectPriority.put("splice_region_variant", 8); // A sequence variant in which a change has occurred within the region of the splice site, either within 1-3 bases of the exon or 3-8 bases of the intron
        effectPriority.put("start_retained_variant", 9); // A sequence variant where at least one base in the start codon is changed, but the start remains
        effectPriority.put("stop_retained_variant", 9); // A sequence variant where at least one base in the terminator codon is changed, but the terminator remains
        effectPriority.put("synonymous_variant", 9); // A sequence variant where there is no resulting change to the encoded amino acid
        effectPriority.put("incomplete_terminal_codon_variant", 10); // A sequence variant where at least one base of the final codon of an incompletely annotated transcript is changed
        effectPriority.put("coding_sequence_variant", 11); // A sequence variant that changes the coding sequence
        effectPriority.put("mature_mirna_variant", 11); // A transcript variant located with the sequence of the mature miRNA
        effectPriority.put("exon_variant", 11); // A sequence variant that changes exon sequence
        effectPriority.put("5_prime_utr_variant", 12); // A UTR variant of the 5" UTR
        effectPriority.put("5_prime_utr_premature_start_codon_gain_variant", 12); // snpEff-specific effect, creating a start codon in 5" UTR
        effectPriority.put("3_prime_utr_variant", 12); // A UTR variant of the 3" UTR
        effectPriority.put("non_coding_exon_variant", 13); // A sequence variant that changes non-coding exon sequence
        effectPriority.put("non_coding_transcript_exon_variant", 13); // snpEff-specific synonym for non_coding_exon_variant
        effectPriority.put("non_coding_transcript_variant", 14); // A transcript variant of a non coding RNA gene
        effectPriority.put("nc_transcript_variant", 14); // A transcript variant of a non coding RNA gene (older alias for non_coding_transcript_variant)
        effectPriority.put("intron_variant", 14); // A transcript variant occurring within an intron
        effectPriority.put("intragenic_variant", 14); // A variant that occurs within a gene but falls outside of all transcript features. This occurs when alternate transcripts of a gene do not share overlapping sequence
        effectPriority.put("intragenic", 14); // snpEff-specific synonym of intragenic_variant
        effectPriority.put("nmd_transcript_variant", 15); // A variant in a transcript that is the target of NMD
        effectPriority.put("upstream_gene_variant", 16); // A sequence variant located 5" of a gene
        effectPriority.put("downstream_gene_variant", 16); // A sequence variant located 3" of a gene
        effectPriority.put("tfbs_ablation", 17); // A feature ablation whereby the deleted region includes a transcription factor binding site
        effectPriority.put("tfbs_amplification", 17); // A feature amplification of a region containing a transcription factor binding site
        effectPriority.put("tf_binding_site_variant", 17); // A sequence variant located within a transcription factor binding site
        effectPriority.put("regulatory_region_ablation", 17); // A feature ablation whereby the deleted region includes a regulatory region
        effectPriority.put("regulatory_region_amplification", 17); // A feature amplification of a region containing a regulatory region
        effectPriority.put("regulatory_region_variant", 17); // A sequence variant located within a regulatory region
        effectPriority.put("regulatory_region", 17); // snpEff-specific effect that should really be regulatory_region_variant
        effectPriority.put("feature_elongation", 18); // A sequence variant that causes the extension of a genomic feature, with regard to the reference sequence
        effectPriority.put("feature_truncation", 18); // A sequence variant that causes the reduction of a genomic feature, with regard to the reference sequence
        effectPriority.put("intergenic_variant", 19); // A sequence variant located in the intergenic region, between genes
        effectPriority.put("intergenic_region", 19); // snpEff-specific effect that should really be intergenic_variant
        effectPriority.put("", 20);

        return effectPriority;
    }
}
