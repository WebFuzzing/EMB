package org.javiermf.features.daos;

import com.mysema.query.jpa.impl.JPADeleteClause;
import com.mysema.query.jpa.impl.JPAQuery;
import org.javiermf.features.exceptions.ObjectNotFoundException;
import org.javiermf.features.models.Feature;
import org.javiermf.features.models.Product;
import org.javiermf.features.models.QFeature;
import org.javiermf.features.models.QProduct;
import org.javiermf.features.models.constraints.FeatureConstraint;
import org.javiermf.features.models.constraints.QFeatureConstraint;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class ProductsDAO {

    @PersistenceContext
    private EntityManager entityManager;

    QProduct qProduct = QProduct.product;
    QFeature qFeature = QFeature.feature;
    QFeatureConstraint qFeatureConstraint = QFeatureConstraint.featureConstraint;

    public List<Product> findAll() {
        JPAQuery query = new JPAQuery(entityManager);

        return query.from(qProduct).list(qProduct);
    }

    public Product findByName(String name) {
        JPAQuery query = new JPAQuery(entityManager);

        query.from(qProduct).where(qProduct.name.eq(name));
        Product product = query.singleResult(qProduct);
        if (product == null) {
            throw new ObjectNotFoundException(name);
        }
        return product;
    }

    @Transactional
    public void deleteByName(String productName) {
        Product product = findByName(productName);
        entityManager.remove(product);

    }

    @Transactional
    public void deleteConstraintsForProduct(String productName) {
        Product product = findByName(productName);
        for (FeatureConstraint featureConstraint : product.getProductFeatureConstraints()) {
            entityManager.remove(featureConstraint);
        }
        product.getProductFeatureConstraints().clear();
    }

    @Transactional
    public void insert(Product product) {
        entityManager.persist(product);
    }

    @Transactional
    public void insertFeature(Feature feature) {
        entityManager.persist(feature);
    }

    @Transactional
    public void deleteFeature(Feature feature) {
        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qFeature);
        deleteClause.where(qFeature.id.eq(feature.getId()));
        deleteClause.execute();
    }

    @Transactional
    public void insertConstraint(FeatureConstraint constraint) {
        entityManager.persist(constraint);
    }

    @Transactional
    public void deleteConstraintForProduct(String productName, Long constraintId) {
        JPADeleteClause deleteClause = new JPADeleteClause(entityManager, qFeatureConstraint);
        deleteClause.where(qFeatureConstraint.id.eq(constraintId));
        deleteClause.execute();
    }
}
