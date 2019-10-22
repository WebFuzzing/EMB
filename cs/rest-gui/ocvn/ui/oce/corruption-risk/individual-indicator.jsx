import CustomPopupChart from "./custom-popup-chart";
import {Map} from "immutable";
import {pluckImm} from "../tools";
import Table from "../visualizations/tables/index";
import translatable from "../translatable";
import CRDPage from "./page";
import {colorLuminance} from "./tools";

class IndividualIndicatorChart extends CustomPopupChart{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/stats`;
  }

  getData(){
    const data = super.getData();
    const {traceColors} = this.props.styling.charts;
    if(!data) return [];
    const {monthly} = this.props;
    let dates = monthly ?
                data.map(datum => {
                  const month = datum.get('month');
                  return this.t(`general:months:${month}`);
                }).toJS() :
                data.map(pluckImm('year')).toJS();

    let totalTrueValues = data.map(pluckImm('totalTrue', 0)).toJS();
    let totalPrecondMetValues = data.map(pluckImm('totalPrecondMet', 0)).toJS();

    if(dates.length == 1){
      dates.unshift("");
      dates.push(" ");
      totalTrueValues.unshift(0);
      totalTrueValues.push(0);
      totalPrecondMetValues.unshift(0);
      totalPrecondMetValues.push(0);
    }

    return [{
      x: dates,
      y: totalTrueValues,
      type: 'scatter',
      fill: 'tonexty',
      name: 'Flagged Procurements',
      hoverinfo: 'none',
      fillcolor: traceColors[0],
      line: {
        color: colorLuminance(traceColors[0], -.3),
      },
    }, {
      x: dates,
      y: totalPrecondMetValues,
      type: 'scatter',
      fill: 'tonexty',
      name: 'Eligible Procurements',
      hoverinfo: 'none',
      fillcolor: traceColors[1],
      line: {
        color: colorLuminance(traceColors[1], -.3)
      }
    }];
  }

  getLayout(){
    return {
      legend: {
        orientation: 'h',
        xanchor: 'right',
        yanchor: 'bottom',
        x: 1,
        y: 1
      },
      hovermode: 'closest',
      xaxis: {
        type: 'category',
        showgrid: false
      },
      yaxis: {}
    }
  }

  getPopup(){
    const {indicator, monthly} = this.props;
    const {popup} = this.state;
    const {year} = popup;
    const data = super.getData();
    if(!data) return null;
    let datum;
    if(monthly){
      datum = data.find(datum => {
        const month = datum.get('month');
        return year == this.t(`general:months:${month}`);
      })
    } else {
      datum = data.find(datum => datum.get('year') == year);
    }
    return (
      <div className="crd-popup" style={{top: popup.top, left: popup.left}}>
        <div className="row">
          <div className="col-sm-12 info text-center">
            {year}
          </div>
          <div className="col-sm-12">
            <hr/>
          </div>
          <div className="col-sm-8 text-right title">Procurements Flagged</div>
          <div className="col-sm-4 text-left info">{datum.get('totalTrue')}</div>
          <div className="col-sm-8 text-right title">Eligible Procurements</div>
          <div className="col-sm-4 text-left info">{datum.get('totalPrecondMet')}</div>
          <div className="col-sm-8 text-right title">% Eligible Procurements Flagged</div>
          <div className="col-sm-4 text-left info">{datum.get('percentTruePrecondMet').toFixed(2)} %</div>
          <div className="col-sm-8 text-right title">% Procurements Eligible</div>
          <div className="col-sm-4 text-left info">{datum.get('percentPrecondMet').toFixed(2)} %</div>
        </div>
        <div className="arrow"/>
      </div>
    )
  }
}

import ProcurementsTable from "./procurements-table";

class ProjectTable extends ProcurementsTable{
  getCustomEP(){
    const {indicator} = this.props;
    return `flags/${indicator}/releases?pageSize=10`;
  }

  getClassName(){
    return "table-project-table";
  }
}

class IndividualIndicatorPage extends translatable(CRDPage){
  constructor(...args){
    super(...args);
    this.state = {
    }
  }

  render(){
    const {chart, table} = this.state;
    const {indicator, translations, filters, years, monthly, months, width
    , styling} = this.props;
    return (
      <div className="page-corruption-type">
        <h2 className="page-header">{this.t(`crd:indicators:${indicator}:name`)}</h2>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:indicator")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:indicator`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:eligibility")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:eligibility`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:thresholds")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:thresholds`)}
        </p>
        <p className="definition">
          <strong>{this.t("crd:indicators:general:description")}</strong>
          &nbsp;
          {this.t(`crd:indicators:${indicator}:description`)}
        </p>
        <section>
          <h3 className="page-header">
            Eligible Procurements and Flagged Procurements for {this.t(`crd:indicators:${indicator}:name`)}
          </h3>
          <IndividualIndicatorChart
            indicator={indicator}
            translations={translations}
            filters={filters}
            years={years}
            monthly={monthly}
            months={months}
            requestNewData={(_, data) => this.setState({chart: data})}
            data={chart}
            width={width - 20}
            styling={styling}
            margin={{t: 0, b: 80, r: 40, pad: 40}}
          />
        </section>
        <section>
          <h3 className="page-header">
            List of Procurements Flagged for {this.t(`crd:indicators:${indicator}:name`)}
          </h3>
          <ProjectTable
            indicator={indicator}
            requestNewData={(_, data) => this.setState({table: data})}
            data={table}
            translations={translations}
            filters={filters}
            years={years}
            monthly={monthly}
            months={months}
          />
        </section>
      </div>
    )
  }
}

export default IndividualIndicatorPage;
