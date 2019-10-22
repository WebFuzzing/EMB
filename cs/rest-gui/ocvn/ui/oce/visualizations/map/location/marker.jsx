import {Marker} from "react-leaflet";

export default class Location extends Marker{
  componentDidMount(){
    super.componentDidMount();
    this.leafletElement.on('popupopen', e => {
      let map = this.leafletElement._map;
      let px = map.project(e.popup._latlng); // find the pixel location on the map where the popup anchor is
      px.y -= e.popup._container.clientHeight/2; // find the height of the popup container, divide by 2, subtract from the Y axis of marker location
      map.panTo(map.unproject(px),{animate: true}); // pan to new center
    });
  }

  componentDidUpdate(prevProps){
    super.componentDidUpdate(prevProps);
    if(prevProps.data != this.props.data){
      this.leafletElement.options.data = this.props.data;
    }
  }
}
