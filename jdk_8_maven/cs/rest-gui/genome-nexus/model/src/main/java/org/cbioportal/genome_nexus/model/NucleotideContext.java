
package org.cbioportal.genome_nexus.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "ensembl.nucleotide_context")
public class NucleotideContext
{
    // query e.g. 17:37880219..37880221:1
    @Id
    private String query;

    // molecule e.g DNA
    private String molecule;
    // id e.g. chromosome:GRCh37:17:37880219:37880221:1
    private String id;
    // seq e.g. TTG
    private String seq;

    // added by genome nexus for convenience
    // similar to mutation assessor
    private String hgvs;

    @Field("query")
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Field("seq")
    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    @Field("id")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Field("molecule")
    public String getMolecule() {
        return molecule;
    }

    public void setMolecule(String molecule) {
        this.molecule = molecule;
    }

    public void setHgvs(String hgvs) {
        this.hgvs = hgvs;
    }
    
    public String getHgvs() {
        return this.hgvs;
    }
}
