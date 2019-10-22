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

import java.util.Date;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.IndicatingAjaxLink;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;

import de.agilecoders.wicket.core.util.Attributes;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePicker;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.datetime.DatetimePickerConfig;

/**
 * @author mpostelnicu
 * 
 */
public class DateTimeFieldBootstrapFormComponent extends GenericBootstrapFormComponent<Date, DatetimePicker> {
    private static final long serialVersionUID = 6829640010904041758L;

    public static final String DEFAULT_FORMAT = "dd/MM/yyyy HH:mm:ss";

    private DatetimePickerConfig config;

    private Boolean isFloatedInput = false;

    /**
     * @param id
     * @param labelModel
     * @param model
     */
    public DateTimeFieldBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<Date> model) {
        super(id, labelModel, model);
    }

    public DateTimeFieldBootstrapFormComponent(final String id) {
        super(id);
    }

    /**
     * @param id
     * @param model
     */
    public DateTimeFieldBootstrapFormComponent(final String id, final IModel<Date> model) {
        super(id, model);
    }

    @Override
    protected DatetimePicker inputField(final String id, final IModel<Date> model) {
        config = new DatetimePickerConfig().withFormat(DEFAULT_FORMAT);
        return new DatetimePicker("field", initFieldModel(), config);
    }

    @Override
    public String getUpdateEvent() {
        return "update";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.devgateway.toolkit.forms.wicket.components.form.
     * GenericBootstrapFormComponent#onConfigure()
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();

        border.add(new AttributeModifier("style", "position:relative;"));

        IndicatingAjaxLink<String> clearDateLink = new IndicatingAjaxLink<String>("clearDate") {
            private static final long serialVersionUID = -1705495886974891511L;

            @Override
            public void onClick(final AjaxRequestTarget target) {
                DateTimeFieldBootstrapFormComponent.this.field.setModelObject(null);
                target.add(DateTimeFieldBootstrapFormComponent.this.field);
            }
        };
        border.add(clearDateLink);
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        if (isFloatedInput) {
            Attributes.addClass(tag, "floated-input");
        }
    }

    public Boolean getIsFloatedInput() {
        return isFloatedInput;
    }

    public void setIsFloatedInput(final Boolean isFloatedInput) {
        this.isFloatedInput = isFloatedInput;
    }
}
