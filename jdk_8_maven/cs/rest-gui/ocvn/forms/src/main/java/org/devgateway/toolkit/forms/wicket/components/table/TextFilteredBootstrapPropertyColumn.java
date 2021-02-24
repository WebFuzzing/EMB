package org.devgateway.toolkit.forms.wicket.components.table;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.FilterForm;
import org.apache.wicket.extensions.markup.html.repeater.data.table.filter.TextFilteredPropertyColumn;
import org.apache.wicket.model.IModel;
import org.devgateway.toolkit.forms.wicket.components.form.TextFieldBootstrapFormComponent;

/**
 * A TextFilteredPropertyColumn that uses TextFieldBootstrapFormComponent as a
 * filter.
 *
 * Created by octavian on 15.04.2016.
 */
public class TextFilteredBootstrapPropertyColumn<T, F, S> extends TextFilteredPropertyColumn<T, F, S> {

    private static final long serialVersionUID = 7360465890668796668L;

    public TextFilteredBootstrapPropertyColumn(final IModel<String> displayModel, final S sortProperty,
            final String propertyExpression) {
        super(displayModel, sortProperty, propertyExpression);
    }

    public TextFilteredBootstrapPropertyColumn(final IModel<String> displayModel, final String propertyExpression) {
        super(displayModel, propertyExpression);
    }

    @Override
    public Component getFilter(final String componentId, final FilterForm<?> form) {
        final TextFieldBootstrapFormComponent<F> textField =
                new TextFieldBootstrapFormComponent<>(componentId, getFilterModel(form));
        textField.hideLabel();
        return textField;
    }
}
