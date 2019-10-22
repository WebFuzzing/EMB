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
package org.devgateway.toolkit.forms.models;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.IWrapModel;

/**
 * @author mpostelnicu
 *
 */
public class SubComponentWrapModel<T> implements IWrapModel<T> {

    private static final long serialVersionUID = 4054354057335519754L;
    private Component parent;

    public SubComponentWrapModel(final Component parent) {
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.apache.wicket.model.IWrapModel#getWrappedModel()
     */
    @Override
    public IModel<?> getWrappedModel() {
        return parent.getDefaultModel();
    }

    @SuppressWarnings("unchecked")
    @Override
    public T getObject() {
        return (T) parent.getDefaultModelObject();
    }

    @Override
    public void setObject(final T object) {
        parent.setDefaultModelObject(object);
    }

    @Override
    public void detach() {
        IModel<?> wrappedModel = getWrappedModel();
        if (wrappedModel != null) {
            wrappedModel.detach();
        }
    }

}
