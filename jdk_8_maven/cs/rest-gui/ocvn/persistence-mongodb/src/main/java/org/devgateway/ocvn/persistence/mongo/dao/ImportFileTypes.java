package org.devgateway.ocvn.persistence.mongo.dao;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author mpostelnicu Specific data types found in Vietnam data mapping files used
 *         for data import into OCDS
 */
public final class ImportFileTypes {

    private ImportFileTypes() {

    }

    public static final String LOCATIONS = "Locations";

    public static final String PUBLIC_INSTITUTIONS = "Public Institutions";

    public static final String SUPPLIERS = "Suppliers";

    public static final String PROCUREMENT_PLANS = "Procurement Plans";

    public static final String BID_PLANS = "Bid Plans";

    public static final String TENDERS = "Tenders";

    public static final String EBID_AWARDS = "eBid Awards";

    public static final String OFFLINE_AWARDS = "Offline Awards";

    public static final String CITIES = "Cities";
    
    public static final String ORG_DEPARTMENTS = "Organization Departments";
    
    public static final String ORG_GROUPS = "Organization Groups";
    
    public static final List<String> ALL_FILE_TYPES = Collections.unmodifiableList(Arrays.asList(LOCATIONS,
            PUBLIC_INSTITUTIONS, SUPPLIERS, PROCUREMENT_PLANS, BID_PLANS, TENDERS, EBID_AWARDS, OFFLINE_AWARDS,
            CITIES, ORG_DEPARTMENTS, ORG_GROUPS
            ));

}
