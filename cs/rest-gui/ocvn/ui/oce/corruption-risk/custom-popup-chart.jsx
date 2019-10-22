import Chart from "../visualizations/charts/frontend-date-filterable";
import ReactIgnore from "../react-ignore.jsx";
import cn from "classnames";
import {POPUP_HEIGHT, POPUP_WIDTH} from "./constants";

class CustomPopupChart extends Chart{
  constructor(...args){
    super(...args);
    this.state = {
      popup: {
        show: false,
        left: 0,
        top: 0
      }
    }
  }

  componentDidMount(){
    super.componentDidMount();
    const {chartContainer} = this.refs;
    chartContainer.on('plotly_hover', this.showPopup.bind(this));
    chartContainer.on('plotly_unhover', data => this.hidePopup());
  }

  showPopup(data){
    const point = data.points[0];
    const year = point.x;
    const traceName = point.data.name;
    const POPUP_ARROW_SIZE = 8;

    const {xaxis, yaxis} = point;
    const markerLeft = xaxis.l2p(point.pointNumber) + xaxis._offset;
    const markerTop = yaxis.l2p(point.y) + yaxis._offset;
    const {left: parentLeft} = this.refs.chartContainer.getBoundingClientRect();
    const toTheLeft = (markerLeft + parentLeft + POPUP_WIDTH) >= window.innerWidth;
    let top, left;

    if(toTheLeft){
      top = markerTop - POPUP_HEIGHT / 2;
      left = markerLeft - POPUP_WIDTH - POPUP_ARROW_SIZE * 1.5;
    } else {
      top = markerTop - POPUP_HEIGHT - POPUP_ARROW_SIZE * 1.5;
      left = markerLeft - POPUP_WIDTH / 2;
    }

    this.setState({
      popup: {
        show: true,
        toTheLeft,
        top,
        left,
        year,
        traceName
      }
    });
  }

  hidePopup(){
    this.setState({
      popup: {
        show: false
      }
    });
  }

  render(){
    const {loading, popup} = this.state;
    let hasNoData = !loading && this.hasNoData();
    return (
      <div className={cn("chart-container", {"popup-left": popup.toTheLeft})}>
    	  {hasNoData && <div className="message">{this.t('charts:general:noData')}</div>}
	      {loading && <div className="message">
  	      Loading...<br/>
    	    <img src="assets/loading-bubbles.svg" alt=""/>
	      </div>}

  	    {popup.show && this.getPopup()}

	      <ReactIgnore>
  	      <div ref="chartContainer"/>
	      </ReactIgnore>
      </div>
    )
  }
}

export default CustomPopupChart;
