import { Location } from "../../server/schema/Location";

// FIXME: These should be Contacts instead -> Location can become simpler again
const location: Location = {
  name: "Tiivistämö",
  about: "Tiivistämö is the afterparty venue of the event.",
  image: {
    url: "locations/tiivistamo.jpg",
  },
  social: {
    homepage: "https://www.suvilahti.fi/tilavuokraus/tiivistamo",
    googleMaps: "https://goo.gl/maps/RPgXPWgJLn32",
  },
  country: {
    name: "Finland",
    code: "FI",
  },
  city: "Helsinki",
  address: "Kaasutehtaankatu 1",
};

export default location;
