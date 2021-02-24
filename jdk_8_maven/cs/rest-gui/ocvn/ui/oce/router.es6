const PREFIX = '#!';
const listeners = [];

export const onNavigation = (listener) => {
  listeners.push(listener);
};

export const getRoute = () => {
  const raw = location.hash.split('/');
  const [maybePrefix, ...params] = raw;
  return maybePrefix === PREFIX ? params : [];
};

export const navigate = (...params) => {
  location.hash = `${PREFIX}/${params.join('/')}`;
  const route = getRoute();
  listeners.forEach(listener => listener(route));
};
