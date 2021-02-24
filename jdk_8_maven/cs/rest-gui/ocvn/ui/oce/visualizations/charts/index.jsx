import Visualization from "../../visualization";
import ReactIgnore from "../../react-ignore";
import {max} from "../../tools";
import {Map} from "immutable";
import cn from "classnames";
import styles from "./index.less";
import Plotly from "plotly.js/lib/core";
Plotly.register([
  require('plotly.js/lib/bar')
]);

class Chart extends Visualization{
  getData(){
    return super.getData();
  }

  getDecoratedLayout(){
    const {title, xAxisRange, yAxisRange, styling, width, height, margin, legend} = this.props;
    const layout = this.getLayout();
    layout.width = width;
    layout.height = height;
    layout.margin = margin || {pad: 20};
    if(title) layout.title = title;
    if(xAxisRange) layout.xaxis.range = xAxisRange;
    if(yAxisRange) layout.yaxis.range = yAxisRange;
    if(styling){
      layout.xaxis.titlefont = {
        color: styling.charts.axisLabelColor
      };

      layout.yaxis.titlefont = {
        color: styling.charts.axisLabelColor
      }
    }
    if("h" == legend){
      layout.legend = layout.legend || {};
      layout.legend.orientation="h";
      layout.legend.y=1.1;
    }
    return layout;
  }

  componentDidMount(){
    super.componentDidMount();
    Plotly.newPlot(this.refs.chartContainer, this.getData(), this.getDecoratedLayout());
  }

  componentWillUnmount(){
    Plotly.Plots.purge(this.refs.chartContainer);
  }

  componentDidUpdate(prevProps){
    super.componentDidUpdate(prevProps);
    if(this.constructor.UPDATABLE_FIELDS.some(prop => prevProps[prop] != this.props[prop]) || this.props.translations != prevProps.translations){
      this.refs.chartContainer.data = this.getData();
      this.refs.chartContainer.layout = this.getDecoratedLayout();
      setTimeout(() => Plotly.redraw(this.refs.chartContainer));
    } else if(['title', 'width', 'xAxisRange', 'yAxisRange'].some(prop => prevProps[prop] != this.props[prop])){
      setTimeout(() => Plotly.relayout(this.refs.chartContainer, this.getDecoratedLayout()));
    }
  }

  hasNoData(){
    return 0 == this.getData().length;
  }

  render(){
    const {loading} = this.state;
    let hasNoData = !loading && this.hasNoData();
    return <div className="chart-container">
      {hasNoData && <div className="message">{this.t('charts:general:noData')}</div>}
      {loading && <div className="message">
        {this.t('general:loading')}<br/>
        <img src="assets/loading-bubbles.svg" alt=""/>
      </div>}
      <ReactIgnore>
        <div ref="chartContainer"/>
      </ReactIgnore>
    </div>
  }
}

Chart.getFillerDatum = seed => Map(seed);

Chart.getMaxField = data => data.flatten().filter((value, key) => value && "year" != key && "month" != key).reduce(max, 0);

Chart.UPDATABLE_FIELDS = ['data'];

Chart.propTypes.styling = React.PropTypes.shape({
  charts: React.PropTypes.shape({
    axisLabelColor: React.PropTypes.string.isRequired,
    traceColors: React.PropTypes.arrayOf(React.PropTypes.string).isRequired
  }).isRequired
}).isRequired;

export default Chart;
