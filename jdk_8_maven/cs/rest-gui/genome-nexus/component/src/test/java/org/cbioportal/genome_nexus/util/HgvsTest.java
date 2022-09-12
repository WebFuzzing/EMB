package org.cbioportal.genome_nexus.util;

import org.junit.Test;

import static org.junit.Assert.*;

public class HgvsTest
{
    @Test
    public void buildVariantId()
    {
        String[] hgvsIds = {
            "17:g.41276045_41276046del",
            "7:g.140453136A>T",
            "17:g.41242962_41242963insGA",
            "13:g.28611219_28611221delTGTinsGC",
            "chr1:g.182712A>C"
        };

        assertEquals("chr17:g.41276045_41276046del", Hgvs.addChrPrefix(hgvsIds[0]));
        assertEquals("17:g.41276045_41276046del", Hgvs.removeDeletedBases(hgvsIds[0]));
        
        assertEquals("chr7:g.140453136A>T", Hgvs.addChrPrefix(hgvsIds[1]));
        assertEquals("7:g.140453136A>T", Hgvs.removeDeletedBases(hgvsIds[1]));

        assertEquals("chr17:g.41242962_41242963insGA", Hgvs.addChrPrefix(hgvsIds[2]));
        assertEquals("17:g.41242962_41242963insGA", Hgvs.removeDeletedBases(hgvsIds[2]));

        assertEquals("chr13:g.28611219_28611221delTGTinsGC", Hgvs.addChrPrefix(hgvsIds[3]));
        assertEquals("13:g.28611219_28611221delinsGC", Hgvs.removeDeletedBases(hgvsIds[3]));

        assertEquals("chr1:g.182712A>C", Hgvs.addChrPrefix(hgvsIds[4]));
        assertEquals("chr1:g.182712A>C", Hgvs.removeDeletedBases(hgvsIds[4]));

    }

}
