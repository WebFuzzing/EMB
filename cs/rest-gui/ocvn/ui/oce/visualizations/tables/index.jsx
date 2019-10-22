import Visualization from '../../visualization';
import backendYearFilterable from "../../backend-year-filterable";

class Table extends backendYearFilterable(Visualization){
  maybeFormat(number){
    const {tables: tableStyling} = this.props.styling;
    return tableStyling && tableStyling.currencyFormatter ?
        tableStyling.currencyFormatter(number) :
        number;
  }
}

Table.DATE_FORMAT = {
  year: 'numeric',
  month: 'short',
  day: 'numeric'
};

Table.comparable = false;

export default Table;