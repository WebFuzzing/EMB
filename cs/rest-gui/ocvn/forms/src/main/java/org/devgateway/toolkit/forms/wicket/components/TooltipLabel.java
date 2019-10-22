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

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.StringResourceModel;

import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipBehavior;
import de.agilecoders.wicket.core.markup.html.bootstrap.components.TooltipConfig;

/**
 * @author mpostelnicu
 *
 */
public class TooltipLabel extends Label {
    private static final long serialVersionUID = 1L;

    private StringResourceModel helpModelText;

    private String fieldId;

    private boolean configWithHtml = true;

    private TooltipConfig.OpenTrigger configWithTrigger = TooltipConfig.OpenTrigger.hover;

    private static CustomTooltipConfig tooltipConfig;

    public class CustomTooltipConfig extends TooltipConfig {
        private static final long serialVersionUID = 1L;

        public CustomTooltipConfig() {
            if (configWithHtml) {
                withHtml(true);
            }
            withTrigger(configWithTrigger);
        }
    }

    /**
     * @param id
     */
    public TooltipLabel(final String id, final String fieldId) {
        super(id, Model.of(""));
        this.fieldId = fieldId;
        add(AttributeModifier.append("class", "fa fa-question-circle"));
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.Component#onConfigure()
     */
    @Override
    protected void onConfigure() {
        super.onConfigure();
        helpModelText = new StringResourceModel(fieldId + ".help", this);
        helpModelText.setDefaultValue("");

        if (!helpModelText.getString().isEmpty()) {
            tooltipConfig = new CustomTooltipConfig();
            add(new TooltipBehavior(helpModelText, tooltipConfig));
            setVisibilityAllowed(!ComponentUtil.isViewMode());
        } else {
            setVisibilityAllowed(false);
        }
    }

    public TooltipConfig.OpenTrigger getConfigWithTrigger() {
        return configWithTrigger;
    }

    public void setConfigWithTrigger(final TooltipConfig.OpenTrigger configWithTrigger) {
        this.configWithTrigger = configWithTrigger;
    }

    public boolean isConfigWithHtml() {
        return configWithHtml;
    }

    public void setConfigWithHtml(final boolean configWithHtml) {
        this.configWithHtml = configWithHtml;
    }
}
