import Table from "./index";
import {pluckImm} from "../../tools";
import orgnamesFetching from "../../orgnames-fetching";

class Suppliers extends orgnamesFetching(Table){
  getOrgsWithoutNamesIds(){
    if(!this.props.data) return [];
    return this.props.data.map(pluckImm('supplierId')).filter(id => !this.state.orgNames[id]).toJS();
  }

  row(entry){
    const id = entry.get('supplierId');
    return <tr key={id}>
      <td>{this.getOrgName(id)}</td>
      <td>{entry.get('totalContracts')}</td>
      <td>{entry.get('procuringEntityIdsCount')}</td>
      <td>{this.maybeFormat(entry.get('totalAwardAmount'))}</td>
    </tr>
  }

  render(){
    if(!this.props.data) return null;
    return (
      <table className="table table-striped table-hover suppliers-table">
        <thead>
        <tr>
          <th>{this.t('tables:top10suppliers:supplierName')}</th>
          <th>{this.t('tables:top10suppliers:nrAwardsWon')}</th>
          <th>{this.t('tables:top10suppliers:nrPE')}</th>
          <th>{this.t('tables:top10suppliers:totalAwardedValue')}</th>
        </tr>
        </thead>
        <tbody>
        {this.props.data.map(entry => this.row(entry))}
        </tbody>
      </table>
    )
  }
}

Suppliers.getName = t => t('tables:top10suppliers:title');
Suppliers.endpoint = 'topTenSuppliers';

export default Suppliers;