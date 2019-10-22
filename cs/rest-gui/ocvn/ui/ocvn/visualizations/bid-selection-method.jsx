import ProcurementMethod from "../../oce/visualizations/charts/procurement-method";

class BidSelectionMethod extends ProcurementMethod{}

BidSelectionMethod.endpoint = 'tenderPriceByAllBidSelectionMethods';
BidSelectionMethod.getName = t => t('charts:bidSelectionMethod:title');
ProcurementMethod.CAT_NAME_FIELD = 'procurementMethodDetails';
ProcurementMethod.excelEP = 'bidSelectionExcelChart';

export default BidSelectionMethod;
