import { request } from "graphql-request";
import isEqual from "lodash/isEqual";
import * as React from "react";

// TODO: Extract apiUrl to context?
function connect(apiUrl, query, variables = {}, propsToVars = props => props) {
  return function getConnect(
    component
  ): React.ComponentClass<any> & { filename: string, variables: {} } {
    let queryCache = {};

    interface ConnectState {
      data: {};
    }

    class Connect<P = any> extends React.Component<P, ConnectState | void> {
      public static filename: string;
      public static variables: Array<{
        id: string;
        query?: string;
        // TODO: Type better
        mapToCollection?: (result: any) => any;
        mapToOption?: (result: any) => { value: any; label: any };
        validation?: { type: any; values?: any; default: any };
      }>;
      public state: ConnectState = {
        data: {},
      };
      constructor(props) {
        super(props);

        this.state = { data: queryCache };
      }
      public componentDidMount() {
        this.fetchData().then(data => this.setState(() => data));
      }
      public componentDidUpdate(prevProps) {
        if (!isEqual(prevProps, this.props)) {
          this.fetchData().then(data => this.setState(() => data));
        }
      }
      public render() {
        if (this.state.data === null) {
          return null;
        }

        return React.createElement(component, {
          ...this.props,
          ...this.state.data,
        });
      }
      public fetchData() {
        if (propsToVars) {
          variables = {
            ...variables,
            ...propsToVars(this.props),
          };
        }

        return request(apiUrl, query, variables)
          .then(data => {
            queryCache = data;

            return { data };
          })
          .catch(err => console.error(err));
      }
    }

    return Connect;
  };
}

export default connect;
