import URI from 'urijs';
import { fromJS } from 'immutable';
import translatable from '../../translatable';
import Component from '../../pure-render-component';
import { fetchJson, shallowCopy } from '../../tools';
import orgNamesFetching from '../../orgnames-fetching';

class TypeAhead extends orgNamesFetching(translatable(Component)) {
  constructor(props) {
    super(props);
    this.state.query = '';
    this.state.options = fromJS([]);
  }

  updateQuery(query) {
    this.setState({ query });
    if (query.length >= this.constructor.MIN_QUERY_LENGTH) {
      fetchJson(new URI(this.constructor.ENDPOINT).addSearch('text', query).toString())
        .then(data => this.setState({ options: fromJS(data) }));
    } else {
      this.setState({ options: fromJS([]) });
    }
  }

  /* Marks an option as selected */
  select(option) {
    const id = option.get('id');
    const name = option.get('name');
    const orgNames = shallowCopy(this.state.orgNames);
    orgNames[id] = name;
    this.setState({ orgNames });
    this.props.onToggle(id);
  }

  static renderOption({ id, name, checked, cb }) {
    return (
      <div className="checkbox" key={id}>
        <label>
          <input
            type="checkbox"
            checked={checked}
            onChange={cb}
          /> {name}
        </label>
      </div>
    );
  }

  getOrgsWithoutNamesIds() {
    return this.props.selected.filter(id => !this.state.orgNames[id]).toJS();
  }

  render() {
    const { query, options, orgNames } = this.state;
    const { selected, onToggle } = this.props;
    const haveQuery = query.length >= this.constructor.MIN_QUERY_LENGTH;
    return (
      <section className="field type-ahead">
        <header>{this.getTitle()} ({selected.count()})</header>
        <section className="options">
          {selected.map(id => this.constructor.renderOption({
            id,
            name: orgNames[id],
            checked: true,
            cb: () => onToggle(id),
          }))}

          <input
            type="text"
            className="input-sm form-control search"
            placeholder={this.t('filters:typeAhead:inputPlaceholder')}
            value={query}
            onChange={e => this.updateQuery(e.target.value)}
          />

          {haveQuery && <div className="result-count">{this.t_n('filters:typeAhead:result:sg', 'filters:typeAhead:result:pl', options.count())}</div>}

          {options.filter(option => !selected.has(option.get('id'))).map(option => this.constructor.renderOption({
            id: option.get('id'),
            name: option.get('name'),
            checked: false,
            cb: () => this.select(option),
          }))}
        </section>
      </section>
    );
  }
}

TypeAhead.MIN_QUERY_LENGTH = 3;

export default TypeAhead;
