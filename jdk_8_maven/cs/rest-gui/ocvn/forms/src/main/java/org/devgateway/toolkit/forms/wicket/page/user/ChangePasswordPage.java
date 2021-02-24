package org.devgateway.toolkit.forms.wicket.page.user;

import org.apache.wicket.authroles.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devgateway.toolkit.web.security.SecurityConstants;
import org.wicketstuff.annotation.mount.MountPath;

@AuthorizeInstantiation(SecurityConstants.Roles.ROLE_USER)
@MountPath(value = "/changePassword")
public class ChangePasswordPage extends EditUserPage {
    private static final long serialVersionUID = 7378006795457575557L;

    public ChangePasswordPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        userName.setVisibilityAllowed(false);
        firstName.setVisibilityAllowed(false);
        lastName.setVisibilityAllowed(false);
        email.setVisibilityAllowed(false);
        title.setVisibilityAllowed(false);
        group.setVisibilityAllowed(false);
        roles.setVisibilityAllowed(false);
        enabled.setVisibilityAllowed(false);
        changePassword.setVisibilityAllowed(false);
        changePass.setVisibilityAllowed(false);
    }

    @Override
    protected boolean isChangePassPage() {
        return true;
    }
}
