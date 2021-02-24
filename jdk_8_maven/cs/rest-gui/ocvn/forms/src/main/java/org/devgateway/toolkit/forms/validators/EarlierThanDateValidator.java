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
package org.devgateway.toolkit.forms.validators;

import java.util.Date;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * Validator to test if the date is earlier than provided high date
 * 
 * @author idobre
 * @since 5/18/15
 */
public class EarlierThanDateValidator implements IValidator<Date> {

    private static final long serialVersionUID = 1L;

    private Date highDate;

    public EarlierThanDateValidator(final Date highDate) {
        this.highDate = highDate;
    }

    @Override
    public void validate(final IValidatable<Date> validatable) {
        if (highDate == null) {
            return;
        }

        if (validatable.getValue() != null && highDate.before(validatable.getValue())) {
            ValidationError error = new ValidationError(this);
            error.setVariable("highDate", highDate);
            validatable.error(error);
        }
    }
}
