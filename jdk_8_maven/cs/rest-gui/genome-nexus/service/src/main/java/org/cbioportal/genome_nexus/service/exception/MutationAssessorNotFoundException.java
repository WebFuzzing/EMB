package org.cbioportal.genome_nexus.service.exception;

public class MutationAssessorNotFoundException extends Exception
{
    private String variant;

    public MutationAssessorNotFoundException(String variant) {
        super();
        this.variant = variant;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    @Override
    public String getMessage() {
        return "Mutation Assessor annotation not found for variant: " + this.getVariant();
    }
}
