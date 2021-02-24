import Tab from "./index";
import CostEffectiveness from "../visualizations/charts/cost-effectiveness";
import AvgNrBids from "../visualizations/charts/avg-nr-bids";
import ProcurementMethod from "../visualizations/charts/procurement-method";
import FrequentTenderers from "../visualizations/tables/frequent-tenderers";

class Competitiveness extends Tab{
  static getName(t){return t('tabs:competitiveness:title')}
}

Competitiveness.icon = "competitive";
Competitiveness.visualizations = [CostEffectiveness, ProcurementMethod, AvgNrBids, FrequentTenderers];
export default Competitiveness;