import {LayerGroup} from "react-leaflet";
import {toK} from "../../tools";
require("leaflet.markercluster");
require("leaflet.markercluster/dist/MarkerCluster.css");
require("leaflet.markercluster/dist/MarkerCluster.Default.css");

function clusterIcon(cluster, maxAmount){
  var {count, amount} = cluster.getAllChildMarkers().reduce((sum, marker) => {
    var data = marker.options.data;
    return {
      amount: sum.amount + data.amount,
      count: sum.count + data.count
    }
  }, {
    amount: 0,
    count: 0
  });

  const amountRatio = maxAmount != 0 ? amount / maxAmount : 0;
  const green = Math.round(128 * (1 - amountRatio));
  const shadow = 1 != cluster.getChildCount() ? `box-shadow: 0 0 5px 5px rgb(255, ${green}, 0)` : "";
  return L.divIcon({
    html: `
      <div style="background-color: rgba(255, ${green}, 0, .8);${shadow}">
        <span>
          ${toK(count)}
        </span>
      </div>
    `,
    className: 'marker-cluster'
  });
}

class Cluster extends LayerGroup {
  componentWillMount() {
    super.componentWillMount();
    this.leafletElement = L.markerClusterGroup({
      showCoverageOnHover: false,
      singleMarkerMode: true,
      iconCreateFunction: cluster => clusterIcon(cluster, this.props.maxAmount)
    });
  }

  componentDidUpdate() {
    this.leafletElement.refreshClusters();
  }
}

Cluster.propTypes = {
  maxAmount: React.PropTypes.number.isRequired
};

export default Cluster;
