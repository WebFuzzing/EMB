import { List } from 'immutable';
import Table from "./index";
import orgNamesFetching from "../../orgnames-fetching";
import {pluckImm, fetchJson} from "../../tools";
import translatable from '../../translatable';

class WinCount extends translatable(React.Component){
  constructor(...args){
    super(...args);
    this.state = {
      cnt: this.t("general:loading")
    }
  }

  componentDidMount(){
    const {id} = this.props;
    fetchJson(`/api/activeAwardsCount?supplierId=${id}`)
      .then(response => this.setState({
        cnt: response[0].cnt
      }))
  }

  render(){
    const {id} = this.props;
    const {cnt} = this.state;
    return <td>{cnt}</td>
  }
}

class FrequentTenderers extends orgNamesFetching(Table){
  constructor(...args){
    super(...args);
    this.state = this.state || {};
    this.state.showAll = false;
  }

  row(entry, index){
    const {translations} = this.props;
    const id1 = entry.get('tendererId1');
    const id2 = entry.get('tendererId2');
    return <tr key={index}>
      <td>{this.getOrgName(id1)}</td>
      <td>{this.getOrgName(id2)}</td>
      <td>{entry.get('pairCount')}</td>
      <WinCount id={id1} translations={translations}/>
      <WinCount id={id2} translations={translations}/>
    </tr>
  }

  maybeSlice(flag, list){
    return flag ? list.slice(0, 10) : list;
  }

  getOrgsWithoutNamesIds(){
    const {data} = this.props;
    if(!data) return [];
    return data.map(datum => List([datum.get('tendererId1'), datum.get('tendererId2')]))
      .flatten()
      .filter(id => !this.state.orgNames[id]).toJS();
  }

  render(){
    if(!this.props.data) return null;
    const {showAll} = this.state;
    return <table className="table table-stripped trable-hover frequent-supplier-bidder-table">
      <thead>
      <tr>
        <th>{this.t('tables:frequentTenderers:supplier')} #1</th>
        <th>{this.t('tables:frequentTenderers:supplier')} #2</th>
        <th>{this.t('tables:frequentTenderers:nrITB')}</th>
        <th>{this.t('tables:frequentTenderers:supplier1wins')}</th>
        <th>{this.t('tables:frequentTenderers:supplier2wins')}</th>
      </tr>
      </thead>
      <tbody>
      {this.maybeSlice(!showAll, this.props.data).map(this.row.bind(this))}
      {!showAll && this.props.data.count() > 10 && <tr>
        <td colSpan="5">
          <button className="btn btn-info btn-danger btn-block" onClick={_ => this.setState({showAll: true})}>
            {this.t('tables:showAll')}
          </button>
        </td>
      </tr>}
      </tbody>
    </table>
  }
}

FrequentTenderers.getName = t => t('tables:frequentTenderers:title');
FrequentTenderers.endpoint = 'frequentTenderers';

export default FrequentTenderers;
