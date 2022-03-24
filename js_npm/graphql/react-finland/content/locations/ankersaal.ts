import { Location } from "../../server/schema/Location";

const location: Location = {
  name: "Ankersaal",
  about: "Vienna university of economics and business",
  image: {
    url: "locations/ankersaal.jpg",
  },
  social: {
    homepage: "http://www.brotfabrik.wien/en/art-culture/ankersaal.html",
    googleMaps: "https://goo.gl/maps/kaUjxckzHRs25RnY6",
  },
  country: {
    name: "Austria",
    code: "AT",
  },
  city: "Vienna",
  address: "Absberggasse 27",
};

export default location;
