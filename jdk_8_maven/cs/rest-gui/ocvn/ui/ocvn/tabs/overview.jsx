import Overview from "../../oce/tabs/overview";
import OCEOverviewChart from "../../oce/visualizations/charts/overview";
import OCVNOverviewChart from "../visualizations/overview";
import {arrReplace} from "../../oce/tools";

class OCVNOverview extends Overview{}

OCVNOverview.visualizations = arrReplace(OCEOverviewChart, OCVNOverviewChart, Overview.visualizations);

export default OCVNOverview;