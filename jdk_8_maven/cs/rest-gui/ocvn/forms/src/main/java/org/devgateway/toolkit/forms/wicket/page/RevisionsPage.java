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
package org.devgateway.toolkit.forms.wicket.page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.datetime.markup.html.basic.DateLabel;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.DefaultRevisionEntity;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author mpostelnicu
 *
 */
@MountPath(value = "/revisions")
@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
public class RevisionsPage extends BasePage {

    @SpringBean
    private EntityManager entityManager;

    private static final long serialVersionUID = 1L;

    /**
     * @param parameters
     */
    public RevisionsPage(final PageParameters parameters) {
        super(parameters);

        final long entityId = parameters.get(WebConstants.PARAM_ID).toLong();
        String entityClass = parameters.get(WebConstants.PARAM_ENTITY_CLASS).toString();

        Class<?> clazz = null;
        try {
            clazz = Class.forName(entityClass);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        AuditReader reader = AuditReaderFactory.get(entityManager);

        AuditQuery query = reader.createQuery().forRevisionsOfEntity(clazz, false, true);
        query.add(AuditEntity.property("id").eq(entityId));

        List<?> resultList = query.getResultList();
        List<DefaultRevisionEntity> items = new ArrayList<>();
        for (Object item : resultList) {
            Object[] obj = (Object[]) item;
            items.add((DefaultRevisionEntity) obj[1]);
        }

        add(new ListView<DefaultRevisionEntity>("revisions", items) {

            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<DefaultRevisionEntity> item) {
                final PageParameters pp = new PageParameters();
                pp.set(WebConstants.PARAM_REVISION_ID, item.getModelObject().getId());
                pp.set(WebConstants.PARAM_ID, entityId);

                item.add(new Label("revisionNumber", new PropertyModel<Integer>(item.getModel(), "id")));
                item.add(DateLabel.forDatePattern("revisionDate",
                        new PropertyModel<Date>(item.getModel(), "revisionDate"), "yyyy/MM/dd @HH:mm:ss z"));
            }
        });
    }
}
