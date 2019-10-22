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
package org.devgateway.toolkit.forms.wicket.components.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.checkbox.bootstraptoggle.BootstrapToggle;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.checkbox.bootstraptoggle.BootstrapToggleConfig;

/**
 * @author mpostelnicu
 * 
 */
public class CheckBoxToggleBootstrapFormComponent
        extends GenericEnablingBootstrapFormComponent<Boolean, BootstrapToggle> {
    private static final long serialVersionUID = -4032850928243673675L;

    private Boolean isFloatedInput = false;

    private BootstrapToggleConfig config;
    private CheckBox wrappedCheckbox;

    public CheckBoxToggleBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<Boolean> model) {
        super(id, labelModel, model);
    }

    /**
     * @param id
     * @param model
     */
    public CheckBoxToggleBootstrapFormComponent(final String id, final IModel<Boolean> model) {
        super(id, model);
    }

    public CheckBoxToggleBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        if (isFloatedInput) {
            Attributes.addClass(tag, "floated-input");
        }
    }

    @Override
    protected FormComponent<Boolean> updatingBehaviorComponent() {
        return wrappedCheckbox;
    }

    @Override
    protected BootstrapToggle inputField(final String id, final IModel<Boolean> model) {

        config = new BootstrapToggleConfig();
        config.withOnStyle(BootstrapToggleConfig.Style.info).withOffStyle(BootstrapToggleConfig.Style.warning)
                .withStyle("customCssClass");

        final BootstrapToggle checkBoxToggle = new BootstrapToggle("field", initFieldModel(), config) {

            private static final long serialVersionUID = 1L;

            @Override
            protected CheckBox newCheckBox(final String id, final IModel<Boolean> model) {
                wrappedCheckbox = super.newCheckBox(id, model);
                wrappedCheckbox.add(new AjaxFormComponentUpdatingBehavior("change") {

                    private static final long serialVersionUID = 1L;

                    @Override
                    protected void onUpdate(final AjaxRequestTarget target) {
                        CheckBoxToggleBootstrapFormComponent.this.onUpdate(target);
                    }
                });
                return wrappedCheckbox;
            }
        };

        return checkBoxToggle;
    }

    @Override
    public String getUpdateEvent() {
        return "change";
    }

    public Boolean getIsFloatedInput() {
        return isFloatedInput;
    }

    public void setIsFloatedInput(final Boolean isFloatedInput) {
        this.isFloatedInput = isFloatedInput;
    }

    @Override
    protected boolean boundComponentsVisibilityAllowed(final Boolean selectedValue) {
        return selectedValue;
    }

    public BootstrapToggleConfig getConfig() {
        return config;
    }
}
