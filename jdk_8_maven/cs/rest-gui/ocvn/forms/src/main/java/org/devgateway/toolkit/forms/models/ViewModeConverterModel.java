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
package org.devgateway.toolkit.forms.models;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.forms.wicket.components.form.GenericBootstrapFormComponent;

/**
 * @author mpostelnicu Converter for {@link GenericBootstrapFormComponent}
 *         viewModeField This will be used when
 *         {@link WebConstants#PARAM_VIEW_MODE} is true in the browser and will
 *         convert the model object to something printable (string-like)
 */
public class ViewModeConverterModel<T> extends AbstractReadOnlyModel<String> {

    private SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private IModel<T> originalModel;

    /**
     * 
     */
    public ViewModeConverterModel(final IModel<T> originalModel) {
        this.originalModel = originalModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.model.IModel#getObject()
     */
    @Override
    public String getObject() {
        T object = originalModel.getObject();
        if (object == null) {
            return "";
        }

        // for booleans we return yes/no
        if (object instanceof Boolean) {
            return ((Boolean) object).booleanValue() ? "Yes" : "No";
        }

        // for collections that are empty, we return empty
        if (object instanceof Collection<?>) {
            if (((Collection<?>) object).size() == 0) {
                return "";
            }
        }

        // convert date to a nicer format
        if (object instanceof Date) {
            return sdf.format((Date) object);
        }

        // alas just return the string value of the object
        return object.toString();
    }

}
