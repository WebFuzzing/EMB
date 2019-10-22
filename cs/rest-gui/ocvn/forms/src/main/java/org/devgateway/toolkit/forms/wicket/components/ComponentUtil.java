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
package org.devgateway.toolkit.forms.wicket.components;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.event.IEvent;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.request.cycle.RequestCycle;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.forms.wicket.components.form.GenericBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.events.EditingDisabledEvent;
import org.devgateway.toolkit.forms.wicket.events.EditingEnabledEvent;

/**
 * @author mpostelnicu
 *
 */
public final class ComponentUtil {

    private ComponentUtil() {

    }

    /**
     * Trivial method to set the child {@link GenericBootstrapFormComponent}
     * required when added to the parent {@link WebMarkupContainer}
     * 
     * @param requiredFlag
     *            the {@link FormComponent#setRequired(boolean)}
     * @param parent
     * @param child
     * 
     * @return the parent
     */
    public static MarkupContainer addRequiredFlagBootstrapFormComponent(final boolean requiredFlag,
            final WebMarkupContainer parent, final GenericBootstrapFormComponent<?, ?> child) {
        return parent.add(requiredFlag ? child.required() : child);
    }

    /**
     * Returns true if the {@link WebConstants#PARAM_VIEW_MODE} is used as a
     * parameter
     * 
     * @return
     */
    public static boolean isViewMode() {
        return RequestCycle.get().getRequest().getRequestParameters().getParameterValue(WebConstants.PARAM_VIEW_MODE)
                .toBoolean(false);
    }

    public static void enableDisableEvent(final Component c, final IEvent<?> event) {
        if (event.getPayload() instanceof EditingDisabledEvent) {
            c.setEnabled(false);
        }

        if (event.getPayload() instanceof EditingEnabledEvent) {
            c.setEnabled(true);
        }

    }

}
