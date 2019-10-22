import Efficiency from "../../oce/tabs/efficiency";
import CancelledByReason from "../visualizations/cancelled-by-reason";
import BidPeriod from "../../oce/visualizations/charts/bid-period";
import OCVNBidPeriod from "../visualizations/bid-period";
import {arrReplace} from "../../oce/tools";

class OCVNEfficiency extends Efficiency{}

OCVNEfficiency.visualizations = arrReplace(BidPeriod, OCVNBidPeriod, Efficiency.visualizations)
    .concat(CancelledByReason);

export default OCVNEfficiency;
