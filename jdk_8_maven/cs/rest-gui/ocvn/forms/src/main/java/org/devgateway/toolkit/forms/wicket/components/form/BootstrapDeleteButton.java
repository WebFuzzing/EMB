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
package org.devgateway.toolkit.forms.wicket.components.form;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.attributes.AjaxCallListener;
import org.apache.wicket.ajax.attributes.AjaxRequestAttributes;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.devgateway.toolkit.forms.wicket.components.ComponentUtil;
import org.devgateway.toolkit.forms.wicket.events.EditingDisabledEvent;

import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconType;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.ladda.LaddaAjaxButton;

/**
 * @author mpostelnicu
 * 
 */
public abstract class BootstrapDeleteButton extends LaddaAjaxButton {

    private static final long serialVersionUID = 8306451874943978003L;

    /**
     * @param id
     * @param model
     */
    public BootstrapDeleteButton(final String id, final IModel<String> model) {
        super(id, model, Buttons.Type.Danger);
    }

    public BootstrapDeleteButton(final String id) {
        super(id, Buttons.Type.Danger);
    }

    @Override
    protected abstract void onSubmit(AjaxRequestTarget target, Form<?> form);

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(new AttributeAppender("onclick", new Model<String>("window.onbeforeunload = null;"), " "));
        setDefaultFormProcessing(false);
        setIconType(FontAwesomeIconType.trash_o);

        if (ComponentUtil.isViewMode()) {
            setVisibilityAllowed(false);
        }
    }

    @Override
    public void onEvent(final IEvent<?> event) {
        if (event.getPayload() instanceof EditingDisabledEvent) {
            this.setEnabled(false);
        }
    }

    @Override
    protected void updateAjaxAttributes(final AjaxRequestAttributes attributes) {

        super.updateAjaxAttributes(attributes);
        AjaxCallListener ajaxCallListener = new AjaxCallListener();
        ajaxCallListener.onPrecondition("return confirm('Confirm Delete?');");
        attributes.getAjaxCallListeners().add(ajaxCallListener);
    }

}
