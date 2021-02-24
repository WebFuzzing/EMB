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

import org.apache.log4j.Logger;
import org.apache.wicket.model.IModel;
import org.devgateway.toolkit.forms.WebConstants;
import org.devgateway.toolkit.persistence.dao.GenericPersistable;
import org.devgateway.toolkit.persistence.dao.Labelable;
import org.devgateway.toolkit.persistence.repository.category.TextSearchableRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.wicketstuff.select2.ChoiceProvider;
import org.wicketstuff.select2.Response;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author mpostelnicu
 *
 */
public abstract class AbstractJpaRepositoryTextChoiceProvider<T extends GenericPersistable & Labelable>
        extends ChoiceProvider<T> {

    private static final long serialVersionUID = 5709987900445896586L;

    protected static final Logger logger = Logger.getLogger(AbstractJpaRepositoryTextChoiceProvider.class);

    protected T newObject;

    protected Sort sort;

    protected Boolean addNewElements = false;

    private Class<T> clazz;

    protected IModel<Collection<T>> restrictedToItemsModel;

    protected TextSearchableRepository<T, Long> textSearchableRepository;

    public AbstractJpaRepositoryTextChoiceProvider(final TextSearchableRepository<T, Long> textSearchableRepository) {
        this.textSearchableRepository = textSearchableRepository;
    }

    public AbstractJpaRepositoryTextChoiceProvider(final TextSearchableRepository<T, Long> textSearchableRepository,
                                                   final Class<T> clazz, final Boolean addNewElements) {
        this.textSearchableRepository = textSearchableRepository;
        this.clazz = clazz;
        this.addNewElements = addNewElements;
    }

    public AbstractJpaRepositoryTextChoiceProvider(final TextSearchableRepository<T, Long> textSearchableRepository,
                                                   final IModel<Collection<T>> restrictedToItemsModel) {
        this(textSearchableRepository);
        this.restrictedToItemsModel = restrictedToItemsModel;
    }

    public TextSearchableRepository<T, Long> getTextSearchableRepository() {
        return textSearchableRepository;
    }

    @Override
    public String getIdValue(final T choice) {
        // if the object is not null but it hasn't an ID return 0
        if (choice != null && choice.getId() == null && addNewElements) {
            return "0";
        }

        return choice.getId().toString();
    }

    @Override
    public void query(final String term, final int page, final Response<T> response) {
        Page<T> itemsByTerm;
        if (term == null || term.isEmpty()) {
            itemsByTerm = findAll(page);
            response.setHasMore(itemsByTerm.hasNext());
        } else {
            itemsByTerm = getItemsByTerm(term.toLowerCase(), page);
        }

        if (itemsByTerm != null) {
            if (itemsByTerm.getContent().size() == 0 && addNewElements) {
                // add new element dynamically
                // the new element should extend Category so that we can attache
                // a 'label' to it
                try {
                    newObject = clazz.newInstance();
                    newObject.setLabel(term);
                } catch (InstantiationException e) {
                    logger.error(e);
                } catch (IllegalAccessException e) {
                    logger.error(e);
                }

                List<T> newElementsList = new ArrayList<>();
                newElementsList.add(newObject);

                response.addAll(newElementsList);
            } else {
                response.setHasMore(itemsByTerm.hasNext());
                response.addAll(itemsByTerm.getContent());
            }
        }
    }

    protected Page<T> getItemsByTerm(final String term, final int page) {
        PageRequest pageRequest = new PageRequest(page, WebConstants.SELECT_PAGE_SIZE, sort);
        return getTextSearchableRepository().searchText(term, pageRequest);
    }

    public Page<T> findAll(final int page) {
        PageRequest pageRequest = new PageRequest(page, WebConstants.SELECT_PAGE_SIZE, sort);
        return getTextSearchableRepository().findAll(pageRequest);
    }


    @Override
    public Collection<T> toChoices(final Collection<String> ids) {
        ArrayList<String> idsList = new ArrayList<>();

        for (String id : ids) {
            // create new element
            if (Long.parseLong(id) == 0 && addNewElements) {
                if (newObject != null && newObject.getId() == null) {
                    getTextSearchableRepository().save(newObject);
                }

                id = newObject.getId().toString();
            }

            idsList.add(id);
        }

        ArrayList<T> response = new ArrayList<>();
        for (String s : idsList) {
            Long id = Long.parseLong(s);
            T findOne = getTextSearchableRepository().findOne(id);
            if (findOne == null) {
                logger.error("Cannot find entity with id=" + id + " in repository "
                        + getTextSearchableRepository().getClass());
            } else {
                response.add(findOne);
            }
        }
        return response;
    }

}
