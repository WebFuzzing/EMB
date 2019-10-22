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
package org.devgateway.toolkit.forms.wicket.components.form;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;

/**
 * @author idobre
 * @since 5/6/15
 */
public class TwitterFieldBootstrapFormComponent extends TextFieldBootstrapFormComponent<String> {
    private static final long serialVersionUID = 1L;

    public TwitterFieldBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        // add a '@' if it's not present yet
        String twitter = this.getModelObject();
        if (twitter != null && !twitter.startsWith("@")) {
            twitter = "@" + twitter;
            this.setModelObject(twitter);
        }
    }

    @Override
    protected TextField<String> inputField(final String id, final IModel<String> model) {
        return new TextField<String>(id, initFieldModel()) {

            private static final long serialVersionUID = 1L;

            @Override
            public void convertInput() {
                super.convertInput();

                this.updateModel();

                // add a '@' in front of the field value
                String twitter = this.getModelObject();
                if (twitter != null && !twitter.startsWith("@")) {
                    twitter = "@" + twitter;
                }

                setConvertedInput(twitter);
            }
        };
    }
}
