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
package org.devgateway.ocds.forms.wicket.providers;

import java.util.Collection;

import org.apache.wicket.model.IModel;
import org.devgateway.toolkit.forms.wicket.providers.AbstractJpaRepositoryTextChoiceProvider;
import org.devgateway.toolkit.persistence.dao.GenericPersistable;
import org.devgateway.toolkit.persistence.dao.Labelable;
import org.devgateway.toolkit.persistence.repository.category.TextSearchableRepository;

/**
 * @author mpostelnicu
 *
 */
public class LabelPersistableJpaRepositoryTextChoiceProvider<T extends GenericPersistable & Labelable>
        extends AbstractJpaRepositoryTextChoiceProvider<T> {

    /**
     * 
     */
    private static final long serialVersionUID = -9109118476966448737L;

    public LabelPersistableJpaRepositoryTextChoiceProvider(
            final TextSearchableRepository<T, Long> textSearchableRepository) {
        super(textSearchableRepository);
    }

    public LabelPersistableJpaRepositoryTextChoiceProvider(
            final TextSearchableRepository<T, Long> textSearchableRepository,
            final IModel<Collection<T>> restrictedToItemsModel) {
        super(textSearchableRepository, restrictedToItemsModel);
    }

    public LabelPersistableJpaRepositoryTextChoiceProvider(
            final TextSearchableRepository<T, Long> textSearchableRepository, final Class<T> clazz,
            final Boolean addNewElements) {
        super(textSearchableRepository, clazz, addNewElements);
    }

    @Override
    public String getDisplayValue(final T choice) {
        return choice.getLabel();
    }
}
