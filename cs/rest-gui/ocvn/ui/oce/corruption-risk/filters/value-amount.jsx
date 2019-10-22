import FilterBox from "./box";
import TenderPrice from "../../filters/tender-price";
import AwardValue from "../../filters/award-value";
import {Map} from "immutable";
import {fetchJson} from "../../tools";

class FlaggedTenderPrice extends TenderPrice{
  componentDidMount(){
    fetchJson('/api/tenderValueInterval?flagged=true')
      .then(([{minTenderValue, maxTenderValue}]) => this.setState({
        min: Math.floor(minTenderValue),
        max: Math.ceil(maxTenderValue)
      }))
  }
};

class FlaggedAwardValue extends AwardValue{
  componentDidMount(){
    fetchJson('/api/awardValueInterval?flagged=true')
      .then(([{minAwardValue, maxAwardValue}]) => this.setState({
        min: Math.floor(minAwardValue),
        max: Math.ceil(maxAwardValue)
      }))
  }
};

class ValueAmount extends FilterBox {
  isActive(){
    const {appliedFilters} = this.props;
    return appliedFilters.get('minTenderValue') ||
           appliedFilters.get('maxTenderValue') ||
           appliedFilters.get('minAwardValue') ||
           appliedFilters.get('maxAwardValue');
  }

  reset(){
    const {onApply, state} = this.props;
    onApply(state.delete('minTenderValue')
                 .delete('maxTenderValue')
                 .delete('minAwardValue')
                 .delete('maxAwardValue'));
  }

  getTitle() {
    return 'Value amount';
  }

  update(slug, {min, max}, {min: minPossibleValue, max: maxPossibleValue}) {
    const {state, onUpdate} = this.props;
    const minValue = state.get('min' + slug) || minPossibleValue;
    const maxValue = state.get('max' + slug) || maxPossibleValue;
    if(minValue != min) {
      onUpdate("min" + slug, min == minPossibleValue ? undefined : min)
    }else if(maxValue != max) {
      onUpdate("max" + slug, max == maxPossibleValue ? undefined : max)
    }
  }

  renderChild(slug, Component) {
    const {state, translations} = this.props;
    const minValue = state.get('min' + slug);
    const maxValue = state.get('max' + slug);
    return (
        <Component
            translations={translations}
            minValue={minValue}
            maxValue={maxValue}
            onUpdate={this.update.bind(this, slug)}
        />
    )
  }

  getBox() {
    return (
        <div className="box-content">
          {this.renderChild("TenderValue", FlaggedTenderPrice)}
          {this.renderChild("AwardValue", FlaggedAwardValue)}
        </div>
    )
  }
}

export default ValueAmount;
