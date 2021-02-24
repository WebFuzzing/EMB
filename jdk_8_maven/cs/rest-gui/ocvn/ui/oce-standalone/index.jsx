import ReactDOM from "react-dom";
import OCApp from "../oce";
import OverviewTab from '../oce/tabs/overview';
import LocationTab from '../oce/tabs/location';
import CompetitivenessTab from '../oce/tabs/competitiveness';
import EfficiencyTab from '../oce/tabs/efficiency';
import EProcurementTab from '../oce/tabs/e-procurement';
import {fetchJson} from "../oce/tools";
import {Map} from "immutable";
import styles from "./style.less";
import ViewSwitcher from "../oce/switcher.jsx";
import CorruptionRickDashboard from "../oce/corruption-risk";
import cn from "classnames";

class OCEDemoLocation extends LocationTab{
  getHeight(){
    const TOP_OFFSET = 128;
    const BOTTOM_OFFSET = 66;
    return window.innerHeight - TOP_OFFSET - BOTTOM_OFFSET;
  }
}
OCEDemoLocation.CENTER = [37, -100];

class OCEChild extends OCApp{
  constructor(props) {
    super(props);
    this.registerTab(OverviewTab);
    this.registerTab(OCEDemoLocation);
    this.registerTab(CompetitivenessTab);
    this.registerTab(EfficiencyTab);
    this.registerTab(EProcurementTab);
  }

  fetchBidTypes(){
    fetchJson('/api/ocds/bidType/all').then(data =>
      this.setState({
        bidTypes: data.reduce((map, datum) =>
          map.set(datum.id, datum.description), Map())
      })
    );
  }

  loginBox(){
    let linkUrl, text;
    if(this.state.user.loggedIn){
      linkUrl = "/preLogout?referrer=/ui/index.html"
      text = this.t("general:logout");
    } else {
      linkUrl = "/login?referrer=/ui/index.html";
      text = this.t("general:login");
    }
    return <a href={linkUrl} className="login-logout">
      <button className="btn btn-default">
        {text}
      </button>
    </a>
  }

  dashboardSwitcher(){
    const {dashboardSwitcherOpen} = this.state;
    const {onSwitch} = this.props;
    return (
      <div className={cn('dash-switcher-wrapper', {open: dashboardSwitcherOpen})}>
        <h1 onClick={this.toggleDashboardSwitcher.bind(this)}>
          <strong>Monitoring & Evaluation</strong> Toolkit
          <i className="glyphicon glyphicon-menu-down"/>
        </h1>
        {dashboardSwitcherOpen &&
          <div className="dashboard-switcher">
            <a href="javascript:void(0);" onClick={e => onSwitch('crd')}>
              Corruption Risk Dashboard
            </a>
          </div>
        }
      </div>
    )
  }

  exportBtn(){
    if(this.state.exporting){
      return (
        <div className="export-progress">
          <div className="progress">
            <div className="progress-bar progress-bar-danger" role="progressbar" style={{width: "100%"}}>
              {this.t('export:exporting')}
            </div>
          </div>
        </div>
      )
    }
    return (
      <div className="export-btn">
        <button className="btn btn-default" disabled>
          <i className="glyphicon glyphicon-download-alt"></i>
        </button>
      </div>
    )
  }

  navigationLink(Tab, index){
    if(OverviewTab != Tab) return super.navigationLink(Tab, index);
    const {getName, icon} = Tab;
    return (
      <div
        className={cn('navigation-item-overview', {active: index == this.state.currentTab})}
        onClick={_ => this.setState({currentTab: index})}
      >
        <a href="javascript:void(0);" key={index} className="col-sm-12">
          <span className="circle">
            <img className="nav-icon" src={`assets/icons/${icon}.svg`}/>
            <i className={`glyphicon glyphicon-${icon}`}/>
          </span>
    	  &nbsp;
		  {getName(this.t.bind(this))}
      <i className="glyphicon glyphicon-info-sign"/>
        </a>
        <div className="description col-sm-12">
          The Procurement M&E Prototype is an interactive platform for analyzing, monitoring, and evaluating information on public procurement. It is specifically designed to help users understand procurement efficiency, and the competitiveness and cost-effectiveness of public markets.
        </div>
      </div>
    )
  }

