import FrontendDateFilterableChart from "./frontend-date-filterable";
import {Map} from "immutable";

class CostEffectiveness extends FrontendDateFilterableChart{
  mkTrace(name, colorIndex){
    let {traceColors, hoverFormatter} = this.props.styling.charts;
    let trace = {
      x: [],
      y: [],
      text: [],
      name,
      type: 'bar',
      marker: {
        color: traceColors[colorIndex]
      }
    };

    if(hoverFormatter) trace.hoverinfo = "text+name";

    return trace;
  }

  getData(){
    let data = super.getData();
    if(!data) return [];
    let traces = [
      this.mkTrace(this.t('charts:costEffectiveness:traces:awardPrice'), 0),
      this.mkTrace(this.t('charts:costEffectiveness:traces:difference'), 1)
    ];

    let {hoverFormatter} = this.props.styling.charts;

    data.forEach(datum => {
      const date = datum.has('month') ?
          this.t('general:months:' + datum.get('month')) :
          datum.get('year');
      traces.forEach(trace => trace.x.push(date));
      let tender = datum.get('totalTenderAmount');
      let diff = datum.get('diffTenderAwardAmount');
      traces[0].y.push(tender);
      traces[1].y.push(diff);
      if(hoverFormatter){
        traces[0].text.push(hoverFormatter(tender));
        traces[1].text.push(hoverFormatter(diff));
      }
    });

    return traces;
  }

  getLayout(){
    return {
      barmode: "relative",
      xaxis: {
        title: this.props.monthly ? this.t('general:month') : this.t('general:year'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:costEffectiveness:yAxisTitle'),
        tickprefix: "   "
      }
    }
  }
}

CostEffectiveness.getName = t => t('charts:costEffectiveness:title');
CostEffectiveness.endpoint = 'costEffectivenessTenderAwardAmount';
CostEffectiveness.excelEP = 'costEffectivenessExcelChart';
CostEffectiveness.getFillerDatum = seed => Map(seed).set('tender', 0).set('diff', 0);

CostEffectiveness.getMaxField = imm => imm.get('totalTenderAmount') + imm.get('diffTenderAwardAmount');

export default CostEffectiveness;
