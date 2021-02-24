import FrontendDateFilterableChart from "./frontend-date-filterable";
import {pluckImm} from "../../tools";

class NrCancelled extends FrontendDateFilterableChart{
  static getName(t){return t('charts:nrCancelled:title')};

  getData(){
    let data = super.getData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: dates,
      y: data.map(pluckImm('totalCancelled')).toArray(),
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
        title: this.t('charts:nrCancelled:yAxisTitle'),
        hoverformat: hoverFormat,
        tickprefix: "   "
      }
    }
  }
}

NrCancelled.endpoint = 'percentTendersCancelled';
NrCancelled.excelEP = 'numberCancelledFundingExcelChart';
NrCancelled.getMaxField = imm => imm.get('totalCancelled');

export default NrCancelled;


