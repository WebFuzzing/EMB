import Location from "../../oce/tabs/location";
import OCVNPlannedLocations from '../visualizations/map/planned-locations';
import OCVNTenderLocations from '../visualizations/map/tender-locations';

class OCVNLocation extends Location{}

OCVNLocation.LAYERS = [OCVNTenderLocations, OCVNPlannedLocations];

export default OCVNLocation;
