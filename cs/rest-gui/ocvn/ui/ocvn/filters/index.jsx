import OCEFilters from "../../oce/filters";
import OCVNOrganizations from "./tabs/organizations";
import ProcurementTypes from "./tabs/procurement-types";
import Locations from "./tabs/locations";
import Amounts from "../../oce/filters/tabs/amounts.jsx";

class OCVNFilters extends OCEFilters{
}

OCVNFilters.TABS=[OCVNOrganizations, ProcurementTypes, Locations, Amounts];

export default OCVNFilters;
