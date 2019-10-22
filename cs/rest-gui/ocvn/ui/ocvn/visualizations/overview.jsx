import Overview from "../../oce/visualizations/charts/overview";
import {pluckImm, yearlyResponse2obj, monthlyResponse2obj} from "../../oce/tools";

class OCVNOverview extends Overview{
  transform([bidplansResponse, tendersResponse, awardsResponse]){
    const transformed = super.transform([tendersResponse, awardsResponse]);
    const monthly = bidplansResponse && bidplansResponse[0] && bidplansResponse[0].month;
    const response2obj = monthly ? monthlyResponse2obj : yearlyResponse2obj;
    const bidplans = response2obj('count', bidplansResponse);
    const dateKey = monthly ? 'month' : 'year';
    return transformed.map(datum => {
      datum.bidplan = bidplans[datum[dateKey]];
      return datum;
    })
  }

  getData(){
    const data = super.getRawData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: dates,
      y: data.map(pluckImm('bidplan')).toArray(),
      type: 'scatter',
      name: this.t('charts:overview:traces:bidplan'),
      marker: {
        color: this.props.styling.charts.traceColors[2]
      }
    }].concat(super.getData());
  }
}

OCVNOverview.endpoints = ['countBidPlansByYear'].concat(Overview.endpoints);

export default OCVNOverview;