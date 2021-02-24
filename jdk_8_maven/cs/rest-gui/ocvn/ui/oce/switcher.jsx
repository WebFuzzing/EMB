import PropTypes from 'prop-types';
import { getRoute, navigate, onNavigation } from './router';

class OCESwitcher extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
      route: getRoute(),
    };

    onNavigation(route => this.setState({ route }));
  }

  render() {
    const { translations, styling } = this.props;
    const { views } = this.constructor;

    let [dashboard, ...route] = this.state.route;
    if (!dashboard) dashboard = Object.keys(views)[0];
    const CurrentView = views[dashboard];

    return (
      <CurrentView
        onSwitch={navigate}
        translations={translations}
        styling={styling}
        route={route}
        navigate={navigate.bind(null, dashboard)}
      />
    );
  }
}

OCESwitcher.propTypes = {
  translations: PropTypes.object.isRequired,
  styling: PropTypes.object.isRequired,
};

OCESwitcher.views = {};

export default OCESwitcher;
