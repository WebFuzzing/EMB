import { Location } from "../../server/schema/Location";
import sponsor from "../sponsors/elisa";

const location: Location = {
  name: "Elisa Appelsiini",
  about:
    "In addition to being a silver sponsor of the event, Elisa is supporting the event by providing space for our workshops",
  image: {
    url: "sponsors/elisa.svg",
  },
  social: sponsor.social,
  country: {
    name: "Finland",
    code: "FI",
  },
  city: "Helsinki",
  address: "Kaarlenkatu 11",
};

export default location;
