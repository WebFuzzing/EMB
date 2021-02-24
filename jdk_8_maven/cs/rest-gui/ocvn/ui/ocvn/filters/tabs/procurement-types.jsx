import Tab from "../../../oce/filters/tabs";
import BidTypes from "../bid-types";
import {Set} from "immutable";
import BidSelectionMethod from "../bid-selection-method";
import ContractMethod from "../contract-method";

class ProcurementTypes extends Tab{
  render(){
    let {state, onUpdate, bidTypes, translations} = this.props;
    let selectedBidTypesIds = state.get('bidTypeId', Set());
    return <div>
      <BidTypes
          options={bidTypes}
          selected={selectedBidTypesIds}
          onToggle={id => onUpdate('bidTypeId', selectedBidTypesIds.has(id) ?
              selectedBidTypesIds.delete(id) :
              selectedBidTypesIds.add(id))
          }
          onUpdateAll={onUpdate.bind(null, 'bidTypeId')}
          translations={translations}
      />

      {this.renderChild(BidSelectionMethod, 'bidSelectionMethod')}
      {this.renderChild(ContractMethod, 'contrMethod')}
    </div>
  }
}

ProcurementTypes.getName = t => t('filters:tabs:procurementTypes:title');

export default ProcurementTypes;