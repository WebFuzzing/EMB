package org.cbioportal.genome_nexus.util;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.*;

public class PatternsTest
{
    @Test
    public void toStartsWithPatternList()
    {
        String[] array = {"cBioPortal", "GenomeNexus", "Make cBioPortal great again", "Also don't forget Genome Nexus"};

        List<Pattern> patterns = Patterns.toStartsWithPatternList(Arrays.asList(array));

        assertTrue(patterns.get(0).matcher("cBioPortal is amazing").matches());
        assertFalse(patterns.get(0).matcher("cBio").matches());
        assertFalse(patterns.get(0).matcher("Portal is amazing").matches());

        assertTrue(patterns.get(1).matcher("GenomeNexus is even more amazing").matches());
        assertFalse(patterns.get(1).matcher("Nexus is amazing").matches());
        assertFalse(patterns.get(1).matcher("Genome").matches());

        assertTrue(patterns.get(2).matcher("Make cBioPortal great again and again").matches());
        assertFalse(patterns.get(2).matcher("cBioPortal is great again").matches());
        assertFalse(patterns.get(2).matcher("Make cBioPortal").matches());

        assertTrue(patterns.get(3).matcher("Also don't forget Genome Nexus!").matches());
        assertFalse(patterns.get(3).matcher("don't forget Genome Nexus").matches());
        assertFalse(patterns.get(3).matcher("Also don't forget").matches());

        patterns = Patterns.toStartsWithPatternList(array[0]);
        assertTrue(patterns.get(0).matcher("cBioPortal is still amazing").matches());
        assertFalse(patterns.get(0).matcher("cBio").matches());
    }
}
