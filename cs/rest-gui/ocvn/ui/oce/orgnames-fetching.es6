import URI from "urijs";
import {send, callFunc, shallowCopy} from "./tools";

const orgNamesFetching = Class => class extends Class{
  constructor(...args){
    super(...args);
    this.state = this.state || {};
    this.state.orgNames = {};
  }

  getOrgName(id){
    return this.state.orgNames[id] || id;
  }

  maybeFetchOrgNames(){
    const idsWithoutNames = this.getOrgsWithoutNamesIds();
    if(!idsWithoutNames.length) return;
    send(new URI('/api/ocds/organization/ids').addSearch('id', idsWithoutNames))
      .then(callFunc('json'))
      .then(orgs => {
				let orgNames = shallowCopy(this.state.orgNames);
				if(!orgs.length){//prevent infinite requests when no orgs names are found
					idsWithoutNames.forEach(id => orgNames[id] = id);
				} else {
					orgs.forEach(({id, name}) => orgNames[id] = name);
				}
        this.setState({orgNames})
      })
  }

  componentDidMount(){
    super.componentDidMount && super.componentDidMount();
    this.maybeFetchOrgNames();
  }

  componentDidUpdate(...args){
    super.componentDidUpdate && super.componentDidUpdate(...args);
    this.maybeFetchOrgNames();
  }
};

export default orgNamesFetching;
