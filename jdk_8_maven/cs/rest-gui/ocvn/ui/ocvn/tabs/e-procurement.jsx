import EProcurement from "../../oce/tabs/e-procurement";
import PercentEProcurement from "../visualizations/percent-e-procurement";

class OCVNProcurement extends EProcurement{}

OCVNProcurement.icon = "eprocurement";
OCVNProcurement.visualizations = [PercentEProcurement].concat(EProcurement.visualizations);

export default OCVNProcurement;
