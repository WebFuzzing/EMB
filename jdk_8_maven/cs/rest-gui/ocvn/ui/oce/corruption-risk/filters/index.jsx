import cn from "classnames";
import {Set, Map} from "immutable";
import Organizations from "./organizations";
import ProcurementMethodBox from "./procurement-method";
import ValueAmount from "./value-amount";
import DateBox from "./date";
import {fetchJson, range, pluck} from "../../tools";

class Filters extends React.Component{
  render(){
    const {onUpdate, translations, currentBoxIndex, requestNewBox, state, allYears, allMonths, onApply, appliedFilters} = this.props;
    const {BOXES} = this.constructor;
    return (
      <div className="row filters-bar" onMouseDown={e => e.stopPropagation()}>
        <div className="col-lg-1 col-md-1 col-sm-1">
        </div>
        <div className="col-lg-9 col-md-9 col-sm-9">
          <div className="title">Filter your data</div>
          {BOXES.map((Box, index) => {
             return (
               <Box
                   key={index}
                   open={currentBoxIndex === index}
                   onClick={e => requestNewBox(currentBoxIndex === index ? null : index)}
                   state={state}
                   onUpdate={(slug, newState) => onUpdate(state.set(slug, newState))}
                   translations={translations}
                   onApply={newState => onApply(newState)}
                   allYears={allYears}
                   allMonths={allMonths}
                   appliedFilters={appliedFilters}
               />
             )
           })}
        </div>
        <div className="col-lg-1 col-md-1 col-sm-1 download">
          <button className="btn btn-default" disabled>
            <i className="glyphicon glyphicon-download-alt"></i>
          </button>
        </div>
        <div className="col-lg-1 col-md-1 col-sm-1"></div>
      </div>
    )
  }
}

Filters.BOXES = [
  Organizations,
  ProcurementMethodBox,
  ValueAmount,
  DateBox
];

export default Filters;
