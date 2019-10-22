/*******************************************************************************
 * Copyright (c) 2016 Development Gateway, Inc and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the MIT License (MIT)
 * which accompanies this distribution, and is available at
 * https://opensource.org/licenses/MIT
 *
 * Contributors:
 * Development Gateway - initial API and implementation
 *******************************************************************************/
package org.devgateway.ocds.forms.wicket.page.list;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.ocds.forms.wicket.providers.PersonDashboardJpaRepositoryProvider;
import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.providers.SortableJpaRepositoryDataProvider;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.wicketstuff.annotation.mount.MountPath;

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_PROCURING_ENTITY)
@MountPath(value = "/listMyDashboards")
public class ListMyDashboardsPage extends ListAllDashboardsPage {

    /**
     * 
     */
    private static final long serialVersionUID = 8105049572554654046L;

    @SpringBean
    private PersonRepository personRepository;

    
    @Override
    public SortableJpaRepositoryDataProvider<UserDashboard> getProvider() {
        return new PersonDashboardJpaRepositoryProvider(userDashboardRepository, personRepository);
    }

    public ListMyDashboardsPage(final PageParameters pageParameters) {
        super(pageParameters);
        
        columns.remove(columnUsers);
        columns.remove(columnDefaultDashboardUsers);
    }

}
