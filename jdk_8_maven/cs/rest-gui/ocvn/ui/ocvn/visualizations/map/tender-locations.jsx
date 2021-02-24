import TenderLocations from "../../../oce/visualizations/map/tender-locations";
import TenderLocation, {ChartTab, OverviewTab, OverviewChartTab, CostEffectivenessTab, ProcurementMethodTab}
  from "../../../oce/visualizations/map/tender-locations/location";
import BidSelectionMethod from "../../visualizations/bid-selection-method";
import {injectOceans} from "./index.jsx";

class BidSelectionMethodTab extends ProcurementMethodTab{
  static getName(t){return t('charts:bidSelectionMethod:title')}
}

BidSelectionMethodTab.Chart = BidSelectionMethod;

class OCVNTenderLocation extends TenderLocation{}

OCVNTenderLocation.TABS = [OverviewTab, OverviewChartTab, CostEffectivenessTab, BidSelectionMethodTab];

class OCVNTenderLocations extends injectOceans(TenderLocations){
    static getLayerName(t){return t('maps:invitationToBidLocations:title')}
}

OCVNTenderLocations.Location = OCVNTenderLocation;

export default OCVNTenderLocations;
