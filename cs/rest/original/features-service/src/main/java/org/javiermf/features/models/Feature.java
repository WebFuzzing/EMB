package org.javiermf.features.models;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Feature {

    @Id
    @GeneratedValue
    Long id;

    @Column(nullable = false)
    String name;

    @Column
    String description;

    @JsonIgnore
    @ManyToOne
    Product product;

    @JsonIgnore
    @ManyToMany(mappedBy = "activedFeatures")
    Set<ProductConfiguration> inConfigurations;

    public static Feature withName(Product product, String featureName) {
        Feature feature = new Feature();
        feature.name = featureName;
        feature.product = product;
        return feature;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Feature)) return false;

        Feature feature = (Feature) o;

        if (!name.equals(feature.name)) return false;
        return product.equals(feature.product);

    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }
}
