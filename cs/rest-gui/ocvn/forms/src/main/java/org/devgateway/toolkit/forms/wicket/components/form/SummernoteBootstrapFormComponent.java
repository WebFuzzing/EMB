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

import org.apache.wicket.model.IModel;
import org.apache.wicket.validation.validator.StringValidator;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.forms.wicket.FormsWebApplication;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.editor.SummernoteConfig;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.editor.SummernoteEditor;

/**
 * @author mpostelnicu
 * 
 */
public class SummernoteBootstrapFormComponent extends GenericBootstrapFormComponent<String, SummernoteEditor> {
    private static final int SUMMERNOTE_HEIGHT = 50;

    private StringValidator validator = WebConstants.StringValidators.MAXIMUM_LENGTH_VALIDATOR_ONE_LINE_TEXTAREA;

    private SummernoteConfig config;

    /**
     * 
     */
    private static final long serialVersionUID = -7822733988194369835L;

    public SummernoteBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<String> model) {
        super(id, labelModel, model);
    }

    public SummernoteBootstrapFormComponent(final String id, final IModel<String> labelModel) {
        super(id, labelModel, null);
    }

    /**
     * @param id
     */
    public SummernoteBootstrapFormComponent(final String id) {
        super(id);
    }

    @Override
    protected SummernoteEditor inputField(final String id, final IModel<String> model) {

        config = new SummernoteConfig();

        // this enabled for demo purposes, but it stores the files in volatile
        // disk dir
        config.useStorageId(FormsWebApplication.STORAGE_ID);

        config.withHeight(SUMMERNOTE_HEIGHT);
        config.withAirMode(false);

        SummernoteEditor summernoteEditor = new SummernoteEditor(id, initFieldModel(), config);

        return summernoteEditor;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        getField().add(validator);
    }

    public SummernoteConfig getConfig() {
        return config;
    }
}
