import Chart from "./index";
import backendYearFilterable from "../../backend-year-filterable";
import {Map, OrderedMap, Set} from "immutable";
import Comparison from "../../comparison";
import {download} from "../../tools";

class CatChart extends backendYearFilterable(Chart) {
  static getCatName(datum){return datum.get(this.CAT_NAME_FIELD)}

  getData(){
    let data = super.getData();
    if(!data) return [];
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      type: 'bar',
      marker: {
        color: traceColors[0]
      }
    };

    if(hoverFormatter){
      trace.text = [];
      trace.hoverinfo = "text";
    }

    data.forEach(datum => {
      let catName = this.constructor.getCatName(datum, this.t.bind(this));
      let value = datum.get(this.constructor.CAT_VALUE_FIELD);
      trace.x.push(catName);
      trace.y.push(value);
      if(hoverFormatter) trace.text.push(hoverFormatter(value));
    });

    return [trace];
  }
}

class CatChartComparison extends Comparison{
  render(){
    let {compareBy, comparisonData, comparisonCriteriaValues, filters, requestNewComparisonData, years, translations,
      styling, width, months, monthly} = this.props;
    if(!comparisonCriteriaValues.length) return null;
    let Component = this.getComponent();
    let decoratedFilters = this.constructor.decorateFilters(filters, compareBy, comparisonCriteriaValues);
    let rangeProp, uniformData;

    if(comparisonData.count() == comparisonCriteriaValues.length + 1){
      let byCats = comparisonData.map(
          data => data.reduce(
              (cats, datum) => cats.set(datum.get(Component.CAT_NAME_FIELD), datum),
              Map()
          )
      );

      let cats = comparisonData.reduce(
          (cats, data) => data.reduce(
              (cats, datum) => {
                let cat = datum.get(Component.CAT_NAME_FIELD);
                return cats.set(cat, Map({
                  [Component.CAT_NAME_FIELD]: cat,
                  [Component.CAT_VALUE_FIELD]: 0
                }))
              },
              cats
          ),
          Map()
      );

      uniformData = byCats.map(
          data => cats.merge(data).toList()
      );

      let maxValue = uniformData.reduce(
          (max, data) => data.reduce(
              (max, datum) => Math.max(max, datum.get(Component.CAT_VALUE_FIELD))
              , max
          )
          , 0
      );

      rangeProp = {
        yAxisRange: [0, maxValue]
      };
    } else {
      rangeProp = {};
      uniformData = comparisonData;
    }

    return this.wrap(decoratedFilters.map((comparisonFilters, index) => {
      let ref = `visualization${index}`;
      let downloadExcel = e => download({
        ep: Component.excelEP,
        filters: comparisonFilters,
        years,
        months,
        t: this.t.bind(this)
      });
      return <div className="col-md-6 comparison" key={index} ref={ref}>
        <Component
            filters={comparisonFilters}
            margin={{b: 200}}
            requestNewData={(_, data) => requestNewComparisonData([index], data)}
            data={uniformData.get(index)}
            years={years}
            months={months}
            monthly={monthly}
            title={this.getTitle(index)}
            translations={translations}
            styling={styling}
            width={width / 2}
            {...rangeProp}
        />
        <div className="chart-toolbar">
          <div className="btn btn-default" onClick={downloadExcel}>
            <img src="assets/icons/export-black.svg" width="16" height="16"/>
          </div>

          <div className="btn btn-default" onClick={e => this.refs[ref].querySelector(".modebar-btn:first-child").click()}>
            <img src="assets/icons/camera.svg"/>
          </div>
        </div>
      </div>
    }));
  }
}

CatChart.compareWith = CatChartComparison;

CatChart.UPDATABLE_FIELDS = ['data'];

export default CatChart;