import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class ContractMethod extends MultipleSelect {
  getTitle() {
    return this.t('filters:contractMethod:title');
  }

  getId(option) {
    return option.get('id');
  }

  getLabel(option) {
    return option.get('details');
  }
}

ContractMethod.ENDPOINT = '/api/ocds/contrMethod/all';

export default ContractMethod;
