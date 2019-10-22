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
package org.devgateway.toolkit.forms.wicket.behaviors;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.util.template.PackageTextTemplate;

public abstract class JavascriptCallbackAjaxBehavior extends AbstractDefaultAjaxBehavior {

    private static final long serialVersionUID = 1L;
    protected PackageTextTemplate scriptTemplate;

    public JavascriptCallbackAjaxBehavior(final PackageTextTemplate scriptTemplate) {
        this.scriptTemplate = scriptTemplate;
    }

    public abstract String getCallbackArguments();

    @Override
    public void renderHead(final Component component, final IHeaderResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("callbackUrl", getCallbackUrl().toString());
        map.put("args", getCallbackArguments());
        map.put("componentMarkupId", component.getMarkupId());

        try {
            if (scriptTemplate != null) {
                OnDomReadyHeaderItem onDomReadyHeaderItem =
                        OnDomReadyHeaderItem.forScript(scriptTemplate.asString(map));
                scriptTemplate.close();
                response.render(onDomReadyHeaderItem);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
