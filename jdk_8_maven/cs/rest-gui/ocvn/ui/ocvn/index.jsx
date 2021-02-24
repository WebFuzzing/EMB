import ReactDOM from "react-dom";
import OCApp from "../oce";
import OCVNOverviewTab from './tabs/overview';
import OCVNLocation from "./tabs/location";
import OCVNCompetitiveness from './tabs/competitiveness';
import OCVNEfficiency from './tabs/efficiency';
import OCVNEProcurement from './tabs/e-procurement';
import {fetchJson} from "../oce/tools";
import {Map} from "immutable";
import OCVNFilters from "./filters";
import styles from "./style.less";
import ViewSwitcher from "../oce/switcher.jsx";
/* import CorruptionRickDashboard from "./oce/corruption-risk";*/

class OCVN extends OCApp{
  constructor(props) {
    super(props);
    this.registerTab(OCVNOverviewTab);
    this.registerTab(OCVNLocation);
    this.registerTab(OCVNCompetitiveness);
    this.registerTab(OCVNEfficiency);
    this.registerTab(OCVNEProcurement);
  }

  fetchBidTypes(){
    fetchJson('/api/ocds/bidType/all').then(data =>
      this.setState({
        bidTypes: data.reduce((map, datum) =>
          map.set(datum.id, datum.description), Map())
      })
    );
  }

  render(){
    return (
      <div className="container-fluid dashboard-default" onClick={_ => this.setState({menuBox: ""})}>
        <header className="branding row">
          <div className="logo-container col-sm-4">
            {/*this.dashboardSwitcher()*/}
            <h1>{this.t('general:title')}
              <small>{this.t('general:subtitle')}</small>
            </h1>
          </div>
          <div className="col-sm-5 menu">
            {this.filters()}
            {this.comparison()}
            {this.exportBtn()}
          </div>
          <div className="col-sm-2 header-icons user-tools">
            {this.loginBox()}
          </div>
          <div className="col-sm-1 header-icons language-switcher">
            {this.languageSwitcher()}
          </div>
        </header>
        <aside className="col-xs-4 col-md-3 col-lg-2">
          <div className="row">
            <div role="navigation">
              {this.navigation()}
            </div>
            <section className="col-sm-12 description">
              <h3><strong>{this.t('general:description:title')}</strong></h3>
              <p>
                <small>
                  {this.t('general:description:content')}
                </small>
              </p>
            </section>
            <section className="col-sm-12 github">
              <a href="https://github.com/devgateway/ocvn" target="_blank">
                <button className="btn btn-default btn-block">
                  <img src="/ui/assets/icons/octocat.png" width={16} height={16}/>
                  &nbsp;
                  {this.t("general:viewOnGithub")}
                </button>
              </a>
            </section>
          </div>
        </aside>
        <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10">
          <div className="row">
            {this.content()}
          </div>
        </div>
        {this.showMonths() && <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10 months-bar" role="navigation">
          {this.monthsBar()}
        </div>}
        <div className="col-xs-offset-4 col-md-offset-3 col-lg-offset-2 col-xs-8 col-md-9 col-lg-10 years-bar" role="navigation">
          {this.yearsBar()}
        </div>
        <footer className="col-sm-12 main-footer">&nbsp;</footer>
      </div>
    );
  }
}

OCVN.Filters = OCVNFilters;

const translations = {
  en_US: Object.assign({},
    require('../../web/public/languages/en_US.json'),
    require('../../web/public/languages/en_US_ocvn.json')
  ),
  vn_VN: require('../../web/public/languages/vn_VN.json'),
};

OCVN.TRANSLATIONS = translations;

const BILLION = 1000000000;
const MILLION = 1000000;
const THOUSAND = 1000;
const formatNumber = number => number.toLocaleString(undefined, {maximumFractionDigits: 2});

const styling = {
  charts: {
    axisLabelColor: "#cc3c3b",
    traceColors: ['#137acb', '#ffcc00', '#69bd48'],
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

OCVN.STYLING = styling;
OCVN.TRANSLATIONS = translations;
OCVN.COMPARISON_TYPES.push({
  value: 'bidSelectionMethod',
  label: 'header:comparison:criteria:bidSelectionMethod'
});

ReactDOM.render(<OCVN/>, document.getElementById('dg-container'));

/* class OceSwitcher extends ViewSwitcher{}
 * 
 * OceSwitcher.views.default = OCVN;
 * OceSwitcher.views.corruptionRiskDashboard = CorruptionRickDashboard;
 * 
 * ReactDOM.render(<OceSwitcher
 *                     translations={translations['en_US']}
 *                     styling={styling}
 *                 />, document.getElementById('dg-container'));*/

if("ocvn.developmentgateway.org" == location.hostname){
  (function(b,o,i,l,e,r){b.GoogleAnalyticsObject=l;b[l]||(b[l]=
    function(){(b[l].q=b[l].q||[]).push(arguments)});b[l].l=+new Date;
    e=o.createElement(i);r=o.getElementsByTagName(i)[0];
    e.src='//www.google-analytics.com/analytics.js';
    r.parentNode.insertBefore(e,r)}(window,document,'script','ga'));
  ga('create','UA-78202947-1');ga('send','pageview');
}
