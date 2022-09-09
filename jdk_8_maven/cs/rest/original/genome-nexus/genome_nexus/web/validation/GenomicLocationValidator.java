package org.cbioportal.genome_nexus.web.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import org.cbioportal.genome_nexus.web.validation.ValidGenomicLocation;

public class GenomicLocationValidator implements ConstraintValidator<ValidGenomicLocation, String> {
    
    public void initialize(ValidGenomicLocation constraintAnnotation) {
    }

    public boolean isValid(String genomicLocation, ConstraintValidatorContext context) 
    {
        String[] genomicLocationSplit = genomicLocation.split(",");
        if (genomicLocationSplit.length < 5) {
            return false;
        }
        else {
            boolean result = true;
            // chr [1-24,X,Y,MT]
            result &= genomicLocationSplit[0].matches("X") || genomicLocationSplit[0].matches("Y") || genomicLocationSplit[0].matches("MT") ||
            genomicLocationSplit[0].matches("[0-9]+") && Integer.valueOf(genomicLocationSplit[0]) >= 1 && Integer.valueOf(genomicLocationSplit[0]) <= 24;
            // start & end should be positive integer
            result &= Integer.valueOf(genomicLocationSplit[1]) >= 0 && Integer.valueOf(genomicLocationSplit[2]) >= 0;
            // variant & ref allele should only contain T,C,G,A,-
            result &= genomicLocationSplit[3].matches("[TCGA-]*") && genomicLocationSplit[4].matches("[TCGA-]*");
            return result;
        }
    }
}
