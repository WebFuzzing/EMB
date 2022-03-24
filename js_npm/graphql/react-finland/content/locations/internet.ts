import { Location } from "../../server/schema/Location";

// FIXME: These should be Contacts instead -> Location can become simpler again
const location: Location = {
  name: "Internet",
  about: "Online, www, world wide web",
  image: {
    url: "locations/paasitorni.jpg",
  },
  social: {
    homepage: "",
    googleMaps: "",
  },
  country: {
    name: "",
    code: "FI",
  },
  city: "",
  address: "",
};

export default location;
