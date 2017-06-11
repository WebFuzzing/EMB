/*******************************************************************************
 * Copyright (c) 2015 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.toolkit.persistence.dao;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.springframework.data.domain.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * 
 * @author mpostelnicu
 *
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditableEntity extends GenericPersistable implements Auditable<String, Long> {

    /**
     *
     */
    private static final long serialVersionUID = 4031407178647451427L;

    @Audited
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonIgnore
    private DateTime lastUpdated;

    @Audited
    @JsonIgnore
    private String lastModifiedBy;

    @Audited
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonIgnore
    private DateTime created;

    @Audited
    @JsonIgnore
    private String createdBy;

    /**
     * Forces the envers to see this object as modified, thus enabling creation
     * of a revision record. This is invoked by children entities, if the entity
     * has OneToMany relationships
     * 
     * @see AbstractAuditableEntity#ensureParentUpdated()
     */
    public void touch() {
        setLastModifiedDate(new DateTime());

        // force update of all parents, because PreUpdate does not always get
        // invoked if there is nothing to update
        if (getParent() != null) {
            getParent().touch();
        }
    }

    /**
     * Override this in subclasses and return the parent entity, or null if no
     * parent entity exists
     * 
     * @return
     */
    public abstract AbstractAuditableEntity getParent();

    /**
     * @see http ://stackoverflow.com/questions/10697945/hibernate-envers-track-
     *      revisions -in-the-owning-side-of-a-onetomany-relation updates parent
     *      timestamp when child is updated. Useful for forcing envers to
     *      generate a revision for parents when it generates a revision for
     *      children
     */
    @PreUpdate
    public void ensureParentUpdated() {
        if (getParent() != null) {
            getParent().touch();
        }
    }

    /**
     * Gets created by audit user.
     */
    @Override
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * Sets created by audit user.
     */
    @Override
    public void setCreatedBy(final String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * Gets create audit date.
     */
    @Override
    @JsonIgnore
    public DateTime getCreatedDate() {
        return created;
    }

    /**
     * Sets create audit date.
     */
    @Override
    public void setCreatedDate(final DateTime creationDate) {
        this.created = creationDate;
    }

    /**
     * Gets last modified by audit user.
     */
    @Override
    public String getLastModifiedBy() {
        return lastModifiedBy;
    }

    /**
     * Sets last modified by audit user.
     */
    @Override
    public void setLastModifiedBy(final String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    /**
     * Gets last modified audit date.
     */
    @Override
    @JsonIgnore
    public DateTime getLastModifiedDate() {
        return lastUpdated;
    }

    /**
     * Sets last modified audit date.
     */
    @Override
    public void setLastModifiedDate(final DateTime lastModifiedDate) {
        this.lastUpdated = lastModifiedDate;
    }

}