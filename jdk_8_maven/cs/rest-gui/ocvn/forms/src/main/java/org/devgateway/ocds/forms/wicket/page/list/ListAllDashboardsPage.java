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

import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapExternalLink;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.ocds.forms.wicket.page.edit.EditUserDashboardPage;
import org.devgateway.ocds.persistence.dao.UserDashboard;
import org.devgateway.ocds.persistence.repository.UserDashboardRepository;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.devgateway.toolkit.forms.wicket.page.lists.AbstractListPage;
import org.wicketstuff.annotation.mount.MountPath;

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_ADMIN)
@MountPath(value = "/listAllDashboards")
public class ListAllDashboardsPage extends AbstractListPage<UserDashboard> {

    /**
     *
     */
    private static final long serialVersionUID = -324298525712620234L;
    @SpringBean
    protected UserDashboardRepository userDashboardRepository;
    protected PropertyColumn<UserDashboard, String> columnName;
    protected PropertyColumn<UserDashboard, String> columnDefaultDashboardUsers;
    protected PropertyColumn<UserDashboard, String> columnUsers;

    public class DashboardsActionPanel extends ActionPanel {

        /**
         * @param id
         * @param model
         */
        public DashboardsActionPanel(String id, IModel<UserDashboard> model) {
            super(id, model);

            UserDashboard entity = (UserDashboard) this.getDefaultModelObject();

            BootstrapExternalLink viewLink = new BootstrapExternalLink("view", Model.of("ui/index.html?dashboardId="
                    + entity.getId()), Buttons.Type.Danger) {
            };
            viewLink.setLabel(new StringResourceModel("view", ListAllDashboardsPage.this, null));
            viewLink.setIconType(FontAwesomeIconType.eye).setSize(Buttons.Size.Small);
            add(viewLink);

        }
    }

    public ListAllDashboardsPage(final PageParameters pageParameters) {
        super(pageParameters);
        this.jpaRepository = userDashboardRepository;
        this.editPageClass = EditUserDashboardPage.class;

        columnName = new PropertyColumn<UserDashboard, String>(
                new Model<String>((new StringResourceModel("name", ListAllDashboardsPage.this, null)).getString()),
                "name", "name");

        columns.add(columnName);

        columnDefaultDashboardUsers = new PropertyColumn<UserDashboard, String>(new Model<String>(
                (new StringResourceModel("defaultDashboardUsers", ListAllDashboardsPage.this, null)).getString()),
                "defaultDashboardUsers");

        columns.add(columnDefaultDashboardUsers);

        columnUsers = new PropertyColumn<UserDashboard, String>(
                new Model<String>((new StringResourceModel("users", ListAllDashboardsPage.this, null)).getString()),
                "users");

        columns.add(columnUsers);

    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        editPageLink.setVisibilityAllowed(false);
    }

    @Override
    public ActionPanel getActionPanel(String id, IModel<UserDashboard> model) {
        return new DashboardsActionPanel(id, model);
    }
}
