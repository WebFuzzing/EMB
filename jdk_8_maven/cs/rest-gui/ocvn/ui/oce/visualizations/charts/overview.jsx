import FrontendDateFilterableChart from "./frontend-date-filterable";
import {yearlyResponse2obj, monthlyResponse2obj, pluckImm} from "../../tools";

class OverviewChart extends FrontendDateFilterableChart{
  transform([tendersResponse, awardsResponse]){
    const monthly = tendersResponse && tendersResponse[0] && tendersResponse[0].month;
    const response2obj = monthly ? monthlyResponse2obj : yearlyResponse2obj;
    const tenders = response2obj('count', tendersResponse);
    const awards = response2obj('count', awardsResponse);
    const dateKey = monthly ? 'month' : 'year';
    return Object.keys(tenders).map(date => ({
      [dateKey]: date,
      tender: tenders[date],
      award: awards[date]
    }));
  }

  getRawData(){
    return super.getData();
  }

  getData(){
    const data = super.getData();
    if(!data) return [];
    const LINES = {
      award: this.t('charts:overview:traces:award'),
      tender: this.t('charts:overview:traces:tender')
    };

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return Object.keys(LINES).map((key, index) => ({
          x: dates,
          y: data.map(pluckImm(key)).toArray(),
          type: 'scatter',
          name: LINES[key],
          marker: {
            color: this.props.styling.charts.traceColors[index]
          }
        })
    );
  }

  getLayout(){
    return {
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:overview:yAxisName'),
        exponentformat: 'none',
        tickprefix: "   "
      }
    }
  }
}

OverviewChart.endpoints = ['countTendersByYear', 'countAwardsByYear'];
OverviewChart.excelEP = 'procurementActivityExcelChart';

OverviewChart.getName = t => t('charts:overview:title');

export default OverviewChart;
