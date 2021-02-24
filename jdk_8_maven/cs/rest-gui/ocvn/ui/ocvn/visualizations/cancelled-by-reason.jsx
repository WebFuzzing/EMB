import FrontendDateFilterableChart from "../../oce/visualizations/charts/frontend-date-filterable";

class CancelledFunding extends FrontendDateFilterableChart{
  static getName(t){return t('charts:cancelledFunding:title')}

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
      const date = datum.has('month') ?
          this.t('general:months:' + datum.get('month')) :
          datum.get('year');
      let totalCancelledTendersAmount = datum.get('totalCancelledTendersAmount');
      trace.x.push(date);
      trace.y.push(totalCancelledTendersAmount);
      if(hoverFormatter) trace.text.push(hoverFormatter(totalCancelledTendersAmount));
    });

    return [trace];
  }


  getLayout(){
    return {
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: 'category'
      },
      yaxis: {
        title: this.t('charts:cancelledFunding:yAxisTitle')
      }
    }
  }
}

CancelledFunding.endpoint = 'totalCancelledTendersByYearByRationale';
CancelledFunding.excelEP = 'cancelledTendersByYearByRationaleExcelChart';

export default CancelledFunding;
