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

import java.math.BigDecimal;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.devgateway.toolkit.forms.WebConstants;

import de.agilecoders.wicket.core.util.Attributes;

/**
 * @author mpostelnicu
 * 
 */
public class TextFieldBootstrapFormComponent<TYPE> extends GenericBootstrapFormComponent<TYPE, TextField<TYPE>> {
    private static final long serialVersionUID = 8062663141536130313L;
    private StringValidator validator = WebConstants.StringValidators.MAXIMUM_LENGTH_VALIDATOR_ONE_LINE_TEXT;
    private Boolean isFloatedInput = false;

    public TextFieldBootstrapFormComponent(final String id, final IModel<String> labelModel, final IModel<TYPE> model) {
        super(id, labelModel, model);
    }

    public TextFieldBootstrapFormComponent(final String id, final IModel<TYPE> model) {
        super(id, model);
    }

    /**
     * @param id
     */
    public TextFieldBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected TextField<TYPE> inputField(final String id, final IModel<TYPE> model) {
        return new TextField<TYPE>(id, initFieldModel());
    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        if (getIsFloatedInput()) {
            Attributes.addClass(tag, "floated-input");
        }
    }

    public TextFieldBootstrapFormComponent<TYPE> integer() {
        field.setType(Integer.class);
        return this;
    }

    public TextFieldBootstrapFormComponent<TYPE> decimal() {
        field.setType(BigDecimal.class);
        return this;
    }

    public TextFieldBootstrapFormComponent<TYPE> asDouble() {
        field.setType(Double.class);
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        if (getField().getType() == null || !Number.class.isAssignableFrom(getField().getType())) {
            getField().add(validator);
        }
    }

    public Boolean getIsFloatedInput() {
        return isFloatedInput;
    }

    public void setIsFloatedInput(final Boolean isFloatedInput) {
        this.isFloatedInput = isFloatedInput;
    }

}
