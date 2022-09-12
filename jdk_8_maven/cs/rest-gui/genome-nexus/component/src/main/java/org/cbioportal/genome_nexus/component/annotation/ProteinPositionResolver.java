package org.cbioportal.genome_nexus.component.annotation;

import org.cbioportal.genome_nexus.model.IntegerRange;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.cbioportal.genome_nexus.model.VariantAnnotation;
import org.cbioportal.genome_nexus.util.Numerical;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProteinPositionResolver
{
    private final ProteinChangeResolver proteinChangeResolver;

    @Autowired
    public ProteinPositionResolver(ProteinChangeResolver proteinChangeResolver)
    {
        this.proteinChangeResolver = proteinChangeResolver;
    }

    @Nullable
    public IntegerRange resolve(VariantAnnotation annotation, TranscriptConsequence transcriptConsequence)
    {
        IntegerRange proteinPos = null;
        String proteinChange = this.proteinChangeResolver.resolveHgvspShort(annotation, transcriptConsequence);

        // special case for duplication
        if (proteinChange != null &&
            proteinChange.toLowerCase().contains("dup"))
        {
            proteinPos = this.extractProteinPos(proteinChange);
        }
        // special case for splice
        if (proteinChange != null &&
            proteinChange.toLowerCase().contains("x"))
        {
            proteinPos = this.extractProteinPos(proteinChange);
        }

        // for all other cases use the reported protein start and end positions
        // (proteinPos may also be null in case of protein change value parse error)
        if (proteinPos == null &&
            transcriptConsequence != null &&
            (transcriptConsequence.getProteinStart() != null || transcriptConsequence.getProteinEnd() != null))
        {
            proteinPos = new IntegerRange(transcriptConsequence.getProteinStart(), transcriptConsequence.getProteinEnd());
        }

        return proteinPos;
    }

    @Nullable
    public IntegerRange extractProteinPos(String proteinChange)
    {
        IntegerRange proteinPos = null;
        Integer start = -1;
        Integer end = -1;

        List<Integer> positions = Numerical.extractPositiveIntegers(proteinChange);

        // ideally positions.size() should always be 2
        if (positions.size() >= 2)
        {
            start = positions.get(0);
            end = positions.get(positions.size() - 1);
        }
        // in case no end point, use start as end
        else if (positions.size() > 0)
        {
            start = end = positions.get(0);
        }

        if (!start.equals(-1)) {
            proteinPos = new IntegerRange(start, end);
        }

        return proteinPos;
    }
}
