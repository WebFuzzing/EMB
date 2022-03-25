import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Ryan Carniato",
  about:
    "JavaScript performance enthusiast and fine-grained reactivity super fan. Works on @MarkoDevTeam at eBay. Author of the @solid_js.",
  image: {
    url: "people/ryan.jpg",
  },
  social: {
    homepage: "",
    twitter: "RyanCarniato",
    github: "ryansolid",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "United States",
      code: "US",
    },
    city: "San Jose",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
