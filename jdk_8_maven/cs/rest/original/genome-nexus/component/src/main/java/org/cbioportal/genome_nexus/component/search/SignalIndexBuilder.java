package org.cbioportal.genome_nexus.component.search;

import org.cbioportal.genome_nexus.component.annotation.NotationConverter;
import org.cbioportal.genome_nexus.component.annotation.ProteinChangeResolver;
import org.cbioportal.genome_nexus.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class SignalIndexBuilder
{
    private final NotationConverter notationConverter;
    private final ProteinChangeResolver proteinChangeResolver;

    @Autowired
    public SignalIndexBuilder(
        NotationConverter notationConverter,
        ProteinChangeResolver proteinChangeResolver
    ) {
        this.notationConverter = notationConverter;
        this.proteinChangeResolver = proteinChangeResolver;
    }

    public List<SignalQuery> buildQueryIndex(
        List<SignalMutation> mutations,
        List<VariantAnnotation> annotations
    )
    {
        Map<String, String> genomicLocationToVariant = this.genomicLocationToVariant(mutations);
        Map<String, String> variantToProteinChange = this.variantToProteinChange(annotations);

        return mutations
            .stream()
            .map(m -> this.mapMutationToExactQuery(m, genomicLocationToVariant, variantToProteinChange))
            .collect(Collectors.toList());
    }

    public List<String> findUniqueVariants(List<SignalMutation> mutations)
    {
        return this.genomicLocationToVariant(mutations).values().stream().distinct().collect(Collectors.toList());
    }

    public Map<String, String> genomicLocationToVariant(List<SignalMutation> mutations)
    {
        return mutations
            .stream()
            .filter(m -> m.getChromosome() != null && m.getVariantAllele() != null && m.getReferenceAllele() != null)
            .map(this::generateGenomicLocation)
            .collect(Collectors.toMap(
                // key
                GenomicLocation::toString,
                // value
                gl -> {
                    String variant = this.notationConverter.genomicToHgvs(gl);
                    return variant == null ? "" : variant;
                },
                // this is to ignore duplicates
                (v1, v2) -> v1
            ));
    }

    public Map<String, String> variantToProteinChange(List<VariantAnnotation> annotations)
    {
        return annotations
            .stream()
            .collect(Collectors.toMap(
                VariantAnnotation::getVariantId,
                annotation -> this.normalizeAlteration(this.proteinChangeResolver.resolveHgvspShort(annotation))
            ));
    }

    public SignalQuery mapMutationToExactQuery(
        SignalMutation mutation,
        Map<String, String> genomicLocationToVariant,
        Map<String, String> variantToProteinChange
    ) {
        String region = this.generateRegion(mutation);
        String variant = genomicLocationToVariant.get(generateGenomicLocation(mutation).toString());
        String alteration = variantToProteinChange.get(variant);

        SignalQuery query = new SignalQuery();

        query.setMatchType(SignalMatchType.EXACT);
        query.setHugoSymbol(mutation.getHugoGeneSymbol());
        query.setRegion(region);
        query.setVariant(variant);

        if (alteration != null && alteration.length() > 0) {
            query.setAlteration(alteration);
        }

        return query;
    }

    public String generateRegion(SignalMutation mutation) {
        return mutation.getChromosome() + ":" + mutation.getStartPosition() + "-" + mutation.getEndPosition();
    }

    public GenomicLocation generateGenomicLocation(SignalMutation mutation) {
        GenomicLocation gl = new GenomicLocation();

        gl.setChromosome(mutation.getChromosome());
        gl.setStart(mutation.getStartPosition().intValue());
        gl.setEnd(mutation.getEndPosition().intValue());
        gl.setReferenceAllele(mutation.getReferenceAllele());
        gl.setVariantAllele(mutation.getVariantAllele());

        return gl;
    }

    private String normalizeAlteration(String proteinChange) {
        if (proteinChange == null) {
            return "";
        }

        if (proteinChange.contains("p.")) {
            return proteinChange.replace("p.", "");
        }

        return proteinChange;
    }
}
