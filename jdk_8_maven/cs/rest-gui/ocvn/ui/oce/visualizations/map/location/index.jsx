import Marker from "./marker";
import Component from "../../../pure-render-component";
import {Popup} from "react-leaflet";
import translatable from "../../../translatable";

export default class LocationWrapper extends translatable(Component){
  render(){
    var {amount, name} = this.props.data;
    return (
        <Marker {...this.props}>
          <Popup>
            <div>
              <h3>{name}</h3>
              <p>
                <strong>{this.t('general:amountInVND')}</strong> {amount.toLocaleString()}
              </p>
            </div>
          </Popup>
        </Marker>
    )
  }
}

