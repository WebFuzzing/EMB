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

import java.util.Collection;

import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Select2BootstrapTheme;
import org.wicketstuff.select2.Select2MultiChoice;

import de.agilecoders.wicket.core.util.Attributes;

/**
 * @author mpostelnicu
 * 
 */
public class Select2MultiChoiceBootstrapFormComponent<TYPE>
        extends GenericBootstrapFormComponent<Collection<TYPE>, Select2MultiChoice<TYPE>> {
    private static final long serialVersionUID = 7177558191815237814L;

    private Boolean isFloatedInput = false;

    public Select2MultiChoiceBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<Collection<TYPE>> model, final ChoiceProvider<TYPE> choiceProvider) {
        super(id, labelModel, model);
        provider(choiceProvider);
    }

    public Select2MultiChoiceBootstrapFormComponent<TYPE> provider(final ChoiceProvider<TYPE> choiceProvider) {
        field.setProvider(choiceProvider);
        return this;
    }

    public Select2MultiChoiceBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final ChoiceProvider<TYPE> choiceProvider) {
        super(id, labelModel, null);
        provider(choiceProvider);
    }

    public Select2MultiChoiceBootstrapFormComponent(final String id, final ChoiceProvider<TYPE> choiceProvider,
            final IModel<Collection<TYPE>> model) {
        super(id, model);
        provider(choiceProvider);
    }

    public Select2MultiChoiceBootstrapFormComponent(final String id, final ChoiceProvider<TYPE> choiceProvider) {
        super(id);
        provider(choiceProvider);
    }

    @Override
    protected Select2MultiChoice<TYPE> inputField(final String id, final IModel<Collection<TYPE>> model) {
        Select2MultiChoice<TYPE> multiChoice = new Select2MultiChoice<TYPE>(id, initFieldModel());
        multiChoice.setEscapeModelStrings(false);
        return multiChoice;
    }

    @Override
    public String getUpdateEvent() {
        return "change";
    }

    @Override
    protected void onInitialize() {
        field.getSettings().setPlaceholder("Click to select");
        field.getSettings().setAllowClear(true);
        field.getSettings().setCloseOnSelect(true);
        field.getSettings().setDropdownAutoWidth(true);
        field.getSettings().setTheme(new Select2BootstrapTheme(false));
        field.getSettings().setEscapeMarkup("function (m) {return m;}");
        super.onInitialize();

    }

    @Override
    protected void onComponentTag(final ComponentTag tag) {
        super.onComponentTag(tag);

        if (isFloatedInput) {
            Attributes.addClass(tag, "floated-input");
        }
    }

    public Boolean getIsFloatedInput() {
        return isFloatedInput;
    }

    public void setIsFloatedInput(final Boolean isFloatedInput) {
        this.isFloatedInput = isFloatedInput;
    }
}
