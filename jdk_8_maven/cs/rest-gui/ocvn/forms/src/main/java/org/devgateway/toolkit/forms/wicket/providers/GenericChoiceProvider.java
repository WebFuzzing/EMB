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
package org.devgateway.toolkit.forms.wicket.providers;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author idobre
 * @since 1/27/15
 *
 *        This is a ChoiceProvider for "non-persistable" unique list of elements
 */
public class GenericChoiceProvider<T> extends ChoiceProvider<T> {
    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(GenericChoiceProvider.class);

    /**
     * all elements are identified by and id which in this case is the String
     * representation of the T object (see @toJson function) so we retain the
     * elements as a tuple <id, T> so we can easily have access to the original
     * T object in the @toChoices function
     */
    private Map<String, T> bagOfElements;

    public GenericChoiceProvider(final List<T> listOfElements) {
        bagOfElements = new LinkedHashMap<>();

        // see the description of 'listOfElements' variable
        for (T el : listOfElements) {
            bagOfElements.put(el.toString(), el);
        }
    }

    @Override
    public String getDisplayValue(final T object) {
        return object.toString();
    }

    @Override
    public String getIdValue(final T object) {
        return object.toString();
    }

    @Override
    public void query(final String term, final int page, final Response<T> response) {
        final List<T> ret = new ArrayList<>();

        if (bagOfElements != null && bagOfElements.values() != null && bagOfElements.values().size() > 0) {
            for (final T el : bagOfElements.values()) {
                // the elements should implement the method toString in order to filter them
                if (StringUtils.isBlank(term) || el.toString().toLowerCase().contains(term.toLowerCase())) {
                    ret.add(el);
                }
            }
        }
        response.setHasMore(false);
        response.addAll(ret);
    }

    @Override
    public Collection<T> toChoices(final Collection<String> ids) {
        final List<T> ret = new ArrayList<>();

        if (ids != null) {
            for (final String id : ids) {
                try {
                    // just get the element from the map
                    ret.add(bagOfElements.get(id));
                } catch (final NumberFormatException e) {
                    LOGGER.error(e.getMessage());
                }
            }
        }
        return ret;
    }
}
