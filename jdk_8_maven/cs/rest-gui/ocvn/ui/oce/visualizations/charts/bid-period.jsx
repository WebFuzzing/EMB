import FrontendDateFilterableChart from "./frontend-date-filterable";
import {pluckImm, yearlyResponse2obj, monthlyResponse2obj} from "../../tools";
import {Map} from "immutable";

let ensureNonNegative = a => a < 0 ? 0 : a;

class BidPeriod extends FrontendDateFilterableChart{
  transform([tenders, awards]) {
    const monthly = tenders && tenders[0] && tenders[0].month;
    const response2obj = monthly ? monthlyResponse2obj : yearlyResponse2obj;
    const awardsHash = response2obj('averageAwardDays', awards);
    const dateKey = monthly ? 'month' : 'year';
    return tenders.map(tender => ({
      [dateKey]: tender[dateKey],
      tender: +tender.averageTenderDays,
      award: +(awardsHash[tender[dateKey]] || 0)
    }))
  };

  getRawData(){
    return super.getData();
  }

  getData() {
    let data = super.getData();
    if (!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: data.map(pluckImm('tender')).map(ensureNonNegative).toArray(),
      y: dates,
      name: this.t('charts:bidPeriod:traces:tender'),
      type: "bar",
      orientation: 'h',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }, {
      x: data.map(pluckImm('award')).map(ensureNonNegative).toArray(),
      y: dates,
      name: this.t('charts:bidPeriod:traces:award'),
      type: "bar",
      orientation: 'h',
      marker: {
        color: this.props.styling.charts.traceColors[1]
      }
    }];
  }

  getLayout() {
    const {hoverFormat} = this.props.styling.charts;
    let annotations = [];
    let data = super.getData();
    if(data){
      annotations = data.map((imm, index) => {
        let sum = imm.reduce((sum, val, key) => "year" == key || "month" == key ? sum : sum + ensureNonNegative(val), 0).toFixed(2);
        return {
          y: index,
          x: sum,
          xanchor: 'left',
          yanchor: 'middle',
          text: this.t('charts:bidPeriod:traces:total') + ' ' + sum,
          showarrow: false
        }
      }).toArray();
    }

    return {
      annotations,
      barmode: "stack",
      xaxis: {
        title: this.t('charts:bidPeriod:xAxisTitle'),
        hoverformat: hoverFormat
      },
      yaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: "category"
      }
    }
  }
}

BidPeriod.endpoints = ['averageTenderPeriod', 'averageAwardPeriod'];
BidPeriod.excelEP = 'bidTimelineExcelChart';
BidPeriod.getName = t => t('charts:bidPeriod:title');
BidPeriod.horizontal = true;

BidPeriod.getFillerDatum = seed => Map(seed).set('tender', 0).set('award', 0);
BidPeriod.getMaxField = imm => imm.get('tender', 0) + imm.get('award', 0);

export default BidPeriod;
