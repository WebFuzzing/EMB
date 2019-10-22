import FrontendDateFilterableChart from "../../oce/visualizations/charts/frontend-date-filterable"
import {pluckImm} from "../../oce/tools";

class PercentEProcurement extends FrontendDateFilterableChart {
  getData() {
    let data = super.getData();
    if (!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();


    return [{
      x: dates,
      y: data.map(pluckImm('percentEgp')).toArray(),
      type: 'scatter',
      fill: 'tonexty',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout() {
    return {
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:percentEProcurement:yAxisTitle'),
        hoverformat: '.2f'
      }
    }
  }
}


PercentEProcurement.endpoint = 'percentTendersUsingEgp';
PercentEProcurement.excelEP = 'percentTendersUsingEgpExcelChart';
PercentEProcurement.getName = t => t('charts:percentEProcurement:title');
PercentEProcurement.getMaxField = imm => imm.get('percentEgp', 0);

export default PercentEProcurement;