import { Location } from "../../server/schema/Location";

// FIXME: These should be Contacts instead -> Location can become simpler again
const location: Location = {
  name: "Paasitorni Congress Hall",
  about: "Paasitorni is the main venue of the event.",
  image: {
    url: "locations/paasitorni.jpg",
  },
  social: {
    homepage: "https://www.paasitorni.fi/",
    googleMaps: "https://goo.gl/maps/v9ZfgLEp6dw",
  },
  country: {
    name: "Finland",
    code: "FI",
  },
  city: "Helsinki",
  address: "Paasivuorenkatu 5 A",
};

export default location;
