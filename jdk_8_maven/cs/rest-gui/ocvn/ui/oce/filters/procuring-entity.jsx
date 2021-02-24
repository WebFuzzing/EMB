import TypeAhead from './inputs/type-ahead';

class ProcuringEntity extends TypeAhead {
  getTitle() {
    return this.t('filters:procuringEntity:title');
  }
}

ProcuringEntity.ENDPOINT = '/api/ocds/organization/procuringEntity/all';

export default ProcuringEntity;
