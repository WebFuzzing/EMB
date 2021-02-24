import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class PEGroup extends MultipleSelect {
  getTitle() { return this.t('filters:peGroup:title'); }

  getId(option) { return option.get('id'); }

  getLabel(option) { return option.get('name'); }
}

PEGroup.ENDPOINT = '/api/ocds/orgGroup/all';

export default PEGroup;
