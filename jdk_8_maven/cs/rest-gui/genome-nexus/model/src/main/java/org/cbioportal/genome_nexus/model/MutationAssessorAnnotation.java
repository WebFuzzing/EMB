package org.cbioportal.genome_nexus.model;



public class MutationAssessorAnnotation {

    private String license;
    private MutationAssessor annotation;

    public MutationAssessorAnnotation()
    {
        this.license = "http://mutationassessor.org/r3/about.php";
        this.annotation = new MutationAssessor();
    }

    public void setLicense(String license) { this.license = license; }

    public String getLicense() { return this.license; }

    public void setAnnotation(MutationAssessor annotation) { this.annotation = annotation; }

    public MutationAssessor getAnnotation() { return this.annotation; }

}
