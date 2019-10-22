import Organizations from "../../../oce/filters/tabs/organizations";
import PEGroup from "../pe-group";
import PEDepartment from "../pe-department";
import PECity from "../pe-city";

class OCVNOrganizations extends Organizations{}

OCVNOrganizations.FILTERS = Organizations.FILTERS.concat([
    ['procuringEntityGroupId', PEGroup],
    ['procuringEntityDepartmentId', PEDepartment],
    ['procuringEntityCityId', PECity]
]);

export default OCVNOrganizations;