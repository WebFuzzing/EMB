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
package org.devgateway.toolkit.forms.wicket.page.user;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.authroles.authentication.AbstractAuthenticatedWebSession;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.cycle.RequestCycle;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.apache.wicket.util.string.StringValue;
import org.apache.wicket.util.time.Duration;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.web.security.SecurityUtil;
import org.devgateway.toolkit.forms.wicket.SSAuthenticatedWebSession;
import org.devgateway.toolkit.forms.wicket.components.form.PasswordFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.components.form.TextFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.page.BasePage;
import org.devgateway.toolkit.forms.wicket.page.Homepage;
import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.wicketstuff.annotation.mount.MountPath;

import de.agilecoders.wicket.core.markup.html.bootstrap.common.NotificationPanel;
import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;

/**
 * @author mpostelnicu
 *
 */
@MountPath("/login")
public class LoginPage extends BasePage {
    private static final long serialVersionUID = 1L;

    @SpringBean
    private PersonRepository personRepository;

    private static final int HIDE_NOTIFICATION_SECONDS = 15;

    class LoginForm extends BootstrapForm<Void> {
        private static final long serialVersionUID = 2066636625524650473L;

        private String username;

        private String password;

        private String referrer;

        LoginForm(final String id) {
            super(id);

            pageTitle.setVisible(false);
        }

        protected void retrieveReferrerFromSavedRequestIfPresent() {
            StringValue referrerParam = RequestCycle.get().getRequest().getRequestParameters()
                    .getParameterValue("referrer");
            if (!referrerParam.isEmpty()) {
                referrer = referrerParam.toString();
            }
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            retrieveReferrerFromSavedRequestIfPresent();

            NotificationPanel notificationPanel = new NotificationPanel("loginFeedback");
            notificationPanel.hideAfter(Duration.seconds(HIDE_NOTIFICATION_SECONDS));
            notificationPanel.setOutputMarkupId(true);
            add(notificationPanel);

            final TextFieldBootstrapFormComponent<String> username = new TextFieldBootstrapFormComponent<>("username",
                    new StringResourceModel("user", LoginPage.this, null), new PropertyModel<String>(this, "username"));
            username.required();
            add(username);

            final PasswordFieldBootstrapFormComponent password =
                    new PasswordFieldBootstrapFormComponent("password", new PropertyModel<>(this, "password"));
            password.getField().setResetPassword(false);
            add(password);

            final IndicatingAjaxButton submit =
                    new IndicatingAjaxButton("submit", new StringResourceModel("submit.label", LoginPage.this, null)) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                            SSAuthenticatedWebSession session =
                                    SSAuthenticatedWebSession.getSSAuthenticatedWebSession();
                            if (session.signIn(LoginForm.this.username, LoginForm.this.password)) {
                                Person user = SecurityUtil.getCurrentAuthenticatedPerson();
                                if (user.getChangePassword()) {
                                    PageParameters pageParam = new PageParameters();
                                    pageParam.add(WebConstants.PARAM_ID, user.getId());
                                    setResponsePage(ChangePasswordPage.class, pageParam);
                                } else {
                                    if (referrer != null) {
                                        throw new RedirectToUrlException(referrer);
                                    }
                                    setResponsePage(getApplication().getHomePage());
                                }
                            } else if (session.getAe().getMessage().equalsIgnoreCase("User is disabled")) {
                                notificationPanel.error(session.getAe().getMessage());
                                target.add(notificationPanel);
                            } else {
                                notificationPanel.error(getString("bad_credentials"));
                                target.add(notificationPanel);
                            }
                        }

                        @Override
                        protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                            target.add(notificationPanel);
                        }
                    };
            add(submit);

            final IndicatingAjaxButton forgotPassword = new IndicatingAjaxButton("forgotPassword",
                    new StringResourceModel("forgotPassword", LoginPage.this, null)) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onConfigure() {
                    super.onConfigure();
                    setDefaultFormProcessing(false);
                }

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    setResponsePage(ForgotYourPasswordPage.class);
                }
            };
            add(forgotPassword);
        }
    }

    /**
     * @param parameters
     *            The page parameters.
     */
    public LoginPage(final PageParameters parameters) {
        super(parameters);

        // redirect to homepage if user reaches the /login page while
        // authenticated
        if (AbstractAuthenticatedWebSession.get().isSignedIn()) {
            setResponsePage(Homepage.class);
        }

        LoginForm loginForm = new LoginForm("loginform");
        add(loginForm);
    }
}