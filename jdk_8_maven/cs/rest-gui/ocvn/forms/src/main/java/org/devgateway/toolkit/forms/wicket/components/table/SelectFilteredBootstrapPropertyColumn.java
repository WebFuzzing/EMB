package org.devgateway.toolkit.forms.wicket.components.table;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.ChoiceFilteredPropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.model.IModel;
import org.devgateway.toolkit.forms.wicket.components.form.Select2ChoiceBootstrapFormComponent;
import org.devgateway.toolkit.forms.wicket.providers.GenericChoiceProvider;

import java.util.List;

/**
 * A ChoiceFilteredPropertyColumn that uses Select2ChoiceBootstrapFormComponent as a filter.
 *
 * @author idobre
 * @since 12/20/16
 */
public class SelectFilteredBootstrapPropertyColumn<T, Y, S> extends ChoiceFilteredPropertyColumn<T, Y, S> {

    public SelectFilteredBootstrapPropertyColumn(final IModel<String> displayModel,
                                                 final S sortProperty,
                                                 final String propertyExpression,
                                                 final IModel<? extends List<? extends Y>> filterChoices) {
        super(displayModel, sortProperty, propertyExpression, filterChoices);
    }

    public SelectFilteredBootstrapPropertyColumn(final IModel<String> displayModel,
                                                 final String propertyExpression,
                                                 final IModel<? extends List<? extends Y>> filterChoices) {
        super(displayModel, propertyExpression, filterChoices);
    }

    @Override
    public Component getFilter(final String componentId, final FilterForm<?> form) {
        final Select2ChoiceBootstrapFormComponent<Y> selectorField =
                new Select2ChoiceBootstrapFormComponent<>(componentId,
                        new GenericChoiceProvider<>((List<Y>) getFilterChoices().getObject()),
                        getFilterModel(form));
        selectorField.hideLabel();
        selectorField.getField().add(AttributeModifier.replace("onchange", "this.form.submit();"));
        return selectorField;
    }
}
