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

import java.util.HashSet;
import java.util.Set;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.FormComponent;
import org.apache.wicket.model.IModel;

/**
 * @author mpostelnicu {@link GenericBootstrapFormComponent} that can show and
 *         hide other components
 */
public abstract class GenericEnablingBootstrapFormComponent<TYPE, FIELD extends FormComponent<TYPE>>
        extends GenericBootstrapFormComponent<TYPE, FIELD> {

    private static final long serialVersionUID = 1L;
    private Set<Component> visibilityBoundComponents = new HashSet<Component>();
    private Set<Component> visibilityReverseBoundComponents = new HashSet<Component>();

    /**
     * @param id
     */
    public GenericEnablingBootstrapFormComponent(final String id) {
        super(id);
    }

    /**
     * @param id
     * @param model
     */
    public GenericEnablingBootstrapFormComponent(final String id, final IModel<TYPE> model) {
        super(id, model);
    }

    /**
     * @param id
     * @param labelModel
     * @param model
     */
    public GenericEnablingBootstrapFormComponent(final String id, final IModel<String> labelModel,
            final IModel<TYPE> model) {
        super(id, labelModel, model);
    }

    /**
     * Returns true if the bound components should have
     * {@link #setVisibilityAllowed(boolean)} to true otherwise returns false.
     * 
     * @param selectedValue
     *            the selected value of the current component, that may be taken
     *            into consideration when evaluating the visibility of the bound
     *            components
     * @return
     */
    protected abstract boolean boundComponentsVisibilityAllowed(TYPE selectedValue);

    /**
     * change visibility status of bound components based of the fact the level
     * has an affirmative answer or not
     * 
     * @param target
     */
    protected void updateBoundComponents(final AjaxRequestTarget target) {
        TYPE selectedValue = this.getModelObject();
        if (selectedValue != null) {
            for (Component component : visibilityBoundComponents) {
                component.setVisibilityAllowed(boundComponentsVisibilityAllowed(selectedValue));
                if (target != null) {
                    target.add(component);
                }
            }
        }
    }

    /**
     * Reversed of {@link #updateBoundComponents(AjaxRequestTarget)}
     * 
     * @param target
     */
    protected void updateReverseBoundComponents(final AjaxRequestTarget target) {
        TYPE selectedValue = this.getModelObject();
        if (selectedValue != null) {
            for (Component component : visibilityReverseBoundComponents) {
                component.setVisibilityAllowed(!boundComponentsVisibilityAllowed(selectedValue));
                if (target != null) {
                    target.add(component);
                }
            }
        }
    }

    /**
     * Each time the component gets updated we also refresh the bound
     * components' visibility
     */
    @Override
    protected void onUpdate(final AjaxRequestTarget target) {
        super.onUpdate(target);
        updateBoundComponents(target);
        updateReverseBoundComponents(target);
    }

    /**
     * Re-apply the visibility states of all bound components, this is good
     * because {@link #onInitialize()} is invoked after the models are
     * initialized, so we know the value of the saved entity selection
     */
    @Override
    protected void onInitialize() {
        super.onInitialize();
        updateBoundComponents(null);
        updateReverseBoundComponents(null);
    }

    /**
     * Add component that is bound to the value of this current component,
     * changing the selected element of this component may influence if the
     * bound component is visible or not. By default all bound components are
     * not visible
     * 
     * @param c
     * @return
     */
    public GenericEnablingBootstrapFormComponent<TYPE, FIELD> addBoundComponent(final Component c) {
        TYPE selectedValue = this.getModelObject();
        c.setVisibilityAllowed(selectedValue == null ? false : boundComponentsVisibilityAllowed(selectedValue));
        visibilityBoundComponents.add(c);
        return this;
    }

    /**
     * This is the negated version of
     * {@link GenericEnablingBootstrapFormComponent#addBoundComponent(Component)}
     * 
     * @param c
     * @return
     */
    public GenericEnablingBootstrapFormComponent<TYPE, FIELD> addReverseBoundComponent(final Component c) {
        TYPE selectedValue = this.getModelObject();
        c.setVisibilityAllowed(selectedValue == null ? false : !boundComponentsVisibilityAllowed(selectedValue));
        visibilityReverseBoundComponents.add(c);
        return this;
    }

}
