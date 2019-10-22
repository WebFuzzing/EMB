import translatable from "../../translatable";
import {Set} from "immutable";

class Tab extends translatable(React.Component){
  renderChild(Component, slug){
    let {onUpdate, translations, state} = this.props;
    let selected = state.get(slug, Set());
    return <Component
        key={slug}
        selected={selected}
        onToggle={id => onUpdate(slug, selected.has(id) ?
            selected.delete(id) :
            selected.add(id))
        }
        onUpdateAll={onUpdate.bind(null, slug)}
        translations={translations}
    />
  }
}

export default Tab;