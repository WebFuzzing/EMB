import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class Locations extends MultipleSelect {
  getTitle() {
    return this.t('filters:locations:title');
  }

  getId(option) {
    return option.get('id');
  }

  getLabel(option) {
    return option.get('description');
  }
}

Locations.ENDPOINT = '/api/ocds/location/all';

export default Locations;
