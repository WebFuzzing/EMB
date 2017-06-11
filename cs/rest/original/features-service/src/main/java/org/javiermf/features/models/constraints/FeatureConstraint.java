package org.javiermf.features.models.constraints;


import org.javiermf.features.models.Product;
import org.javiermf.features.models.ProductConfiguration;
import org.javiermf.features.models.evaluation.EvaluationResult;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public abstract class FeatureConstraint {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    Long id;

    @ManyToOne
    Product forProduct;

    public abstract String getType();

    public abstract EvaluationResult evaluateConfiguration(EvaluationResult currentResult, ProductConfiguration configuration);

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setForProduct(Product forProduct) {
        this.forProduct = forProduct;
    }
}
