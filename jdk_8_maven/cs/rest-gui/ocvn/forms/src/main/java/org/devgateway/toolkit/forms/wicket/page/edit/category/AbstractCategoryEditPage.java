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
package org.devgateway.toolkit.forms.wicket.page.edit.category;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.devgateway.toolkit.forms.wicket.components.form.TextFieldBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.page.edit.AbstractEditPage;
import org.devgateway.toolkit.persistence.dao.categories.Category;

/**
 * @author mpostelnicu
 */

public abstract class AbstractCategoryEditPage<T extends Category> extends AbstractEditPage<T> {
    private static final long serialVersionUID = 6571076983713857766L;
    protected TextFieldBootstrapFormComponent<String> label;

    public AbstractCategoryEditPage(final PageParameters parameters) {
        super(parameters);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        label = new TextFieldBootstrapFormComponent<>("label");
        label.required();
        editForm.add(label);

    }
}
