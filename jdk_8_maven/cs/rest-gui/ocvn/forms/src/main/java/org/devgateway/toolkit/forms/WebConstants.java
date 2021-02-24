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
package org.devgateway.toolkit.forms;

import org.apache.wicket.validation.validator.StringValidator;
import org.devgateway.toolkit.persistence.dao.DBConstants;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public final class WebConstants {

    private WebConstants() {

    }

    public static final int PAGE_SIZE = 10;
    public static final int SELECT_PAGE_SIZE = 25;

    public static final String PARAM_VIEW_MODE = "viewMode";

    public static final String PARAM_ID = "id";
    public static final String PARAM_REVISION_ID = "revisionId";
    public static final String PARAM_ENTITY_CLASS = "class";

    public static final String LANGUAGE_PARAM = "lang";

    public static final class StringValidators {
        public static final StringValidator MAXIMUM_LENGTH_VALIDATOR_ONE_LINE_TEXT =
                StringValidator.maximumLength(DBConstants.MAX_DEFAULT_TEXT_LENGTH_ONE_LINE);
        public static final StringValidator MAXIMUM_LENGTH_VALIDATOR_ONE_LINE_TEXTAREA =
                StringValidator.maximumLength(DBConstants.MAX_DEFAULT_TEXT_AREA);
    }

    // add more languages here. It is pointless to make this dynamic because the
    // wicket i18n is in .properties files so we need
    // to change the src code anyway.
    public static final List<Locale> AVAILABLE_LOCALES = Collections.unmodifiableList(Arrays.asList(new Locale("en")));

}
