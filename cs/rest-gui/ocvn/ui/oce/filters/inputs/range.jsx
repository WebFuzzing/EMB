import translatable from "../../translatable";
import Component from "../../pure-render-component";
import RCRange from 'rc-slider/lib/Range'
import {callFunc} from "../../tools";
import 'rc-slider/assets/index.css';

class FormattedNumberInput extends React.Component{
  getFormattedValue(){
    const {value} = this.props;
    return value && value.toLocaleString();
  }

  sendUnformattedValue(e){
    const value = parseFloat(e.target.value.replace(/,/g, ''));
    this.props.onChange(value);
  }

  render(){
    const {value, onChange} = this.props;
    return (
      <input
        {...this.props}
        value={this.getFormattedValue()}
        onChange={this.sendUnformattedValue.bind(this)}
      />
    )
  }
}

class Range extends translatable(Component){
  render(){
    if(!this.state) return null;
    const {min, max} = this.state;
    const {onUpdate} = this.props;
    const minValue = this.props.minValue || min;
    const maxValue = this.props.maxValue || max;
    return (
      <section className="field">
        <header>
          {this.getTitle()}
        </header>
        <section className="options range">
          <RCRange
            allowCross={false}
            min={min}
            max={max}
            defaultValue={[min, max]}
            value={[minValue, maxValue]}
            onChange={([minValue, maxValue]) => onUpdate({min: minValue, max: maxValue}, {min, max})}
          />
        </section>
        <div className="range-inputs">
          {this.t('general:range:min')}
          &nbsp;
          <FormattedNumberInput
            className="form-control input-sm"
            value={minValue}
            onChange={value => onUpdate({min: value, max: maxValue}, {min, max})}
          />
        &nbsp;
      {this.t('general:range:max')}
        &nbsp;
      <FormattedNumberInput
        className="form-control input-sm"
        value={maxValue}
        onChange={value => onUpdate({min: minValue, max: value}, {min, max})}
      />
        </div>
      </section>
    )
  }
}

export default Range;
