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
package org.devgateway.toolkit.forms.wicket.events;

import org.apache.wicket.ajax.AjaxRequestTarget;

public class AjaxUpdateEvent {
    private final AjaxRequestTarget target;

    public AjaxUpdateEvent(final AjaxRequestTarget target) {
        this.target = target;
    }

    public AjaxRequestTarget getAjaxRequestTarget() {
        return target;
    }
}