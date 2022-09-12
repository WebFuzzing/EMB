package org.cbioportal.genome_nexus.web.validation;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
public class GenomicLocationValidatorTest
{
    @Test
    public void testMyVariantInfoAnnotation()
    {
        String[] genomicLocation = {
            "7,140453136,140453136,A,ATGC-T", // true
            "A,37880220,37880220,T,C", // false
            "7,-1,37879794,G,C", // false
            "12,25398284,-2,CC,GA", // false
            "12,25380271,25380271,12,T", // false
            "10,89692940,89692940,C,Q", // false
            "X,89692940,89692940,C,", // false
            "X_89692940_89692940_C_T", //false
            "MT,10360,10360,G,A", // true
        };
        GenomicLocationValidator validator = new GenomicLocationValidator();
        assertEquals(true, validator.isValid(genomicLocation[0], null));
        assertEquals(false, validator.isValid(genomicLocation[1], null));
        assertEquals(false, validator.isValid(genomicLocation[2], null));
        assertEquals(false, validator.isValid(genomicLocation[3], null));
        assertEquals(false, validator.isValid(genomicLocation[4], null));
        assertEquals(false, validator.isValid(genomicLocation[5], null));
        assertEquals(false, validator.isValid(genomicLocation[6], null));
        assertEquals(false, validator.isValid(genomicLocation[7], null));
        assertEquals(true, validator.isValid(genomicLocation[8], null));
    }
}

