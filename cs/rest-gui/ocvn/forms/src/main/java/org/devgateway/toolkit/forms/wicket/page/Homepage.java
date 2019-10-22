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
/**
 *
 */
package org.devgateway.toolkit.forms.wicket.page;

import org.apache.wicket.Component;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.authroles.authorization.strategies.role.metadata.MetaDataRoleAuthorizationStrategy;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devgateway.toolkit.web.security.SecurityConstants;

/**
 * @author mpostelnicu
 *
 */
@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_USER)
public class Homepage extends BasePage {
    private static final long serialVersionUID = 1L;

    /**
     * @param parameters
     */
    public Homepage(final PageParameters parameters) {
        super(parameters);
        
        
    }
    
    @Override
    protected void onInitialize() {
        super.onInitialize();
        TransparentWebMarkupContainer manageUsersPanel = new TransparentWebMarkupContainer("manageUsers");
        MetaDataRoleAuthorizationStrategy.authorize(manageUsersPanel, Component.RENDER,
                SecurityConstants.Roles.ROLE_ADMIN);
        add(manageUsersPanel);

    }

}
