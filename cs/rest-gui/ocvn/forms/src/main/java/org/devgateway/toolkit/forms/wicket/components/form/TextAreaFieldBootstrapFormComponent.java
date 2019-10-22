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

import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.devgateway.toolkit.forms.WebConstants;

/**
 * @author mpostelnicu
 * 
 */
public class TextAreaFieldBootstrapFormComponent<TYPE> extends GenericBootstrapFormComponent<TYPE, TextArea<TYPE>> {
    private StringValidator validator = WebConstants.StringValidators.MAXIMUM_LENGTH_VALIDATOR_ONE_LINE_TEXTAREA;

    /**
     * 
     */
    private static final long serialVersionUID = -7822733988194369835L;

    public TextAreaFieldBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<TYPE> model) {
        super(id, labelModel, model);
    }

    public TextAreaFieldBootstrapFormComponent(final String id, final IModel<String> labelModel) {
        super(id, labelModel, null);
    }

    /**
     * @param id
     */
    public TextAreaFieldBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected TextArea<TYPE> inputField(final String id, final IModel<TYPE> model) {
        TextArea<TYPE> textArea = new TextArea<TYPE>(id, initFieldModel());
        return textArea;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getField().add(validator);
    }
}
