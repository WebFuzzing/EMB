import TypeAhead from './inputs/type-ahead.jsx';

class Supplier extends TypeAhead {
  getTitle() {
    return this.t('filters:supplier:title');
  }
}

Supplier.ENDPOINT = '/api/ocds/organization/supplier/all';

export default Supplier;
