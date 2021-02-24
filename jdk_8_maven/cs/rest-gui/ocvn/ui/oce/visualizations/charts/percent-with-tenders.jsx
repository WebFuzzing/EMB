import FrontendDateFilterableChart from "./frontend-date-filterable";
import {pluckImm} from "../../tools";

class PercentWithTenders extends FrontendDateFilterableChart{
  static getName(t){return t('charts:percentWithTenders:title')}

  getData(){
    let data = super.getData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: dates,
      y: data.map(pluckImm('percentTenders')).toArray(),
      type: 'scatter',
      fill: 'tonexty',
      marker: {
        color: this.props.styling.charts.traceColors[0]
      }
    }];
  }

  getLayout(){
    return {
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:percentWithTenders:yAxisTitle'),
        hoverformat: '.2f',
        tickprefix: "   "
      }
    }
  }
}

PercentWithTenders.endpoint = 'percentTendersWithLinkedProcurementPlan';
PercentWithTenders.excelEP = 'tendersWithLinkedProcurementPlanExcelChart';
PercentWithTenders.getMaxField = imm => imm.get('percentTenders', 0);

export default PercentWithTenders;
