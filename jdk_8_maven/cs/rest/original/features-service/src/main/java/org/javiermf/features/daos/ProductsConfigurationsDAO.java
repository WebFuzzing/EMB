package org.javiermf.features.daos;

import com.mysema.query.jpa.impl.JPAQuery;
import org.javiermf.features.models.Feature;
import org.javiermf.features.models.ProductConfiguration;
import org.javiermf.features.models.QProduct;
import org.javiermf.features.models.QProductConfiguration;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProductsConfigurationsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    QProductConfiguration qProductConfiguration = QProductConfiguration.productConfiguration;
    QProduct qProduct = QProduct.product;

    public List<ProductConfiguration> findByProductName(String productName) {


        JPAQuery query = new JPAQuery(entityManager);
        query.from(qProductConfiguration)
                .innerJoin(qProductConfiguration.product, qProduct)
                .where(qProduct.name.eq(productName));
        return query.list(qProductConfiguration);
    }

    public ProductConfiguration findByNameAndProductName(String productName, String configurationName) {
        QProduct qProduct = QProduct.product;


        JPAQuery query = new JPAQuery(entityManager);
        query.from(qProductConfiguration)
                .innerJoin(qProductConfiguration.product, qProduct)
                .where(qProduct.name.eq(productName)
                        .and(qProductConfiguration.name.eq(configurationName)));
        return query.singleResult(qProductConfiguration);
    }

    @Transactional
    public void deleteConfigurationsForProduct(String productName) {
        for (ProductConfiguration productConfiguration : findByProductName(productName)) {
            entityManager.remove(productConfiguration);
        }

    }

    public List<ProductConfiguration> findConfigurationsWithFeatureActive(Feature feature) {
        JPAQuery query = new JPAQuery(entityManager);
        query.from(qProductConfiguration)
                .where(qProductConfiguration.activedFeatures.contains(feature));

        return query.list(qProductConfiguration);
    }

    @Transactional
    public void insert(ProductConfiguration configuration) {
        entityManager.persist(configuration);

    }

    @Transactional
    public void deleteConfigurationForProduct(String productName, String configurationName) {
        ProductConfiguration configuration = findByNameAndProductName(productName, configurationName);
        entityManager.remove(configuration);
    }
}
