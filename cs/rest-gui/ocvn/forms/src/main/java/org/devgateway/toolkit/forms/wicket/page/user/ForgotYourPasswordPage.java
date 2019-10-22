package org.devgateway.toolkit.forms.wicket.page.user;

import de.agilecoders.wicket.core.markup.html.bootstrap.form.BootstrapForm;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.model.StringResourceModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.spring.injection.annot.SpringBean;
import org.devgateway.ocds.web.spring.SendEmailService;
import org.devgateway.toolkit.forms.wicket.components.form.TextFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.page.BasePage;
import org.devgateway.toolkit.persistence.dao.Person;
import org.devgateway.toolkit.persistence.repository.PersonRepository;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath(value = "/forgotPassword")
public class ForgotYourPasswordPage extends BasePage {
    private static final long serialVersionUID = -6767090562116351915L;

    @SpringBean
    private PersonRepository personRepository;

    @SpringBean
    private SendEmailService sendEmailService;

    public static final int RANDOM_PASSWORD_LENGTH = 16;

    public ForgotYourPasswordPage(final PageParameters parameters) {
        super(parameters);

        ForgotPasswordForm form = new ForgotPasswordForm("form");
        add(form);
    }

    class ForgotPasswordForm extends BootstrapForm<Void> {
        private static final long serialVersionUID = 7708855731894924277L;

        private String emailAddress;

        private Label message;

        private IndicatingAjaxButton goBack;

        ForgotPasswordForm(final String componentId) {
            super(componentId);
        }

        @Override
        protected void onInitialize() {
            super.onInitialize();

            final TextFieldBootstrapFormComponent<String> emailAddressField = new TextFieldBootstrapFormComponent<>(
                    "emailAddress", new StringResourceModel("emailMessage", ForgotYourPasswordPage.this, null),
                    new PropertyModel<>(this, "emailAddress"));
            emailAddressField.required();
            emailAddressField.setOutputMarkupId(true);
            add(emailAddressField);

            message = new Label("message", new StringResourceModel("checkMessage", ForgotYourPasswordPage.this, null));
            message.setVisibilityAllowed(false);
            message.setOutputMarkupId(true);
            add(message);

            final IndicatingAjaxButton submit = new IndicatingAjaxButton("submit",
                    new StringResourceModel("submit.label", ForgotYourPasswordPage.this, null)) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    StandardPasswordEncoder encoder = new StandardPasswordEncoder("");
                    Person person = personRepository.findByEmail(emailAddress);

                    if (person == null) {
                        feedbackPanel.error("Email address not found");
                    } else {
                        String newPassword = RandomStringUtils.random(RANDOM_PASSWORD_LENGTH, true, true);
                        person.setPassword(encoder.encode(newPassword));
                        person.setChangePassword(true);

                        personRepository.saveAndFlush(person);
                        sendEmailService.sendEmailResetPassword(person, newPassword);

                        emailAddressField.setVisibilityAllowed(false);
                        this.setVisibilityAllowed(false);

                        message.setVisibilityAllowed(true);
                        goBack.setVisibilityAllowed(true);

                        target.add(form);
                    }

                    target.add(feedbackPanel);
                }

                @Override
                protected void onError(final AjaxRequestTarget target, final Form<?> form) {
                    target.add(feedbackPanel);
                }
            };
            submit.setOutputMarkupId(true);
            add(submit);

            goBack = new IndicatingAjaxButton("goBack",
                    new StringResourceModel("back", ForgotYourPasswordPage.this, null)) {
                private static final long serialVersionUID = 1L;

                @Override
                protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                    setResponsePage(LoginPage.class);
                }
            };
            goBack.setVisibilityAllowed(false);
            goBack.setOutputMarkupId(true);
            add(goBack);
        }
    }
}
