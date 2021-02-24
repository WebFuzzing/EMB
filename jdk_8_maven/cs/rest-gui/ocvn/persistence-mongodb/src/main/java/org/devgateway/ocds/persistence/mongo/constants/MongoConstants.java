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
package org.devgateway.ocds.persistence.mongo.constants;

public final class MongoConstants {

    private MongoConstants() {
    }

    public static final String OCDS_PREFIX = "ocds-ep75k8-";

    public static final int IMPORT_ROW_BATCH = 1000;

    public static final class FieldNames {
        public static final String TENDER_PERIOD_START_DATE = "tender.tenderPeriod.startDate";
        public static final String TENDER_PERIOD_END_DATE = "tender.tenderPeriod.endDate";
        public static final String TENDER_PERIOD_START_DATE_REF = "$" + TENDER_PERIOD_START_DATE;
        public static final String TENDER_PERIOD_END_DATE_REF = "$" + TENDER_PERIOD_END_DATE;
    }

    public static final int MINIMUM_MONGO_YEAR = 2010;

    public static final int MAXIMUM_MONGO_YEAR = 2020;

    public static final String DEFAULT_IMPORT_TIMEZONE = "America/New_York";

    public static final int DAY_MS = 86400000;
}
