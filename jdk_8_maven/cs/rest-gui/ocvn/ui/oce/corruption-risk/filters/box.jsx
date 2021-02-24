import FilterTab from "../../filters/tabs";
import cn from "classnames";

class FilterBox extends FilterTab{
  isActive(){
    console.warn(`Implement an "isActive" method for ${this.getTitle()}`);
  }

  reset(){
    console.warn(`Implement an "reset" method for ${this.getTitle()}`);
  }

  render(){
    const {open, onClick, onApply, state} = this.props;
    return(
      <div onClick={onClick} className={cn('filter', {open, active: this.isActive()})}>
        <span className="box-title">
          {this.getTitle()}
        </span>
        <i className="glyphicon glyphicon-menu-down"></i>
        {open &&
         <div className="dropdown" onClick={e => e.stopPropagation()}>
           {this.getBox()}
           <div className="controls">
             <button className="btn btn-primary" onClick={e => onApply(state)}>Apply</button>
             &nbsp;
             <button className="btn btn-default" onClick={this.reset.bind(this)}>Reset</button>
           </div>
         </div>
        }
      </div>
    )
  }
}

export default FilterBox;
