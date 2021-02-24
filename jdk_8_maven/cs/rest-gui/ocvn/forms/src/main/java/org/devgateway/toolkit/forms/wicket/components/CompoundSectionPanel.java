package org.devgateway.toolkit.forms.wicket.components;

import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.CompoundPropertyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;

/**
 * @author mpostelnicu Forced {@link CompoundPropertyModel} model over
 *         {@link GenericPanel} to ease sub-field referencing
 * @see CompoundSectionPanel#onInitialize()
 */
public class CompoundSectionPanel<T> extends GenericPanel<T> {

    private static final long serialVersionUID = 1L;

    protected IModel<String> title;

    /**
     * @param id
     */
    public CompoundSectionPanel(final String id) {
        this(id, null);
    }

    /**
     * @param id
     * @param model
     */
    public CompoundSectionPanel(final String id, final IModel<T> model) {
        this(id, model, new ResourceModel(id + ".label"));
    }

    /**
     * @param id
     * @param model
     * @param title
     */
    public CompoundSectionPanel(final String id, final IModel<T> model, final IModel<String> title) {
        super(id, model);

        // create a title in case we need it
        this.title = title;
    }

    /**
     * The default {@link CompoundPropertyModel}, for this {@link CompoundSectionPanel}
     * This can be overriden to provide for example, custom binding
     * @see CompoundPropertyModel#bind(String)
     * @return
     */
    public IModel<T> getCompoundSectionModel() {
        return new CompoundPropertyModel<>(getModel());
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();

        setOutputMarkupPlaceholderTag(true);
        setOutputMarkupId(true);

        // we wrap the self model into a CompoundModel to ease field referencing
        setModel(getCompoundSectionModel());
    }
}
