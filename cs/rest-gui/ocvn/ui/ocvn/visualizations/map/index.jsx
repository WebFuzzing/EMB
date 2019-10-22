import Map from "../../../oce/visualizations/map";
import {TileLayer} from "react-leaflet";

export let injectOceans = MapClass => class OceansMap extends MapClass{
    getTiles(){
        return (
            <TileLayer
              url='//server.arcgisonline.com/ArcGIS/rest/services/Ocean_Basemap/MapServer/tile/{z}/{y}/{x}'
              attribution='Tiles &copy; Esri &mdash; Sources: GEBCO, NOAA, CHS, OSU, UNH, CSUMB, National Geographic, DeLorme, NAVTEQ, and Esri'
            />
        )
    }
}

class OCVNMap extends injectOceans(Map){}

export default OCVNMap;
