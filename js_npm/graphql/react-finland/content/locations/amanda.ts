import { Location } from "../../server/schema/Location";

// FIXME: These should be Contacts instead -> Location can become simpler again
const location: Location = {
  name: "Amanda",
  about: "Paasitorni is the main venue of the event.",
  image: {
    url: "locations/amanda.jpg",
  },
  social: {
    homepage: "https://www.amanda.fi/",
    googleMaps: "https://g.page/cafe-amanda-helsinki",
  },
  country: {
    name: "Finland",
    code: "FI",
  },
  city: "Helsinki",
  address: "Eteläesplanadi 8",
};

export default location;
