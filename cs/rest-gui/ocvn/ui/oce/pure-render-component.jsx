var React = require('react');
function shallowDiff (a,b){
  if(a && b && "object" == typeof a && "object" == typeof b){
    return Object.keys(a).some(key => "function" != typeof a[key] && a[key] != b[key]);
  } else {
    return a != b;
  }
}

export default class PureRenderComponent extends React.Component {
  shouldComponentUpdate (nextProps, nextState){
    let shouldUpdate = shallowDiff(this.props, nextProps) || shallowDiff(this.state, nextState);
    if(shouldUpdate){
      // console.log(Object.keys(this.props).filter(key => this.props[key] != nextProps[key]));
    }
    return shouldUpdate;
  }
};