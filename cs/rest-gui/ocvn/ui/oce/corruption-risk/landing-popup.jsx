import {LOGIN_URL} from "./constants";
import {debounce} from "../tools";

class LandingPopup extends React.Component{
  constructor(...args){
    super(...args);
    this.state = {
      top: 0
    }
  }

  onClose(){
    const {redirectToLogin, requestClosing} = this.props;
    if(redirectToLogin){
      location.href = LOGIN_URL;
    } else {
      requestClosing();
    }
  }

  recalcTop(){
    this.setState({
      top: (window.innerHeight - this.refs.thePopup.offsetHeight) / 2
    })
  }

  componentDidMount(){
    this.recalcTop();
    this.windowResizeListener = debounce(this.recalcTop.bind(this));
    window.addEventListener("resize", this.windowResizeListener);
  }

  componentWillUnmount(){
    window.removeEventListener("resize", this.windowResizeListener);
  }

  render(){
    const {top} = this.state;
    return (
      <div>
        <div className="crd-landing-popup-overlay" onClick={this.onClose.bind(this)}/>

        <div className="crd-landing-popup" ref="thePopup" style={{top}}>
          <div className="container-fluid">
            <div className="row">
              <div className="col-sm-1 text-right">
                <img src="assets/logo.png"/>
              </div>
              <div className="col-sm-10">
                <h4 className="popup-title">Corruption Risk Dashboard</h4>
              </div>
              <div className="col-ms-1 text-right">
                <i className="glyphicon glyphicon-remove-circle close-button" onClick={this.onClose.bind(this)}></i>
              </div>
            </div>
            <div className="row">
              <div className="col-sm-5 col-sm-offset-1 text-column-left">
                The Corruption Risk Dashboard (CRD) is an open source tool that aims to help users understand the potential presence of corruption in public contracting. Through a red flagging approach (highlighting procurement activities that have certain risk factors), this prototype explores corruption risk through the visualization of 10 indicators that are mapped to three different forms of corruption: fraud, collusion, and process rigging. Users are free to explore the data, which has been converted to the Open Contracting Data Standard (OCDS), using a variety of filters. A crosstab table enables users to explore the overlap between any two indicators that are mapped to the same risk type.
              </div>
              <div className="col-sm-5 end text-column-right">
                The methodological approach that informs the CRD, which was built by Development Gateway (DG) with the collaboration and support of the Open Contracting Partnership (OCP), is presented in a co-authored <a href="http://www.open-contracting.org/resources/red-flags-integrity-giving-green-light-open-data-solutions/">research paper.</a> Explanations of the principal concepts and indicators are available within the Dashboard.
              </div>
              <div className="col-sm-1"/>

              <hr className="col-sm-offset-1 col-sm-10 end separator"/>
              <div className="col-sm-1"/>

            </div>
            <div className="row">
              <div className="col-sm-offset-1 col-sm-11 section-title">
                <h5>
                  Intended Use
                </h5>
              </div>

              <div className="col-sm-11 col-sm-offset-1">
                The CRD was designed with the primary objective of supporting two specific use cases:
              </div>
              <div className="col-sm-5 col-sm-offset-1 text-column-left intended-use-item">
                1. To facilitate the efforts of procurement regulators and informed observers to identify individual contracts that may warrant investigation, and;
              </div>

              <div className="col-sm-5 text-column-right intended-use-item">
                2. To aid these individuals to monitor corruption risk in procurement markets over time.
              </div>
            </div>
            <div className="row">

              <div className="col-sm-1"/>

              <hr className="col-sm-offset-1 col-sm-10 end separator"/>

              <div className="col-sm-offset-1 col-sm-11 section-title">
                <h5>
                  Distinguishing between &ldquo;Corruption&rdquo; and &ldquo;Corruption Risk&rdquo;
                </h5>
              </div>

              <div className="col-sm-5 col-sm-offset-1 text-column-left">
                While flags associated with a procurement process may indicate the possibility that corruption has taken place, they may also be attributable to poor data quality, systemic weaknesses, or practices that may be appropriate when specifically authorized by a procurement authority or regulatory institution. For this reason, this tool is best viewed as a mechanism for identifying the “risk” of corruption, rather than “corruption” itself.
              </div>
              <div className="col-sm-5 end text-column-right">
                Furthermore, in some instances, a single flag - such as for the awarding of two contracts to the same supplier by a single procuring entity - may not show any evidence of wrongdoing, though the confluence of multiple indicators suggests greater cause of concern.
              </div>

              <hr className="col-sm-offset-1 col-sm-10 end separator"/>

              <div className="col-sm-11 col-sm-offset-1 section-title">
                <h5>Your feedback is welcome</h5>
              </div>

              <div className="col-sm-9 col-sm-offset-1 contact">
                We welcome your feedback on this prototype. Please contact Andrew Mandelbaum at DG if you have any questions or suggestions:&nbsp;
                <a href="mailto:amandelbaum@developmentgateway.org">amandelbaum@developmentgateway.org</a>
              </div>

              <div className="col-sm-2 end">
                <button className="btn btn-primary" onClick={this.onClose.bind(this)}>
                  Enter!
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    )
  }
}

export default LandingPopup;
