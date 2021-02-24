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
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.page.user.EditUserPageElevated;
import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.wicketstuff.annotation.mount.MountPath;

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
@MountPath(value = "/listusers")
public class ListUserPage extends AbstractListPage<Person> {

    private static final long serialVersionUID = 3529738250403399032L;

    @SpringBean
    protected PersonRepository userRepository;

    public ListUserPage(final PageParameters pageParameters) {
        super(pageParameters);
        this.jpaRepository = userRepository;
        this.editPageClass = EditUserPageElevated.class;
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Name"), "username", "username"));
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Group"), "group", "group"));
        columns.add(new PropertyColumn<Person, String>(new Model<String>("Roles"), "roles", "roles"));
    }

}