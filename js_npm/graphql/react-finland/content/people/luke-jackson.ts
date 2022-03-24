import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "Luke Jackson",
  about:
    "Frontend developer interested in user interfaces, API architecture and functional programming.",
  image: {
    url: "people/luke.jpg",
  },
  social: {
    homepage: "",
    twitter: "lukejacksonn",
    github: "lukejacksonn",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Great Britain",
      code: "GB",
    },
    city: "London",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
