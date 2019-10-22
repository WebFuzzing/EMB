import FrontendDateFilterable from "./frontend-date-filterable";
import {pluckImm} from "../../tools";

class PercentEbid extends FrontendDateFilterable{
  getData(){
    let data = super.getData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: dates,
      y: data.map(pluckImm('percentageTendersUsingEbid')).toArray(),
      type: 'scatter',
      fill: 'tonexty',
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
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:percentEBid:yAxisName'),
        hoverformat: hoverFormat,
        tickprefix: "   "
      }
    }
  }
}

PercentEbid.endpoint = 'percentTendersUsingEBid';
PercentEbid.excelEP = 'percentTendersUsingEBidExcelChart';
PercentEbid.getName = t => t('charts:percentEBid:title');
PercentEbid.getMaxField = pluckImm('percentageTendersUsingEbid');

export default PercentEbid;
