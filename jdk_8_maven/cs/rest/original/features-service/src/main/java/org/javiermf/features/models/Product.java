package org.javiermf.features.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.javiermf.features.exceptions.ObjectNotFoundException;
import org.javiermf.features.models.constraints.FeatureConstraint;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Product {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String name;

    @OneToMany(mappedBy = "product", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<Feature> productFeatures = new HashSet<Feature>();

    @OneToMany(mappedBy = "forProduct", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    Set<FeatureConstraint> productFeatureConstraints = new HashSet<FeatureConstraint>();

    @JsonProperty("features")
    public Set<Feature> getProductFeatures() {
        return productFeatures;
    }

    public Long getId() {
        return id;
    }

    public void addFeature(Feature feature) {
        productFeatures.add(feature);
    }

    public void removeFeature(Feature feature) {
        productFeatures.remove(feature);
    }

    public static Product buildWithFeatures(String... featureNames) {
        Product product = new Product();
        for (String featureName : featureNames) {
            product.addFeature(Feature.withName(product, featureName));
        }

        return product;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("constraints")
    public Set<FeatureConstraint> getProductFeatureConstraints() {
        return productFeatureConstraints;
    }

    public Feature findProductFeatureByName(String featureName) {
        for (Feature feature : getProductFeatures()) {
            if (feature.name.equalsIgnoreCase(featureName)) {
                return feature;
            }
        }

        throw new ObjectNotFoundException(featureName);
    }

    public boolean hasFeatureNamed(String featureName) {
        for (Feature feature : getProductFeatures()) {
            if (feature.name.equalsIgnoreCase(featureName)) {
                return true;
            }
        }

        return false;
    }

    public void addFeatureConstraint(FeatureConstraint constraint) {
        productFeatureConstraints.add(constraint);
    }
}
