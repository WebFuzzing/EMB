import BidPeriod from "../../oce/visualizations/charts/bid-period";
import {pluckImm, yearlyResponse2obj, monthlyResponse2obj} from "../../oce/tools";

class OCVNBidPeriod extends BidPeriod{
  transform([tenders, awards, avg]){
    const monthly = avg && avg[0] && avg[0].month;
    const response2obj = monthly ? monthlyResponse2obj : yearlyResponse2obj;
    let transformed = super.transform([tenders, awards]);
    let avgHash = response2obj('avgTimeFromPlanToTenderPhase', avg);
    const dateKey = monthly ? 'month' : 'year';
    return transformed.map(datum => {
      datum.avg = avgHash[datum[dateKey]] || 0;
      return datum;
    })
  }

  getData(){
    let data = super.getRawData();
    if(!data) return [];

    const monthly = data.hasIn([0, 'month']);
    const dates = monthly ?
        data.map(pluckImm('month')).map(month => this.t(`general:months:${month}`)).toArray() :
        data.map(pluckImm('year')).toArray();

    return [{
      x: data.map(pluckImm('avg')).toArray(),
      y: dates,
      name: this.t('charts:bidPeriod:traces:avgTime'),
      type: "bar",
      orientation: 'h',
      marker: {
        color: this.props.styling.charts.traceColors[2]
      }
    }].concat(super.getData());
  }
}

OCVNBidPeriod.endpoints = BidPeriod.endpoints.concat('avgTimeFromPlanToTenderPhase');
OCVNBidPeriod.excelEP = 'bidTimelineExcelChart';
OCVNBidPeriod.getFillerDatum = seed => BidPeriod.getFillerDatum(seed).set('avg', 0);
OCVNBidPeriod.getMaxField = imm => BidPeriod.getMaxField(imm) + imm.get('avg');

export default OCVNBidPeriod;
