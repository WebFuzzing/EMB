/**
 *
 */
package org.devgateway.ocvn.forms.wicket.components;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.IAjaxIndicatorAware;
import org.apache.wicket.extensions.ajax.markup.html.AjaxIndicatorAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

import java.io.Serializable;

/**
 * A special label to display log files interactively on the page, with nice
 * {@link IAjaxIndicatorAware} spinner
 *
 * @author mpostelnicu
 *
 */
public class LogLabel extends Label implements IAjaxIndicatorAware {

    private static final int REFRESH_INTERVAL = 10;

    private static final long serialVersionUID = 1L;

    private AjaxIndicatorAppender indicatorAppender = new AjaxIndicatorAppender();
    private Duration refreshInterval = Duration.seconds(REFRESH_INTERVAL);

    private AjaxSelfUpdatingTimerBehavior selfUpdatingBehavior;

    /**
     * @param id
     */
    public LogLabel(final String id) {
        super(id);
    }

    /**
     * @param id
     * @param label
     */
    public LogLabel(final String id, final String label) {
        super(id, label);
    }

    /**
     * @param id
     * @param label
     */
    public LogLabel(final String id, final Serializable label) {
        super(id, label);
    }

    /**
     * @param id
     * @param model
     */
    public LogLabel(final String id, final IModel<?> model) {
        super(id, model);

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.apache.wicket.ajax.IAjaxIndicatorAware#getAjaxIndicatorMarkupId()
     */
    @Override
    public String getAjaxIndicatorMarkupId() {
        return indicatorAppender.getMarkupId();
    }

    public LogLabel refreshInterval(final Duration duration) {
        refreshInterval = duration;
        return this;
    }

    @Override
    protected void onInitialize() {
        super.onInitialize();
        add(indicatorAppender);
        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);
        setEscapeModelStrings(false);

        selfUpdatingBehavior = new AjaxSelfUpdatingTimerBehavior(refreshInterval) {
            private static final long serialVersionUID = 1L;

            @Override
            protected void onPostProcessTarget(final AjaxRequestTarget target) {
                LogLabel.this.onPostProcessTarget(target);
            }
        };
        add(selfUpdatingBehavior);
    }

    protected void onPostProcessTarget(final AjaxRequestTarget target) {

    }

    public AjaxSelfUpdatingTimerBehavior getSelfUpdatingBehavior() {
        return selfUpdatingBehavior;
    }

}
