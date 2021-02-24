import Component from "../pure-render-component";
import translatable from "../translatable";
import cn from "classnames";
import Organizations from "./tabs/organizations";
import ProcurementRules from "./tabs/procurement-rules.jsx";
import Amounts from "./tabs/amounts";
import {Map} from "immutable";
import {send, fetchJson, callFunc} from "../tools";
import URI from "urijs";
import {DropdownButton, MenuItem} from "react-bootstrap";
import {fromJSON, toJSON} from "transit-immutable-js";

const dashboardId = new URI(location).search(true).dashboardId;

class Filters extends translatable(Component){
  constructor(props){
    super(props);
    this.state = {
      currentTab: 0,
      state: Map(),
      savingDashboard: false,
      dashboardName: "",
      dashboards: []
    };

    const save = ep => () => {
      const {state, dashboardName} = this.state;
      const encoded = toJSON(state);
      send(new URI(`/rest/userDashboards/${ep}`)
          .addSearch('name', dashboardName)
          .addSearch('formUrlEncodedBody', encoded)
      ).then(callFunc('text')).then(response => response.length ?
          alert(this.t('filters:dashboard:saveError')) : //response text means error
          this.fetchDashboards()//no response text means success, on success, no response is expected
      );
      this.setState({
        savingDashboard: false,
        dashboardName: ""
      })
    };

    this.saveForCurrentUser = save('saveDashboardForCurrentUser')
    this.saveUnassigned = save('saveDashboard');
  }

  listTabs(){
    let {currentTab} = this.state;
    return this.constructor.TABS.map((Tab, index) => <li
            key={index}
            role="presentation"
            className={cn({active: index == currentTab})}
            onClick={_ => this.setState({currentTab: index})}
        >
          <a href="javascript:void(0);">
            {Tab.getName(this.t.bind(this))}
          </a>
        </li>
    );
  }

  content(){
    let {currentTab, state} = this.state;
    let {bidTypes, translations} = this.props;
    let Component = this.constructor.TABS[currentTab];
    return <Component
        state={state}
        onUpdate={(key, update) => this.setState({state: this.state.state.set(key, update)})}
        bidTypes={bidTypes}
        translations={translations}
    />
  }

  reset(){
    this.setState({state: Map()});
    this.props.onUpdate(Map())
  }

  fetchDashboards(){
    fetchJson('/rest/userDashboards/search/getDashboardsForCurrentUser')
        .then(data => {
          const dashboards = data._embedded ? data._embedded.userDashboards : [];
          this.setState({dashboards});
          if(dashboardId){
            for(let counter in dashboards){
              const dashboard = dashboards[counter];
              if(dashboard.id == dashboardId){
                this.updateFilters(fromJSON(dashboard.formUrlEncodedBody));
                break;
              }
            }
          }
          return data;
        })
        .catch(() => null);
  }

  componentDidMount(){
    this.fetchDashboards();
    !dashboardId && fetchJson('/rest/userDashboards/search/getDefaultDashboardForCurrentUser')
        .then(({formUrlEncodedBody}) => this.updateFilters(fromJSON(formUrlEncodedBody)))
        .catch(() => null)
  }

  updateFilters(newFilters){
    this.setState({state: newFilters});
    this.props.onUpdate(newFilters);
  }

  render(){
    let {onClick, onUpdate, open, user} = this.props;
    const {savingDashboard, dashboardName, dashboards} = this.state;
    return <div className={cn('filters', {open})}  onClick={onClick}>
      <img className="top-nav-icon" src="assets/icons/filter.svg" width="100%" height="100%"/> {this.t('filters:title')} <i className="glyphicon glyphicon-menu-down"></i>
      <div className="box row" onClick={e => e.stopPropagation()}>
        <ul className="nav nav-pills nav-stacked col-xs-4">
          {this.listTabs()}
        </ul>
        <div className="col-xs-8 filter-tab-content">
          {this.content()}
        </div>
        <section className="buttons col-xs-offset-4 col-xs-8">
          <button className="btn btn-danger" onClick={e => onUpdate(this.state.state)}>
            {this.t('filters:apply')}
          </button>
          &nbsp;
          <button className="btn btn-default" onClick={e => this.reset()}>
            {this.t('filters:reset')}
          </button>
          &nbsp;
          {user.loggedIn && !savingDashboard &&
            <button className="btn btn-default" onClick={e => this.setState({savingDashboard: true})}>
              {this.t('filters:dashboard:save')}
            </button>
          }
          {savingDashboard &&
            <input
                type="text"
                className="input-sm form-control dashboard-name"
                value={dashboardName}
                onChange={e => this.setState({dashboardName: e.target.value})}
            />
          }
          &nbsp;
          {savingDashboard && !user.isAdmin &&
              <button className="btn btn-default" onClick={e => this.saveForCurrentUser()}>
                {this.t('filters:dashboard:save')}
              </button>
          }
          {savingDashboard && user.isAdmin &&
              <DropdownButton dropup id="admin-dashboard-save" title={this.t('filters:dashboard:save')}>
                <MenuItem onClick={e => this.saveForCurrentUser()}>{this.t('filters:dashboard:saveForAdmin')}</MenuItem>
                <MenuItem onClick={e => this.saveUnassigned()}>{this.t('filters:dashboard:saveUnassigned')}</MenuItem>
              </DropdownButton>
          }
          {user.loggedIn && !!dashboards.length &&
              <DropdownButton dropup id="dashboard-load-dropdown" title={this.t('filters:dashboard:load')}>
                {dashboards.map(({name, formUrlEncodedBody}, index) =>
                    <MenuItem key={index} onClick={e => this.updateFilters(fromJSON(formUrlEncodedBody))}>
                      {name}
                    </MenuItem>
                )}
              </DropdownButton>
          }

          {user.loggedIn && !user.isAdmin && <p className="dashboard-hint">
            {this.t('filters:dashboard:defaultHint')}
            &nbsp;
            <a href={`/account?id=${user.id}`}>{this.t('filters:dashboard:profileSettings')}</a>
          </p>}

          {user.loggedIn && user.isAdmin && <p className="dashboard-hint">
            <a href="/listAllDashboards">{this.t('filters:dashboard:manageDashboards')}</a>
            &nbsp;|&nbsp;
            <a href="/listusers">{this.t('filters:dashboard:manageUsers')}</a>
          </p>}
        </section>
      </div>
    </div>
  }
}

Filters.TABS = [Organizations, ProcurementRules, Amounts];

export default Filters;
