import Tab from "./index";
import TenderPrice from "../tender-price";
import AwardValue from "../award-value";

class Amount extends Tab{
    renderChild(slug, Component){
        const {state, onUpdate, translations} = this.props;
        const minValue = state.get('min'+slug);
        const maxValue = state.get('max'+slug);
        return <Component
          translations={translations}
          minValue={minValue}
          maxValue={maxValue}
          onUpdate={({min, max}) => {
              minValue != min && onUpdate("min"+slug, min);
              maxValue != max && onUpdate("max"+slug, max);
          }}
        />
    }
    
    render(){
      return <div>
        {this.renderChild("TenderValue", TenderPrice)}
        {this.renderChild("AwardValue", AwardValue)}
      </div>
  }
}

Amount.getName = t => t('filters:tabs:amounts:title');

export default Amount;
