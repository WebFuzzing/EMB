import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class PECity extends MultipleSelect {
  getTitle() { return this.t('filters:peCity:title'); }

  getId(option) { return option.get('id'); }

  getLabel(option) { return option.get('name'); }
}

PECity.ENDPOINT = '/api/ocds/city/all';

export default PECity;
