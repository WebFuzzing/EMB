import frontendDateFilterable from "../frontend-date-filterable";
import { Map, TileLayer } from 'react-leaflet';
import {pluck} from "../../tools";
import Cluster from "./cluster";
import Location from "./location";
import Visualization from "../../visualization";
import style from "./style.less";

class MapVisual extends frontendDateFilterable(Visualization){
  getMaxAmount(){
    return Math.max(0, ...this.getData().map(pluck('amount')));
  }

  getTiles(){
    return (
        <TileLayer
            url='//{s}.tile.osm.org/{z}/{x}/{y}.png'
            attribution='&copy; <a href="//osm.org/copyright">OpenStreetMap</a> contributors'
        />
    )
  }

  render(){
    let {translations, filters, years, styling, monthly, months, center, zoom} = this.props;
    return <Map center={center} zoom={zoom}>
      {this.getTiles()}
      <Cluster maxAmount={this.getMaxAmount()}>
        {this.getData().map(location => (
            <this.constructor.Location
                key={location._id}
                position={location.coords.reverse()}
                maxAmount={this.getMaxAmount()}
                data={location}
                translations={translations}
                filters={filters}
                years={years}
                monthly={monthly}
                months={months}
                styling={styling}
            />
        ))}
      </Cluster>
    </Map>
  }
}

MapVisual.propTypes = {};
MapVisual.computeComparisonYears = null;
MapVisual.Location = Location;

export default MapVisual;
