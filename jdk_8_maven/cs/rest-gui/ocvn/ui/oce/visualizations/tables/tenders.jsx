import Table from "./index";

//safely gets the date from the data and formats it
const getDate = (obj/*tender obj*/, key/*date key*/) => obj.hasIn(['tenderPeriod', key]) ?
    new Date(obj.getIn(['tenderPeriod', key])).toLocaleDateString(undefined, Table.DATE_FORMAT) :
    "";

class Tenders extends Table{
  row(entry){
    let bidNo = entry.getIn(['planning', 'bidNo']);
    let tender = entry.get('tender');
    let value = tender.get('value');
    return (
      <tr key={bidNo}>
        <td>{bidNo}</td>
        <td>{getDate(tender, 'startDate')}</td>
        <td>{getDate(tender, 'endDate')}</td>
        <td className="procuring-entity-title">{tender.getIn(['procuringEntity', 'name'])}</td>
        <td>{this.maybeFormat(value.get('amount'))} {value.get('currency')}</td>
      </tr>
    )
  }

  render(){
    if(!this.props.data) return null;
    return (
      <table className="table table-striped table-hover tenders-table">
        <thead>
          <tr>
            <th>{this.t('tables:top10tenders:number')}</th>
            <th>{this.t('tables:top10tenders:startDate')}</th>
            <th>{this.t('tables:top10tenders:endDate')}</th>
            <th>{this.t('tables:top10tenders:procuringEntity')}</th>
            <th>{this.t('tables:top10tenders:estimatedValue')}</th>
          </tr>
        </thead>
        <tbody>
          {this.props.data.map(this.row.bind(this))}
        </tbody>
      </table>
    )
  }
}

Tenders.getName = t => t('tables:top10tenders:title');
Tenders.endpoint = 'topTenLargestTenders';

export default Tenders;
