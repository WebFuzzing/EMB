import { Contact, ContactType } from "../../server/schema/Contact";
import Keyword from "../../server/schema/keywords";

const person: Contact = {
  name: "François Wouts",
  about: "🚀 Developer Happiness Engineer",
  image: {
    url: "people/francois.jpg",
  },
  social: {
    homepage: "https://fwouts.com/",
    twitter: "fwouts",
    github: "fwouts",
    facebook: "",
    medium: "",
    instagram: "",
    linkedin: "fwouts",
    youtube: "",
    devto: "",
  },
  location: {
    country: {
      name: "Australia",
      code: "AU",
    },
    city: "Sydney",
  },
  keywords: [Keyword.REACT],
  type: [ContactType.SPEAKER],
};

export default person;
