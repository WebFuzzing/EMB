import frontendDateFilterable from "../frontend-date-filterable";
import Chart from "./index.jsx";

class FrontendYearFilterableChart extends frontendDateFilterable(Chart){
  hasNoData(){
    let data = super.getData();
    return data && data.isEmpty();
  }
}

FrontendYearFilterableChart.UPDATABLE_FIELDS = ['data', 'years', 'months', 'monthly'];

export default FrontendYearFilterableChart;