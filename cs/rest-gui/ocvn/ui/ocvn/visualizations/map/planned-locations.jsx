import Map from "./index.jsx";

class PlannedLocations extends Map{
  getData(){
    let data = super.getData();
    if(!data) return [];
    return data
        .groupBy(location => location.getIn(['projectLocation', '_id']))
        .map(locations => locations.reduce((reducedLocation, location) => {
          return {
            "_id": location.getIn(['projectLocation', '_id']),
            "name": location.getIn(['projectLocation', 'description']),
            "amount": reducedLocation.amount + location.get('totalPlannedAmount'),
            "count": reducedLocation.count + location.get('recordsCount'),
            "coords": location.getIn(['projectLocation', 'geometry', 'coordinates']).toJS()
          }
        }, {
          "amount": 0,
          "count": 0
        }))
        .toArray()
  }

  static getLayerName(t){return t('map:plannedLocations:title')}
}

PlannedLocations.endpoint = 'plannedFundingByLocation';

export default PlannedLocations;
