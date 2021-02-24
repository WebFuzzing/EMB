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
package org.devgateway.toolkit.forms.wicket.page.lists;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.page.EditGroupPage;
import org.devgateway.toolkit.persistence.dao.categories.Group;
import org.devgateway.toolkit.persistence.repository.GroupRepository;
import org.wicketstuff.annotation.mount.MountPath;

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
@MountPath(value = "/listgroups")
public class ListGroupPage extends AbstractListPage<Group> {

    /**
     * 
     */
    private static final long serialVersionUID = -324298525712620234L;
    @SpringBean
    protected GroupRepository groupRepository;

    public ListGroupPage(final PageParameters pageParameters) {
        super(pageParameters);
        this.jpaRepository = groupRepository;
        this.editPageClass = EditGroupPage.class;
        columns.add(new PropertyColumn<Group, String>(
                new Model<String>((new StringResourceModel("name", ListGroupPage.this, null)).getString()), "label",
                "label"));
    }

}
