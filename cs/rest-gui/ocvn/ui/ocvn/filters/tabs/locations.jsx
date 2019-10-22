import Tab from "../../../oce/filters/tabs";
import Locations from '../locations';
import {Set} from "immutable";

class LocationsTab extends Tab{
  render(){
    return <div>
      {this.renderChild(Locations, 'tenderLoc')}
    </div>
  }
}

LocationsTab.getName = t => t('filters:tabs:locations:title');

export default LocationsTab;