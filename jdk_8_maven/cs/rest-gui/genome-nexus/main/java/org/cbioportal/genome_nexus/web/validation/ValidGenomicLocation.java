package org.cbioportal.genome_nexus.web.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { GenomicLocationValidator.class })
public @interface ValidGenomicLocation {
    String message() default "genomicLocation is incorrect";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
