import Competitiveness from "../../oce/tabs/competitiveness";
import ProcurementMethod from "../../oce/visualizations/charts/procurement-method";
import BidSelectionMethod from "../visualizations/bid-selection-method";

class OCVNCompetitiveness extends Competitiveness{}

OCVNCompetitiveness.visualizations = Competitiveness.visualizations.map(visual => ProcurementMethod == visual ? BidSelectionMethod : visual);
export default OCVNCompetitiveness;