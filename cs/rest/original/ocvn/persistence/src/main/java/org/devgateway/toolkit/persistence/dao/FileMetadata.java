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

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;

/**
 * @author idobre
 * @since 11/13/14
 *
 *        Entity used to store the metadata of uploaded files
 */

@Entity
@Audited
public class FileMetadata extends AbstractAuditableEntity implements Serializable {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private FileContent content;

    private String name;

    private String contentType;

    private long size;

    private boolean isUserSupportDocument = false;

    @Override
    public String toString() {
        return name;
    }

    public FileContent getContent() {
        return content;
    }

    public void setContent(final FileContent content) {
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(final String contentType) {
        this.contentType = contentType;
    }

    public long getSize() {
        return size;
    }

    public void setSize(final long size) {
        this.size = size;
    }

    public boolean isUserSupportDocument() {
        return isUserSupportDocument;
    }

    public void setUserSupportDocument(final boolean isUserSupportDocument) {
        this.isUserSupportDocument = isUserSupportDocument;
    }

    @Override
    public AbstractAuditableEntity getParent() {
        return null;
    }
}
