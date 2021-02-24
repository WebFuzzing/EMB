import ReactDOM from 'react-dom';
import { Popup } from 'react-leaflet';
import cn from 'classnames';
import Marker from '../location/marker';
import Component from '../../../pure-render-component';
import translatable from '../../../translatable';
import OverviewChart from '../../../visualizations/charts/overview';
import CostEffectiveness from '../../../visualizations/charts/cost-effectiveness';
import { cacheFn, download } from '../../../tools';
import ProcurementMethodChart from '../../../visualizations/charts/procurement-method';
// eslint-disable-next-line no-unused-vars
import style from './style.less';

class LocationWrapper extends translatable(Component) {
  constructor(props) {
    super(props);
    this.state = {
      currentTab: 0,
    };
  }

  render() {
    const { currentTab } = this.state;
    const { data, translations, filters, years, styling, monthly, months } = this.props;
    const CurrentTab = this.constructor.TABS[currentTab];
    const t = translationKey => this.t(translationKey);
    return (
      <Marker {...this.props}>
        <Popup className="tender-locations-popup">
          <div>
            <header>
              {data.name}
            </header>
            <div className="row">
              <div className="tabs-bar col-xs-4">
                {this.constructor.TABS.map((Tab, index) => (
                  <div
                    key={Tab.getName(t)}
                    className={cn({ active: index === currentTab })}
                    onClick={() => this.setState({ currentTab: index })}
                    role="button"
                    tabIndex={0}
                  >
                    <a href="#">{Tab.getName(t)}</a>
                  </div>
                ))}
              </div>
              <div className="col-xs-8">
                <CurrentTab
                  data={data}
                  translations={translations}
                  filters={filters}
                  years={years}
                  monthly={monthly}
                  months={months}
                  styling={styling}
                />
              </div>
            </div>
          </div>
        </Popup>
      </Marker>
    );
  }
}

class Tab extends translatable(Component) {}

export class OverviewTab extends Tab {
  static getName(t) { return t('maps:tenderLocations:tabs:overview:title'); }

  render() {
    const { data } = this.props;
    const { count, amount } = data;
    return (<div>
      <p>
        <strong>{this.t('maps:tenderLocations:tabs:overview:nrOfTenders')}</strong> {count}
      </p>
      <p>
        <strong>{this.t('maps:tenderLocations:tabs:overview:totalFundingByLocation')}</strong> {amount.toLocaleString()}
      </p>
    </div>);
  }
}

const addTenderDeliveryLocationId = cacheFn(
  (filters, id) => filters.set('tenderLoc', id),
);

export class ChartTab extends Tab {
  constructor(props) {
    super(props);
    this.state = {
      chartData: null,
    };
  }

  static getMargins() {
    return {
      t: 0,
      l: 50,
      r: 50,
      b: 50,
    };
  }

  static getChartClass() { return ''; }

  render() {
    const { filters, styling, years, translations, data, monthly, months } = this.props;
    const decoratedFilters = addTenderDeliveryLocationId(filters, data._id);
    const doExcelExport = () => download({
      ep: this.constructor.Chart.excelEP,
      filters: decoratedFilters,
      years,
      months,
      t: translationKey => this.t(translationKey),
    });
    return (<div className={cn('map-chart', this.constructor.getChartClass())}>
      <this.constructor.Chart
        filters={decoratedFilters}
        styling={styling}
        years={years}
        monthly={monthly}
        months={months}
        translations={translations}
        data={this.state.chartData}
        requestNewData={(_, chartData) => this.setState({ chartData })}
        width={500}
        height={350}
        margin={this.constructor.getMargins()}
        legend="h"
      />
      <div className="chart-toolbar">
        <div
          className="btn btn-default"
          onClick={doExcelExport}
          role="button"
          tabIndex={0}
        >
          <img
            src="assets/icons/export-black.svg"
            alt="Export"
            width="16"
            height="16"
          />
        </div>

        <div
          className="btn btn-default"
          onClick={() => ReactDOM.findDOMNode(this).querySelector('.modebar-btn:first-child').click()}
          role="button"
          tabIndex={0}
        >
          <img src="assets/icons/camera.svg" alt="Screenshot" />
        </div>
      </div>
    </div>);
  }
}

export class OverviewChartTab extends ChartTab {
  static getName(t) { return t('charts:overview:title'); }

  static getChartClass() { return 'overview'; }
}

OverviewChartTab.Chart = OverviewChart;

export class CostEffectivenessTab extends ChartTab {
  static getName(t) { return t('charts:costEffectiveness:title'); }
}

CostEffectivenessTab.Chart = CostEffectiveness;

export class ProcurementMethodTab extends ChartTab {
  static getName(t) { return t('charts:procurementMethod:title'); }

  static getMargins() {
    const margins = super.getMargins();
    margins.r = 100;
    margins.b = 100;
    return margins;
  }
}

ProcurementMethodTab.Chart = ProcurementMethodChart;

LocationWrapper.TABS = [OverviewTab, OverviewChartTab, CostEffectivenessTab, ProcurementMethodTab];

export default LocationWrapper;
