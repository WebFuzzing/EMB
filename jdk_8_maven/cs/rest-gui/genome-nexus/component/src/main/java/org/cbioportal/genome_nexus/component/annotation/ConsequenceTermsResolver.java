package org.cbioportal.genome_nexus.component.annotation;

import org.apache.commons.lang.StringUtils;
import org.cbioportal.genome_nexus.model.TranscriptConsequence;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsequenceTermsResolver
{
    @Nullable
    public String resolve(TranscriptConsequence transcriptConsequence)
    {
        String consequence = null;
        List<String> consequenceTerms = this.resolveAll(transcriptConsequence);

        if (consequenceTerms != null && consequenceTerms.size() > 0) {
            consequence = StringUtils.join(consequenceTerms, ",");
        }

        return consequence;
    }

    @Nullable
    public List<String> resolveAll(TranscriptConsequence transcriptConsequence)
    {
        List<String> consequenceTerms = null;

        if (transcriptConsequence != null) {
            consequenceTerms = transcriptConsequence.getConsequenceTerms();
        }

        return consequenceTerms;
    }
}
