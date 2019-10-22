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
package org.devgateway.toolkit.forms.wicket.components;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;

/**
 * @author idobre
 * @since 4/6/15
 *
 *        FieldPanel that displays only a Label - is used to display a simple
 *        title in the Header of the ListSectionPanel
 */
public class TitleLabelField<T> extends FieldPanel<T> {

    private static final long serialVersionUID = 1L;
    private Label title;

    public TitleLabelField(final String id) {
        this(id, null);
    }

    public TitleLabelField(final String id, final IModel<T> model) {
        super(id);

        title = new Label("title", model);
        add(title);
    }

    public Label getTitle() {
        return title;
    }

    public void setTitle(final Label title) {
        this.title = title;
    }
}
