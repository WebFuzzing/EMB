import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class PEDepartment extends MultipleSelect {
  getTitle() { return this.t('filters:peDepartment:title'); }

  getId(option) { return option.get('id'); }

  getLabel(option) { return option.get('name'); }
}

PEDepartment.ENDPOINT = '/api/ocds/orgDepartment/all';

export default PEDepartment;
