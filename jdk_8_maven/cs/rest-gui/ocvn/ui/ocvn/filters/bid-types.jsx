import MultipleSelect from '../../oce/filters/inputs/multiple-select';

class BidTypes extends MultipleSelect {
  getTitle() {
    return this.t('filters:bidArea:title');
  }

  getOptions() {
    return this.props.options;
  }

  getId(_, id) {
    return id;
  }

  getLabel(label) {
    return label;
  }
}

export default BidTypes;
