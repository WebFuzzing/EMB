import BidsByItem from "./bids-by-item";

class TotalAmountsByItem extends BidsByItem{
  static getName(t){return t('charts:amountsByItem:title')}

  getLayout(){
    return {
      xaxis: {
        title: this.t('charts:amountsByItem:xAxisTitle'),
        type: "category"
      },
      yaxis: {
        title: this.t('charts:amountsByItem:yAxisTitle'),
        tickprefix: "   "
      }
    }
  }
}

TotalAmountsByItem.excelEP = '';
TotalAmountsByItem.CAT_VALUE_FIELD = 'totalTenderAmount';

export default TotalAmountsByItem;
