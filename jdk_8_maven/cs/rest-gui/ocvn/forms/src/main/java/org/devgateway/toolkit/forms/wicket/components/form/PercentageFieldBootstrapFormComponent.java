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

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.IConverter;
import org.apache.wicket.util.convert.converter.BigDecimalConverter;
import org.apache.wicket.validation.validator.RangeValidator;

/**
 * @author mpostelnicu Field for showing percentages
 */
public class PercentageFieldBootstrapFormComponent extends TextFieldBootstrapFormComponent<BigDecimal> {
    private static final int PERCENT_MAX = 100;

    private static final long serialVersionUID = 1L;

    private static final BigDecimalConverter PERCENTAGE_CONVERTER = new BigDecimalConverter(); // {

    private Label label;

    public PercentageFieldBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<BigDecimal> model) {
        super(id, labelModel, model);
    }

    public PercentageFieldBootstrapFormComponent(final String id, final IModel<BigDecimal> model) {
        super(id, model);
    }

    /**
     * @param id
     */
    public PercentageFieldBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected TextField<BigDecimal> inputField(final String id, final IModel<BigDecimal> model) {
        return new TextField<BigDecimal>(id, initFieldModel()) {
            private static final long serialVersionUID = 1L;

            /*
             * (non-Javadoc)
             * 
             * @see org.apache.wicket.Component#getConverter(java.lang.Class)
             */
            @SuppressWarnings("unchecked")
            @Override
            public <C> IConverter<C> getConverter(final Class<C> type) {
                return (IConverter<C>) PERCENTAGE_CONVERTER;
            }
        };
    }

    @Override
    protected void onInitialize() {
        decimal();
        getField().add(new RangeValidator<>(BigDecimal.ZERO, BigDecimal.valueOf(PERCENT_MAX)));
        super.onInitialize();

        label = new Label("label", "%");
        label.add(new AttributeAppender("for", new Model<>(getField().getMarkupId())));
        border.add(label);
    }

}
