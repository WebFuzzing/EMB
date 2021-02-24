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
package org.devgateway.toolkit.forms.validators;

import java.util.Calendar;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

/**
 * @author mpostelnicu Validates an Integer input to be a valid year Returns
 *         errors when this is not true
 */
public class YearValidator implements IValidator<Integer> {

    private static final long serialVersionUID = 1L;
    private boolean maxCurrentYear = false;

    public YearValidator maxCurrentYear() {
        this.maxCurrentYear = true;
        return this;
    }

    @Override
    public void validate(final IValidatable<Integer> validatable) {
        if (validatable.getValue() == null) {
            return;
        }

        if (maxCurrentYear && validatable.getValue() > Calendar.getInstance().get(Calendar.YEAR)) {
            ValidationError error = new ValidationError();
            error.addKey(this, "maxCurrentYear");
            validatable.error(error);
        }
    }

}
