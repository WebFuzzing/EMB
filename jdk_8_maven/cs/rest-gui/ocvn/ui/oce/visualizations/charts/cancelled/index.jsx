import Amounts from "./amounts";
import Percents from "./percents";
import translatable from "../../../translatable";
import {Set} from "immutable";
import Comparison from "../../../comparison";
import ReactDOM from "react-dom";
import {download} from "../../../tools";

class Cancelled extends translatable(React.Component){
  constructor(props){
    super(props);
    this.state = {
      percents: false
    }
  }

  render(){
    let {percents} = this.state;
    let {filters, years, months} = this.props;
    let Chart = percents ? Percents : Amounts;
    return <section>
      <h4 className="page-header">
        {percents ? this.t('charts:cancelledPercents:title') : this.t('charts:cancelledAmounts:title')}
        &nbsp;
        <button
            className="btn btn-default btn-sm"
            onClick={_ => this.setState({percents: !percents})}
            dangerouslySetInnerHTML={{__html: percents ? '&#8363;' : '%'}}
        />
        <img
            src="assets/icons/export-black.svg"
            width="16"
            height="16"
            className="chart-export-icon"
            onClick={e => download({
              ep: Chart.excelEP,
              filters,
              years,
              months,
              t: this.t.bind(this)
            })}
        />

        <img
            src="assets/icons/camera.svg"
            className="chart-export-icon"
            onClick={e => ReactDOM.findDOMNode(this).querySelector(".modebar-btn:first-child").click()}
        />
      </h4>
      <Chart {...this.props}/>
    </section>
  }

  static computeYears(data){
    if(!data) return Set();
    return Amounts.computeYears(data).union(Percents.computeYears(data));
  }
}

Cancelled.dontWrap = true;
Cancelled.comparable = true;
Cancelled.compareWith = class CancelledComparison extends Comparison{
  constructor(props){
    super(props);
    this.state.percents = false;
  }

  getComponent(){
    return this.state.percents ? Percents : Amounts;
  }

  wrap(children){
    let {percents} = this.state;
    return <div>
      <h3 className="page-header">
        {percents ? this.t('charts:cancelledPercents:title') : this.t('charts:cancelledAmounts:title')}{percents ? this.t('charts:cancelledPercents:title') : this.t('charts:cancelledAmounts:title')}
        &nbsp;
        <button
            className="btn btn-default btn-sm"
            onClick={_ => this.setState({percents: !percents})}
            dangerouslySetInnerHTML={{__html: percents ? '&#8363;' : '%'}}
        />
      </h3>
      <div className="row">
        {children}
      </div>
    </div>
  }
};

export default Cancelled;
