import FrontendDateFilterableChart from "./frontend-date-filterable";
import {pluckImm} from "../../tools";
import {Map} from "immutable";

class AvgNrBids extends FrontendDateFilterableChart{
  getData(){
    let data = super.getData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: dates,
      y: data.map(pluckImm('averageNoTenderers')).toArray(),
      type: 'bar',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    const {hoverFormat} = this.props.styling.charts;
    return {
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:avgNrBids:yAxisTitle'),
        hoverformat: hoverFormat,
        tickprefix: "   "
      }
    }
  }
}

AvgNrBids.endpoint = 'averageNumberOfTenderers';
AvgNrBids.excelEP = 'averageNumberBidsExcelChart';
AvgNrBids.getName = t => t('charts:avgNrBids:title');
AvgNrBids.getFillerDatum = seed => Map(seed).set('averageNoTenderers', 0);

export default AvgNrBids;