  render(){
    return (
      <div className="container-fluid dashboard-default" onClick={_ => this.setState({menuBox: ""})}>
        <header className="branding row">
          <div className="col-sm-9 logo-wrapper">
            <img src="assets/dg-logo.svg"/>
            {this.dashboardSwitcher()}
          </div>
          <div className="col-sm-3">
            {this.loginBox()}
          </div>
        </header>
        <div className="header-tools row">
          <div className="col-xs-offset-4 col-md-offset-3 col-sm-5 menu">
            <div className="filters-hint">
              Filter your data
            </div>
            {this.filters()}
            {this.comparison()}
          </div>
          <div className="col-xs-3 col-md-4">
            {this.exportBtn()}
          </div>
        </div>
        <aside className="col-xs-4 col-md-3">
          <div className="row">
            <div role="navigation">
              {this.navigation()}
            </div>
            {/*
                <section className="col-sm-12 description">
                <h3><strong>{this.t('general:description:title')}</strong></h3>
                <p>
                <small>
                {this.t('general:description:content')}
                </small>
                </p>
                </section>
              */}
          </div>
        </aside>
        <div className="col-xs-offset-4 col-md-offset-3 col-xs-8 col-md-9">
          <div className="row">
            {this.content()}
          </div>
        </div>
        {this.showMonths() && <div className="col-xs-offset-4 col-md-offset-3 col-xs-8 col-md-9 months-bar" role="navigation">
          {this.monthsBar()}
        </div>}
        <div className="col-xs-offset-4 col-md-offset-3 col-xs-8 col-md-9 years-bar" role="navigation">
          {this.yearsBar()}
        </div>
        <footer className="col-sm-12 main-footer">&nbsp;</footer>
      </div>
    );
  }
}

const translations = {
  en_US: require('../../web/public/languages/en_US.json')
};

const BILLION = 1000000000;
const MILLION = 1000000;
const THOUSAND = 1000;
const formatNumber = number => number.toLocaleString(undefined, {maximumFractionDigits: 2});

const styling = {
  charts: {
    axisLabelColor: "#cc3c3b",
    traceColors: ['#324d6e', '#ecac00', '#4b6f33'],
    hoverFormat: ',.2f',
    hoverFormatter: number => {
      if(typeof number == "undefined") return number;
      let abs = Math.abs(number);
      if(abs >= BILLION) return formatNumber(number/BILLION) + "B";
      if(abs >= MILLION) return formatNumber(number/MILLION) + "M";
      if(abs >= THOUSAND) return formatNumber(number/THOUSAND) + "K";
      return formatNumber(number);
    }
  },
  tables: {
    currencyFormatter: formatNumber
  }
};

OCEChild.STYLING = styling;
OCEChild.TRANSLATIONS = translations;

CorruptionRickDashboard.STYLING = JSON.parse(JSON.stringify(styling));

CorruptionRickDashboard.STYLING.charts.traceColors = ["#234e6d", "#3f7499", "#80b1d3", "#afd5ee", "#d9effd"];

class OceSwitcher extends ViewSwitcher{}

OceSwitcher.views['m-and-e'] = OCEChild;
OceSwitcher.views.crd = CorruptionRickDashboard;

ReactDOM.render(<OceSwitcher
                  translations={translations['en_US']}
                  styling={styling}
                />, document.getElementById('dg-container'));

if("ocvn.developmentgateway.org" == location.hostname){
  (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
    function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
    e=o.createElement(i);r=o.getElementsByTagName(i)[0];
    e.src='//www.google-analytics.com/analytics.js';
    r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
  ga('create','UA-78202947-1');ga('send','pageview');
}
