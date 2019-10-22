package org.devgateway.toolkit.forms.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.TransparentWebMarkupContainer;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.devgateway.toolkit.forms.wicket.components.form.BootstrapAddButton;
import org.devgateway.toolkit.forms.wicket.components.form.BootstrapDeleteButton;
import org.devgateway.toolkit.persistence.dao.AbstractAuditableEntity;

import java.util.List;

/**
 * @author idobre
 * @since 10/5/16
 *
 * Class that displays a list of <T> type with the possibility of adding/removing elements.
 *
 * @param <T>      The current list data type
 * @param <PARENT> The parent field data type
 */

public abstract class ListViewSectionPanel<T extends AbstractAuditableEntity, PARENT extends AbstractAuditableEntity>
        extends CompoundSectionPanel<List<T>> {
    private WebMarkupContainer listWrapper;

    protected ListView<T> listView;

    public ListViewSectionPanel(final String id) {
        super(id);
    }

    /**
     * Removes a child based on its index
     *
     * @param index
     * @return
     */
    private BootstrapDeleteButton getRemoveChildButton(final int index) {
        BootstrapDeleteButton removeButton = new BootstrapDeleteButton("remove") {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                ListViewSectionPanel.this.getModelObject().remove(index);
                listView.removeAll();
                target.add(listWrapper);
            }
        };

        removeButton.setOutputMarkupPlaceholderTag(true);
        return removeButton;
    }

    /**
     * Returns the new child button
     *
     * @return
     */
    protected BootstrapAddButton getAddNewChildButton() {
        BootstrapAddButton newButton = new BootstrapAddButton("newButton", new ResourceModel("newButton")) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onSubmit(final AjaxRequestTarget target, final Form<?> form) {
                @SuppressWarnings("unchecked")
                T newChild = createNewChild((IModel<PARENT>) ListViewSectionPanel.this.getParent().getDefaultModel());
                ListViewSectionPanel.this.getModel().getObject().add(newChild);

                listView.removeAll();
                target.add(listWrapper);
            }

        };

        newButton.setOutputMarkupPlaceholderTag(true);
        return newButton;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        listWrapper = new TransparentWebMarkupContainer("listWrapper");
        listWrapper.setOutputMarkupId(true);
        add(listWrapper);

        listWrapper.add(new Label("panelTitle", title));

        listView = new ListView<T>("list", getModel()) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void populateItem(final ListItem<T> item) {
                // we wrap the item model on a compound model so we can use the field ids as property models
                final CompoundPropertyModel<T> compoundPropertyModel = new CompoundPropertyModel<>(item.getModel());

                // we set back the model as the compound model, thus ensures the rest of the items added will benefit
                item.setModel(compoundPropertyModel);

                // we add the rest of the items in the listItem
                populateCompoundListItem(item);

                // we add the remove button
                final BootstrapDeleteButton removeButton = getRemoveChildButton(item.getIndex());
                item.add(removeButton);
            }
        };

        listView.setReuseItems(true);
        listView.setOutputMarkupId(true);
        listWrapper.add(listView);

        final BootstrapAddButton addButton = getAddNewChildButton();
        add(addButton);
    }

    /**
     * Use the constructor for new children and return the entity after setting
     * its parent
     *
     * @param parentModel the model of the parent
     * @return
     */
    public abstract T createNewChild(IModel<PARENT> parentModel);

    /**
     * Populates the list item elements
     *
     * @param item
     */
    public abstract void populateCompoundListItem(ListItem<T> item);
}
