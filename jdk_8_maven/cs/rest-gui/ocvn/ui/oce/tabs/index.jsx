import Visualization from "../visualization";
import {Set, List} from "immutable";
import {Map} from "immutable";
import DefaultComparison from "../comparison";
import Chart from "../visualizations/charts/index";
import {download} from '../tools';

class Tab extends Visualization{
  maybeWrap(Component, index, rendered){
    let {dontWrap, getName} = Component;
    let {filters, years, months} = this.props;
    let ref = `section${index}`;
    let exportable = Component.prototype instanceof Chart;
    return dontWrap ? rendered : <section key={index} ref={ref}>
      <h4 className="page-header">
        {getName(this.t.bind(this))}
        {exportable && Component.excelEP && <img
            src="assets/icons/export-black.svg"
            width="16"
            height="16"
            className="chart-export-icon"
            onClick={e => download({
              ep: Component.excelEP,
              filters,
              years,
              months,
              t: this.t.bind(this)
            })}
        />}
        {exportable && <img
            src="assets/icons/camera.svg"
            className="chart-export-icon"
            onClick={e => this.refs[ref].querySelector(".modebar-btn:first-child").click()}
        />}
      </h4>
      {rendered}
    </section>
  }

  compare(Component, index){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, bidTypes
        , width, translations, styling, monthly, months} = this.props;
    let {compareWith: CustomComparison} = Component;
    let Comparison = CustomComparison || DefaultComparison;
    return <Comparison
        key={index}
        compareBy={compareBy}
        comparisonData={comparisonData.get(index, List())}
        comparisonCriteriaValues={comparisonCriteriaValues}
        filters={filters}
        requestNewComparisonData={(path, data) => requestNewComparisonData([index, ...path], data)}
        years={years}
        monthly={monthly}
        months={months}
        Component={Component}
        bidTypes={bidTypes}
        width={width}
        translations={translations}
        styling={styling}
    />
  }

  render(){
    let {filters, compareBy, requestNewData, data, years, months, monthly, width, translations, styling} = this.props;
    return <div className="col-sm-12 content">
      {this.constructor.visualizations.map((Component, index) =>
          compareBy && Component.comparable ? this.compare(Component, index) :
              this.maybeWrap(Component, index,
                  <Component
                      key={index}
                      filters={filters}
                      requestNewData={(_, data) => requestNewData([index], data)}
                      data={data.get(index)}
                      monthly={monthly}
                      years={years}
                      months={months}
                      width={width}
                      translations={translations}
                      styling={styling}
                      margin={{t: 10, l: 100, b: 80, r: 20, pad: 20}}
                  />
              )
      )}
    </div>
  }

  static computeYears(data){
    if(!data) return Set();
    return this.visualizations.reduce((years, visualization, index) =>
            visualization.computeYears ?
                years.union(visualization.computeYears(data.get(index))) :
                years
        , Set())
  }

  static computeComparisonYears(data){
    if(!data) return Set();
    return this.visualizations.reduce((years, visualization, index) =>
            years.union(
                data.get(index, List()).reduce((years, data, index) =>
                        visualization.computeYears ?
                            years.union(visualization.computeYears(data)) :
                            years
                    , Set())
            )
        , Set())
  }

  componentDidMount(){
    window.scrollTo(0, 0);
  }
}

Tab.visualizations = [];

Tab.propTypes = {
  filters: React.PropTypes.object.isRequired,
  data: React.PropTypes.object,
  comparisonData: React.PropTypes.object,
  requestNewData: React.PropTypes.func.isRequired,
  requestNewComparisonData: React.PropTypes.func.isRequired,
  compareBy: React.PropTypes.string.isRequired,
  comparisonCriteriaValues: React.PropTypes.arrayOf(React.PropTypes.string).isRequired,
  width: React.PropTypes.number.isRequired
};


export default Tab;
